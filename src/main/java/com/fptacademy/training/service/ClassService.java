package com.fptacademy.training.service;

import com.fptacademy.training.domain.*;
import com.fptacademy.training.domain.Class;
import com.fptacademy.training.domain.enumeration.ClassStatus;
import com.fptacademy.training.exception.ResourceAlreadyExistsException;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.exception.ResourceNotFoundException;
import com.fptacademy.training.repository.*;
import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.service.dto.*;
import com.fptacademy.training.service.mapper.*;
import com.fptacademy.training.web.vm.ClassVM;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class ClassService {
    private final ClassRepository classRepository;
    private final ClassDetailRepository classDetailRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final ProgramRepository programRepository;
    private final AttendeeRepository attendeeRepository;
    private final LocationRepository locationRepository;
    private final ClassMapper classMapper;
    private final ClassDetailMapper classDetailMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AttendeeMapper attendeeMapper;
    private final LocationMapper locationMapper;

    @PostFilter("(hasAnyAuthority('" + Permissions.CLASS_VIEW + "') " +
            "and filterObject.status != 'DRAFT' and filterObject.status != 'INACTIVE') or " +
            "(hasAnyAuthority('" +
            Permissions.CLASS_CREATE + "', '" +
            Permissions.CLASS_MODIFY + "', '" +
            Permissions.CLASS_FULL_ACCESS  + "'))")
    public List<ClassDto> filterClass(List<String> keywords,
                            LocalDate from,
                            LocalDate to,
                            List<String> cities,
                            List<String> classTimes,
                            List<String> statuses,
                            List<String> attendeeTypes,
                            String fsu,
                            String trainerCode,
                            String sort) {
        //search by keyword
        List<Class> classes;
        if (keywords != null) {
            List<Class> firstFilteredClasses = classRepository.findAll();
            if (keywords.size() >= 1) {
                classes = firstFilteredClasses
                        .stream()
                        .filter(c -> keywords
                                .stream()
                                .allMatch(e -> c.getName().toLowerCase().contains(e.toLowerCase()) ||
                                        c.getCode().toLowerCase().contains(e.toLowerCase()) ||
                                        c.getCreatedAt().toString().toLowerCase().contains(e.toLowerCase()) ||
                                        c.getCreatedBy().getCode().toLowerCase().contains(e.toLowerCase()) ||
                                        String.valueOf(c.getDuration()).equals(e.toLowerCase()) ||
                                        c.getClassDetail().getAttendee().getType().toLowerCase().contains(e.toLowerCase()) ||
                                        c.getClassDetail().getLocation().getCity().toLowerCase().contains(e.toLowerCase()) ||
                                        c.getClassDetail().getLocation().getFsu().toLowerCase().contains(e.toLowerCase())
                                        ))
                        .toList();
            } else {
                classes = firstFilteredClasses;
            }
        }
        else classes = classRepository.findAll();

        //apply filter
        classes = classes.stream()
                .filter(c -> !c.getClassDetail().getStatus().equals("DELETED"))
                .filter(c -> {
            LocalDate minStudyDate =
                    Collections.min(c.getClassDetail().getSchedules().stream().map(ClassSchedule::getStudyDate).toList());
            LocalDate maxStudyDate =
                    Collections.min(c.getClassDetail().getSchedules().stream().map(ClassSchedule::getStudyDate).toList());
            LocalDate fromDate = from == null ? LocalDate.MIN : from;
            LocalDate toDate = to == null ? LocalDate.MAX : to;
            return (minStudyDate.isEqual(toDate) || minStudyDate.isBefore(toDate)) &&
                (maxStudyDate.isEqual(fromDate) || maxStudyDate.isAfter(fromDate));
        })
                .filter(c -> cities == null || cities.stream().anyMatch(ci -> c.getClassDetail().getLocation().getCity().equalsIgnoreCase(ci)))
                .filter(c -> {
                    if (classTimes == null) return true;
                    if (c.getClassDetail().getStartAt().compareTo(LocalTime.of(8,0)) >= 0 &&
                            c.getClassDetail().getFinishAt().compareTo(LocalTime.of(12,0)) <= 0)
                        return classTimes.stream().anyMatch(ct -> ct.equalsIgnoreCase("Morning"));
                    if (c.getClassDetail().getStartAt().compareTo(LocalTime.of(13,0)) >= 0 &&
                            c.getClassDetail().getFinishAt().compareTo(LocalTime.of(17,0)) <= 0)
                        return classTimes.stream().anyMatch(ct -> ct.equalsIgnoreCase("Noon"));
                    if (c.getClassDetail().getStartAt().compareTo(LocalTime.of(18,0)) >= 0 &&
                            c.getClassDetail().getFinishAt().compareTo(LocalTime.of(22,0)) <= 0)
                        return classTimes.stream().anyMatch(ct -> ct.equalsIgnoreCase("Night"));
                    return false;
                })
                .filter(c -> statuses == null || statuses.stream().anyMatch(sta -> c.getClassDetail().getStatus().equalsIgnoreCase(sta)))
                .filter(c -> attendeeTypes == null || attendeeTypes.stream().anyMatch(at -> c.getClassDetail().getAttendee().getType().equalsIgnoreCase(at)))
                .filter(c -> fsu == null || c.getClassDetail().getLocation().getFsu().equals(fsu))
                .filter(c -> trainerCode == null ||
                c.getClassDetail().getUsers().stream().filter(u -> u.getRole().getName().equals("Trainer"))
                        .anyMatch(tr -> tr.getCode().equalsIgnoreCase(trainerCode)))
                .collect(Collectors.toList());
        List<ClassDto> classDtos = new ArrayList<>(classMapper.toDtos(classes));

        // Sort the list
        String[] sorts = sort.split(",");
        if (sorts.length != 2) {
            throw new ResourceBadRequestException("Invalid parameter for sort");
        }
        String property = sorts[0];
        String direction = sorts[1];
        if (!direction.equals("asc") && !direction.equals("desc")) {
            throw new ResourceBadRequestException("Invalid parameter for sort, cannot find sort direction (asc or desc)");
        }
        Comparator<ClassDto> comparator;
        switch (property) {
            case "id" -> comparator = direction.equals("asc") ?
                    Comparator.comparing(ClassDto::getId) :
                    Comparator.comparing(ClassDto::getId).reversed();
            case "name" -> comparator = direction.equals("asc") ?
                    Comparator.comparing(ClassDto::getName) :
                    Comparator.comparing(ClassDto::getName).reversed();
            case "code" -> comparator = direction.equals("asc") ?
                    Comparator.comparing(ClassDto::getCode) :
                    Comparator.comparing(ClassDto::getCode).reversed();
            case "createdOn" -> comparator = direction.equals("asc") ?
                    Comparator.comparing(ClassDto::getCreated_at) :
                    Comparator.comparing(ClassDto::getCreated_at).reversed();
            case "createdBy" -> {
                comparator = Comparator.comparing(c -> c.getCreated_by().getCode());
                if (direction.equals("desc")) comparator = comparator.reversed();
            }
            case "duration" -> comparator = direction.equals("asc") ?
                    Comparator.comparing(ClassDto::getDuration) :
                    Comparator.comparing(ClassDto::getDuration).reversed();
            case "attendee" -> {
                comparator = Comparator.comparing(c -> c.getAttendee().getName());
                if (direction.equals("desc")) comparator = comparator.reversed();
            }
            case "location" -> {
                comparator = Comparator.comparing(c -> c.getLocation_id().getCity());
                if (direction.equals("desc")) comparator = comparator.reversed();
            }
            case "fsu" -> {
                comparator = Comparator.comparing(c -> c.getLocation_id().getFsu());
                if (direction.equals("desc")) comparator = comparator.reversed();
            }
            default -> throw new ResourceBadRequestException("Invalid parameter for sort, there's no such property");
        }
        classDtos.sort(comparator);
        return classDtos;
    }

    public ClassDetailDto createClass(ClassVM classVM) {
//        if (classRepository.existsByName(classVM.name()))
//            throw new ResourceAlreadyExistsException("Class name already exists");
        Program program = programRepository.findByIdForClass(classVM.programId())
                .orElseThrow(() -> new ResourceNotFoundException("Program ID not found"));
        int totalStudyDates = program.getSyllabuses().stream()
                .mapToInt(s -> s.getSessions().size())
                .sum();
//        int totalStudyDates = program.getSyllabuses().stream()
//                .flatMap(s -> s.getSessions().stream()).collect(Collectors.toSet()).size();
        if (classVM.studyDates().size() != totalStudyDates)
            throw new ResourceBadRequestException("Class have to last exactly for " + totalStudyDates + " dates");
        classVM.studyDates().sort(null);

        Class newClass = new Class();
        newClass.setName(classVM.name());
        Location location = locationRepository.findById(classVM.fsuId())
                .orElseThrow(() -> new ResourceNotFoundException("FSU ID not found"));
        Attendee attendee = attendeeRepository.findById(classVM.attendeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendee ID not found"));
        newClass.setDuration(totalStudyDates);
        newClass.setProgram(program);
        newClass.setCode("Generating");
        newClass = classRepository.save(newClass);
        newClass.setCode(
                location.getCode() +
                String.valueOf(classVM.studyDates().get(0).getYear()).substring(2, 4) +
                "_" + attendee.getCode() + "_" + newClass.getName().split(" ")[0] +
                "_" + String.valueOf(newClass.getId())
        );

        ClassDetail newClassDetail = new ClassDetail();
        newClassDetail.setClassField(newClass);
        newClassDetail.setStatus(classVM.status());
        newClassDetail.setLocation(location);
        newClassDetail.setAttendee(attendee);
        newClassDetail.setPlanned(classVM.planned());
        newClassDetail.setAccepted(classVM.accepted());
        newClassDetail.setActual(classVM.actual());
        newClassDetail.setStartAt(classVM.startAt());
        newClassDetail.setFinishAt(classVM.finishAt());
        newClassDetail.setOthers(classVM.others());
        newClassDetail.setDetailLocation(classVM.detailLocation());
        newClassDetail.setContactPoint(classVM.contactPoint());
        newClassDetail.setUsers(
                classVM.userIds().stream().map(userId ->
                        userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User ID not found")))
                        .toList()
        );
        ClassDetail finalNewClassDetail = classDetailRepository.save(newClassDetail);

        List<Session> sessionList = program.getSyllabuses().stream()
                .flatMap(s -> s.getSessions().stream())
//                .collect(Collectors.toSet())
//                .stream()
                .toList();
        for (int i = 0; i < sessionList.size(); i++) {
            ClassSchedule classSchedule = new ClassSchedule();
            classSchedule.setClassDetail(finalNewClassDetail);
            classSchedule.setStudyDate(classVM.studyDates().get(i));
            classSchedule.setSession(sessionList.get(i));
            classScheduleRepository.save(classSchedule);
        }
        return classDetailMapper.toDto(finalNewClassDetail);
    }

    public ClassDto getById(Long classId) {
        Class findingClass = classRepository.findByIdAndStatusNotDeleted(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class ID " + classId + " not found"));
        return classMapper.toDto(findingClass);
    }

    public ClassDetailDto getDetailsByClass_Id(Long classId) {
        ClassDetail findingClassDetail = classDetailRepository.findDetailsByClass_IdAndStatusNotDeleted(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class ID " + classId + " not found"));
        return classDetailMapper.toDto(findingClassDetail);
    }

    public List<ClassDetailDto> getDetailsByStudyDate(LocalDate date) {
        List<ClassDetail> classDetailList = classDetailRepository.findActiveClassByStudyDateAndStatus(date);
        return classDetailMapper.toDtos(classDetailList);
    }

    public void deleteClass(Long id) {
        Class classInfo = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class ID " + id + " not found"));
        classInfo.getClassDetail().setStatus(ClassStatus.DELETED.toString());
    }

    public void deactivateClass(Long id) {
        Class classInfo = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class ID " + id + " not found"));
        classInfo.getClassDetail().setStatus(ClassStatus.INACTIVE.toString());
    }

    public void activateClass(Long id) {
        Class classInfo = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class ID " + id + " not found"));
        classInfo.getClassDetail().setStatus(ClassStatus.OPENNING.toString());
    }

    public List<UserDto> getAllTrainer() {
        List<User> user = userRepository.findAllTrainers();
        return user.stream().map(userMapper::toDto).toList();
    }

    public List<UserDto> getAllClassAdmin() {
        List<User> user = userRepository.findAllClassAdmin();
        return user.stream().map(userMapper::toDto).toList();
    }

    public List<AttendeeDto> getAllAttendees() {
        List<Attendee> attendees = attendeeRepository.findAll();
        return attendees.stream().map(attendeeMapper::toDto).toList();
    }

    public List<LocationDto> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream().map(locationMapper::toDto).toList();
    }

    public ClassDetailDto updateClass(Long classId, ClassVM classVM) {
        Class currentClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class ID " + classId + " not found"));

        Program program;
        int totalStudyDates = 0;
        if (currentClass.getProgram().getId().equals(classVM.programId())) {
            program = currentClass.getProgram();
            totalStudyDates = currentClass.getClassDetail().getSchedules().size();
        }
        else {
            program = programRepository.findByIdForClass(classVM.programId())
                    .orElseThrow(() -> new ResourceNotFoundException("Program ID not found"));
            totalStudyDates = program.getSyllabuses().stream()
                    .mapToInt(s -> s.getSessions().size())
                    .sum();
//            totalStudyDates = program.getSyllabuses().stream()
//                    .flatMap(s -> s.getSessions().stream()).collect(Collectors.toSet()).size();
        }
        if (classVM.studyDates().size() != totalStudyDates)
            throw new ResourceBadRequestException("Class have to last exactly for " + totalStudyDates + " dates");
        classVM.studyDates().sort(null);

        currentClass.setName(classVM.name());
        Location location = locationRepository.findById(classVM.fsuId())
                .orElseThrow(() -> new ResourceNotFoundException("FSU ID not found"));
        Attendee attendee = attendeeRepository.findById(classVM.attendeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendee ID not found"));
        currentClass.setDuration(totalStudyDates);
        currentClass.setCode(
                location.getCode() +
                        String.valueOf(classVM.studyDates().get(0).getYear()).substring(2, 4) +
                        "_" + attendee.getCode() + "_" + currentClass.getName().split(" ")[0] +
                        "_" + String.valueOf(currentClass.getId())
        );

        ClassDetail currentClassDetail = currentClass.getClassDetail();
        currentClassDetail.setStatus(classVM.status());
        currentClassDetail.setLocation(location);
        currentClassDetail.setAttendee(attendee);
        currentClassDetail.setPlanned(classVM.planned());
        currentClassDetail.setAccepted(classVM.accepted());
        currentClassDetail.setActual(classVM.actual());
        currentClassDetail.setStartAt(classVM.startAt());
        currentClassDetail.setFinishAt(classVM.finishAt());
        currentClassDetail.setOthers(classVM.others());
        currentClassDetail.setDetailLocation(classVM.detailLocation());
        currentClassDetail.setContactPoint(classVM.contactPoint());

        currentClassDetail.setUsers(
                classVM.userIds().stream().map(userId ->
                                userRepository.findById(userId)
                                        .orElseThrow(() -> new ResourceNotFoundException("User ID ot found")))
                        .toList()
        );

        //change schedules
        if (currentClass.getProgram().getId().equals(classVM.programId())) {
            for (int i = 0; i < classVM.studyDates().size(); i++) {
                ClassSchedule currentClassSchedule = currentClassDetail.getSchedules().get(i);
                currentClassSchedule.setStudyDate(classVM.studyDates().get(i));
            }
        }
        else {
            currentClass.setProgram(program);
            currentClassDetail.getSchedules().clear();
            List<Session> sessionList = program.getSyllabuses().stream()
                    .flatMap(s -> s.getSessions().stream())
//                    .collect(Collectors.toSet())
//                    .stream()
                    .toList();
            for (int i = 0; i < sessionList.size(); i++) {
                ClassSchedule classSchedule = new ClassSchedule();
                classSchedule.setClassDetail(currentClassDetail);
                classSchedule.setStudyDate(classVM.studyDates().get(i));
                classSchedule.setSession(sessionList.get(i));
                currentClassDetail.getSchedules().add(classSchedule);
            }
        }
        return classDetailMapper.toDto(currentClassDetail);
    }

    public Page<ClassDto> filterClassV2(List<String> keywords,
                                        LocalDate from,
                                        LocalDate to,
                                        List<String> cities,
                                        List<String> classTimes,
                                        List<String> statuses,
                                        List<String> attendeeTypes,
                                        String fsu,
                                        String trainerCode,
                                        Pageable pageable) {
        Page<Class> classes = classRepository.findAll(
                ClassRepository.getSpecificationForSearchAndFilter(
                        keywords,
                        cities,
                        statuses,
                        attendeeTypes,
                        fsu
                ),
                pageable
                );

        return classes.map(classMapper::toDto);
    }
}
