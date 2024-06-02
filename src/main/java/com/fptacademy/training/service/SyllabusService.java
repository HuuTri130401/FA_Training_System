package com.fptacademy.training.service;

import com.fptacademy.training.domain.Assessment;
import com.fptacademy.training.domain.Delivery;
import com.fptacademy.training.domain.Lesson;
import com.fptacademy.training.domain.Material;
import com.fptacademy.training.domain.OutputStandard;
import com.fptacademy.training.domain.Session;
import com.fptacademy.training.domain.Syllabus;
import com.fptacademy.training.domain.TrainingPrinciple;
import com.fptacademy.training.domain.Unit;
import com.fptacademy.training.domain.enumeration.SyllabusStatus;
import com.fptacademy.training.exception.ResourceBadRequestException;
import com.fptacademy.training.repository.DeliveryRepository;
import com.fptacademy.training.repository.FormatTypeRepository;
import com.fptacademy.training.repository.LevelRepository;
import com.fptacademy.training.repository.OutputStandardRepository;
import com.fptacademy.training.repository.SyllabusRepository;
import com.fptacademy.training.service.dto.SyllabusDto;
import com.fptacademy.training.service.dto.SyllabusDto.SyllabusDetailDto;
import com.fptacademy.training.service.dto.SyllabusDto.SyllabusListDto;
import com.fptacademy.training.service.mapper.SyllabusMapper;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional
public class SyllabusService {

  private final SyllabusRepository syllabusRepository;
  private final ModelMapper modelMapper;
  private final SyllabusMapper syllabusMapper;
  private final LevelRepository levelRepository;
  private final FormatTypeRepository formatTypeRepository;
  private final DeliveryRepository deliveryRepository;
  private final OutputStandardRepository outputStandardRepository;

  @Transactional(readOnly = true)
  public Page<SyllabusListDto> findAll(Specification<Syllabus> spec, Pageable pageable) {
    ModelMapper map = new ModelMapper();
    map
      .createTypeMap(Syllabus.class, SyllabusListDto.class)
      .addMappings(mapper -> {
        mapper.map(src -> src.getCreatedBy().getCode(), SyllabusListDto::setCreatedBy);
        mapper
          .using(
            (Converter<List<Session>, List<OutputStandard>>) ctx ->
              ctx
                .getSource()
                .stream()
                .flatMap(session -> session.getUnits().stream())
                .flatMap(unit -> unit.getLessons().stream())
                .map(Lesson::getOutputStandard)
                .distinct()
                .toList()
          )
          .map(Syllabus::getSessions, SyllabusListDto::setOutputStandard);
        // mapper.using((Converter<List<Session>, Integer>) ctx -> ctx.getSource().size()).map(Syllabus::getSessions, SyllabusListDto::setDuration);
        // mapper.skip(SyllabusListDto::setStatus);
        // mapper.when(ctx -> Objects.nonNull(ctx.getSource())).map(Syllabus::getStatus, SyllabusListDto::setStatus);
        // new PropertyMap<Syllabus, SyllabusListDto>() {
        //   @Override
        //   protected void configure() {
        //     skip(destination.getStatus());
        //   }
        // };
      });

    return syllabusRepository.findAll(spec, pageable).map(s -> map.map(s, SyllabusListDto.class));
  }

  public SyllabusDetailDto save(SyllabusDetailDto syllabusDetailDto) {
    ModelMapper map = new ModelMapper();
    map
      .createTypeMap(SyllabusDetailDto.class, Syllabus.class)
      .addMappings(mapper -> {
        mapper.skip(Syllabus::setId);
        mapper.map(src -> 1.0, Syllabus::setVersion);
        mapper.map(src -> Long.toString(UUID.randomUUID().getMostSignificantBits() & 0xffffff, 36).toUpperCase(), Syllabus::setCode);
        // mapper.<SyllabusStatus>map(src -> SyllabusStatus.DRAFT, Syllabus::setStatus);
        mapper.using((Converter<List<Session>, Integer>) ctx -> ctx.getSource().size()).map(SyllabusDetailDto::getSessions, Syllabus::setDuration);
      });
    map.createTypeMap(TrainingPrinciple.class, TrainingPrinciple.class).addMappings(mapper -> mapper.skip(TrainingPrinciple::setId));
    map.createTypeMap(Assessment.class, Assessment.class).addMappings(mapper -> mapper.skip(Assessment::setId));
    map.createTypeMap(Session.class, Session.class).addMappings(mapper -> mapper.skip(Session::setId));
    map
      .createTypeMap(Unit.class, Unit.class)
      .addMappings(mapper -> {
        mapper.skip(Unit::setId);
        mapper
          .using(
            (Converter<List<Lesson>, Double>) ctx -> Math.round(ctx.getSource().stream().mapToDouble(Lesson::getDuration).sum() / 60 * 10.0) / 10.0
          )
          .map(Unit::getLessons, Unit::setTotalDurationLesson);
      });
    map.createTypeMap(Lesson.class, Lesson.class).addMappings(mapper -> mapper.skip(Lesson::setId));
    map.createTypeMap(Material.class, Material.class).addMappings(mapper -> mapper.skip(Material::setId));
    return modelMapper.map(syllabusRepository.save(map.map(syllabusDetailDto, Syllabus.class)), SyllabusDetailDto.class);
  }

  public Optional<SyllabusDetailDto> update(SyllabusDetailDto syllabusDto) {
    TypeMap<SyllabusDetailDto, Syllabus> typeMap = modelMapper.getTypeMap(SyllabusDetailDto.class, Syllabus.class);
    if (typeMap == null) {
      typeMap =
        modelMapper
          .createTypeMap(SyllabusDetailDto.class, Syllabus.class)
          .addMappings(mapper -> {
            mapper.skip(Syllabus::setSessions);
            // mapper.skip(Syllabus::setStatus);
            mapper.skip(Syllabus::setCreatedAt);
            mapper.skip(Syllabus::setLastModifiedAt);
            mapper.skip(Syllabus::setCode);
            mapper.skip(Syllabus::setVersion);
            mapper
              .using((Converter<List<Session>, Integer>) ctx -> ctx.getSource().size())
              .map(SyllabusDetailDto::getSessions, Syllabus::setDuration);
          });
    }
    return syllabusRepository
      .findById(syllabusDto.getId())
      .map(syl -> {
        modelMapper.map(syllabusDto, syl);
        syl.setVersion(syl.getVersion() + 0.1F);
        syl.getSessions().clear();
        syl.getSessions().addAll(modelMapper.map(syllabusDto.getSessions(), new TypeToken<List<Session>>() {}.getType()));
        syl
          .getSessions()
          .forEach(session -> {
            session.setSyllabus(syl);
            session
              .getUnits()
              .forEach(unit -> {
                unit.setSession(session);
                unit.setTotalDurationLesson(Math.round(unit.getLessons().stream().mapToDouble(Lesson::getDuration).sum() / 60 * 10.0) / 10.0);
                unit
                  .getLessons()
                  .forEach(lesson -> {
                    lesson.setUnit(unit);
                    lesson.getMaterials().forEach(material -> material.setLesson(lesson));
                  });
              });
          });
        return syl;
      })
      .map(syllabusRepository::save)
      .map(syl -> modelMapper.map(syl, SyllabusDetailDto.class));
  }

  @Transactional(readOnly = true)
  public Optional<SyllabusDetailDto> findOne(Long id) {
    ModelMapper map = new ModelMapper();
    map
      .createTypeMap(Syllabus.class, SyllabusDetailDto.class)
      .addMappings(mapper -> {
        mapper.map(src -> src.getLastModifiedBy().getFullName(), SyllabusDetailDto::setLastModifiedBy);
        mapper.map(src -> src.getCreatedBy().getFullName(), SyllabusDetailDto::setCreatedBy);
        mapper
          .using(
            (Converter<List<Session>, List<OutputStandard>>) ctx ->
              ctx
                .getSource()
                .stream()
                .flatMap(session -> session.getUnits().stream())
                .flatMap(unit -> unit.getLessons().stream())
                .map(Lesson::getOutputStandard)
                .distinct()
                .toList()
          )
          .map(Syllabus::getSessions, SyllabusDetailDto::setOutputStandard);
        // mapper
        //   .using((Converter<List<Session>, Integer>) ctx -> ctx.getSource().size())
        //   .map(Syllabus::getSessions, SyllabusDetailDto::setDuration);
        mapper
          .using(
            (Converter<List<Session>, Double>) ctx ->
              Math.round(
                (
                  ctx
                    .getSource()
                    .stream()
                    .flatMap(session -> session.getUnits().stream())
                    .flatMap(unit -> unit.getLessons().stream())
                    .mapToDouble(Lesson::getDuration)
                    .sum() /
                  60
                ) *
                1.0
              ) /
              1.0
          )
          .map(Syllabus::getSessions, SyllabusDetailDto::setDurationInHours);
        mapper
          .using(
            (Converter<List<Session>, List<Session>>) ctx ->
              ctx
                .getSource()
                .stream()
                .sorted(Comparator.comparing(Session::getIndex, Comparator.nullsLast(Integer::compareTo)))
                .peek(session ->
                  session.setUnits(
                    session
                      .getUnits()
                      .stream()
                      .sorted(Comparator.comparing(Unit::getIndex, Comparator.nullsLast(Integer::compareTo)))
                      .peek(unit ->
                        unit.setLessons(
                          unit.getLessons().stream().sorted(Comparator.comparing(Lesson::getIndex, Comparator.nullsLast(Integer::compareTo))).toList()
                        )
                      )
                      .toList()
                  )
                )
                .toList()
          )
          .map(Syllabus::getSessions, SyllabusDetailDto::setSessions);
        mapper
          .using(
            (Converter<List<Session>, List<Delivery>>) ctx -> {
              Map<Delivery, Long> deliveryCount = ctx
                .getSource()
                .stream()
                .flatMap(session -> session.getUnits().stream())
                .flatMap(unit -> unit.getLessons().stream())
                .collect(Collectors.groupingBy(Lesson::getDelivery, Collectors.counting()));
              long totalLessons = deliveryCount.values().stream().mapToLong(Long::longValue).sum();
              List<Delivery> timeAllocation = deliveryCount
                .entrySet()
                .stream()
                .map(entry -> {
                  double percentage = Math.round(((entry.getValue() * 100.0) / totalLessons) * 1.0) / 1.0;
                  Delivery delivery = entry.getKey();
                  delivery.setPresent(percentage);
                  return delivery;
                })
                .sorted(Comparator.comparing(Delivery::getPresent, Comparator.reverseOrder()))
                .collect(Collectors.toList());
              return timeAllocation;
            }
          )
          .map(Syllabus::getSessions, SyllabusDetailDto::setTimeAllocation);
      });

    return syllabusRepository
      .findById(id)
      .map(syl -> map.map(syl, SyllabusDetailDto.class))
      .filter(s -> s.getStatus() != null && !s.getStatus().equals(SyllabusStatus.REJECTED.toString()));
  }

  public void delete(Syllabus syllabus) {
    syllabusRepository.save(syllabus);
  }

  private <T> T getCellValue(Cell cell, CellType expectedType, T defaultValue, Class<T> clazz) {
    return Optional
      .ofNullable(cell)
      .filter(c -> c.getCellType() == expectedType)
      .map(c ->
        expectedType == CellType.NUMERIC && Number.class.isAssignableFrom(clazz)
          ? clazz.cast(
            new HashMap<Class<?>, Function<Double, ?>>() {
              {
                put(Integer.class, (Double d) -> d.intValue());
                put(Double.class, (Double d) -> d);
                put(Float.class, (Double d) -> d.floatValue());
                put(Long.class, (Double d) -> d.longValue());
              }
            }
              .getOrDefault(
                clazz,
                (Double d) -> {
                  throw new IllegalArgumentException("Unexpected class type: " + clazz.getName());
                }
              )
              .apply(c.getNumericCellValue())
          )
          : expectedType == CellType.BOOLEAN && clazz == Boolean.class ? clazz.cast(c.getBooleanCellValue()) : clazz.cast(c.getStringCellValue())
      )
      .orElse(defaultValue);
  }

  public List<?> importExcel(MultipartFile file, String[] scanning, String handle) {
    ModelMapper map = new ModelMapper();
    map
      .createTypeMap(Syllabus.class, Syllabus.class)
      .addMappings(mapper -> {
        mapper.skip(Syllabus::setId);
        mapper.using((Converter<List<Session>, Integer>) ctx -> ctx.getSource().size()).map(Syllabus::getSessions, Syllabus::setDuration);
      });
    map.createTypeMap(TrainingPrinciple.class, TrainingPrinciple.class).addMappings(mapper -> mapper.skip(TrainingPrinciple::setId));
    map.createTypeMap(Assessment.class, Assessment.class).addMappings(mapper -> mapper.skip(Assessment::setId));
    map.createTypeMap(Session.class, Session.class).addMappings(mapper -> mapper.skip(Session::setId));
    map
      .createTypeMap(Unit.class, Unit.class)
      .addMappings(mapper -> {
        mapper.skip(Unit::setId);
        mapper
          .using((Converter<List<Lesson>, Double>) ctx -> ctx.getSource().stream().mapToDouble(Lesson::getDuration).sum() / 60)
          .map(Unit::getLessons, Unit::setTotalDurationLesson);
      });
    map.createTypeMap(Lesson.class, Lesson.class).addMappings(mapper -> mapper.skip(Lesson::setId));
    map.createTypeMap(Material.class, Material.class).addMappings(mapper -> mapper.skip(Material::setId));
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Set<String> codeSet = new HashSet<>();
      Stream<Row> syllabusSheet = StreamSupport
        .stream(workbook.getSheet("syllabus").spliterator(), false)
        .skip(1)
        .filter(row -> {
          String code = getCellValue(row.getCell(1), CellType.STRING, null, String.class);
          if (codeSet.contains(code) && handle.equals("skip")) {
            return false;
          } else if (codeSet.contains(code) && handle.equals("replace")) {
            return true;
          } else if (codeSet.contains(code) && handle.equals("allow")) {
            row.createCell(1).setCellValue(Long.toString(UUID.randomUUID().getMostSignificantBits() & 0xffffff, 36).toUpperCase());
            return true;
          } else {
            codeSet.add(code);
            return true;
          }
        })
        .collect(
          Collectors.toMap(row -> getCellValue(row.getCell(1), CellType.STRING, null, String.class), Function.identity(), (row1, row2) -> row2)
        )
        .values()
        .stream();
      Supplier<Stream<Row>> assessmentSheet = () ->
        StreamSupport
          .stream(workbook.getSheet("assessment").spliterator(), false)
          .skip(1)
          .filter(row -> getCellValue(row.getCell(0), CellType.NUMERIC, null, Long.class) != null);
      Supplier<Stream<Row>> sessionSheet = () ->
        StreamSupport
          .stream(workbook.getSheet("session").spliterator(), false)
          .skip(1)
          .filter(row -> getCellValue(row.getCell(3), CellType.NUMERIC, null, Long.class) != null);
      Supplier<Stream<Row>> unitSheet = () ->
        StreamSupport
          .stream(workbook.getSheet("unit").spliterator(), false)
          .skip(1)
          .filter(row -> getCellValue(row.getCell(4), CellType.NUMERIC, null, Long.class) != null);
      Supplier<Stream<Row>> lessonSheet = () ->
        StreamSupport
          .stream(workbook.getSheet("lesson").spliterator(), false)
          .skip(1)
          .filter(row -> getCellValue(row.getCell(7), CellType.NUMERIC, null, Long.class) != null);
      Supplier<Stream<Row>> materialSheet = () ->
        StreamSupport
          .stream(workbook.getSheet("material").spliterator(), false)
          .skip(1)
          .filter(row -> getCellValue(row.getCell(3), CellType.NUMERIC, null, Long.class) != null);

      Supplier<Stream<Row>> trainingPrincipleSheet = () ->
        StreamSupport
          .stream(workbook.getSheet("trainingPrinciple").spliterator(), false)
          .skip(1)
          .filter(row -> getCellValue(row.getCell(0), CellType.NUMERIC, null, Long.class) != null);

      return syllabusRepository.saveAll(
        map.map(
          syllabusSheet
            .map(rowSyllabus -> {
              String code = getCellValue(rowSyllabus.getCell(1), CellType.STRING, null, String.class) == null
                ? Long.toString(UUID.randomUUID().getMostSignificantBits() & 0xffffff, 36).toUpperCase()
                : getCellValue(rowSyllabus.getCell(1), CellType.STRING, null, String.class).toUpperCase();
              code =
                syllabusRepository.existsByCode(code) == true
                  ? Long.toString(UUID.randomUUID().getMostSignificantBits() & 0xffffff, 36).toUpperCase()
                  : code;
              return Syllabus
                .builder()
                .id(getCellValue(rowSyllabus.getCell(0), CellType.NUMERIC, null, Long.class))
                .code(code)
                .name(getCellValue(rowSyllabus.getCell(2), CellType.STRING, null, String.class))
                .attendeeNumber(getCellValue(rowSyllabus.getCell(3), CellType.NUMERIC, null, Integer.class))
                .status(SyllabusStatus.ACTIVATED)
                .version(1.0F)
                .courseObjective(getCellValue(rowSyllabus.getCell(4), CellType.STRING, null, String.class))
                .technicalRequirement(getCellValue(rowSyllabus.getCell(5), CellType.STRING, null, String.class))
                .trainingPrinciple(
                  trainingPrincipleSheet
                    .get()
                    .filter(rowTrainingPrinciple ->
                      getCellValue(rowTrainingPrinciple.getCell(0), CellType.NUMERIC, null, Long.class) ==
                      getCellValue(rowSyllabus.getCell(6), CellType.NUMERIC, null, Long.class)
                    )
                    .findFirst()
                    .map(rowTrainingPrinciple ->
                      TrainingPrinciple
                        .builder()
                        .id(getCellValue(rowTrainingPrinciple.getCell(0), CellType.NUMERIC, null, Long.class))
                        .training(getCellValue(rowTrainingPrinciple.getCell(1), CellType.STRING, null, String.class))
                        .reTest(getCellValue(rowTrainingPrinciple.getCell(2), CellType.STRING, null, String.class))
                        .marking(getCellValue(rowTrainingPrinciple.getCell(3), CellType.STRING, null, String.class))
                        .waiverCriteria(getCellValue(rowTrainingPrinciple.getCell(4), CellType.STRING, null, String.class))
                        .others(getCellValue(rowTrainingPrinciple.getCell(5), CellType.STRING, null, String.class))
                        .build()
                    )
                    .orElse(null)
                )
                .level(levelRepository.findById(getCellValue(rowSyllabus.getCell(7), CellType.NUMERIC, 0L, Long.class)).orElse(null))
                .assessment(
                  assessmentSheet
                    .get()
                    .filter(rowAssessment ->
                      getCellValue(rowAssessment.getCell(0), CellType.NUMERIC, null, Long.class) ==
                      getCellValue(rowSyllabus.getCell(8), CellType.NUMERIC, null, Long.class)
                    )
                    .findFirst()
                    .map(rowAssessment ->
                      Assessment
                        .builder()
                        .id(getCellValue(rowAssessment.getCell(0), CellType.NUMERIC, null, Long.class))
                        .assignment(getCellValue(rowAssessment.getCell(1), CellType.NUMERIC, null, Float.class))
                        .finalField(getCellValue(rowAssessment.getCell(2), CellType.NUMERIC, null, Float.class))
                        .finalPractice(getCellValue(rowAssessment.getCell(3), CellType.NUMERIC, null, Float.class))
                        .finalTheory(getCellValue(rowAssessment.getCell(4), CellType.NUMERIC, null, Float.class))
                        .gpa(getCellValue(rowAssessment.getCell(5), CellType.NUMERIC, null, Float.class))
                        .quiz(getCellValue(rowAssessment.getCell(6), CellType.NUMERIC, null, Float.class))
                        .build()
                    )
                    .orElse(null)
                )
                .sessions(
                  sessionSheet
                    .get()
                    .filter(rowSession ->
                      getCellValue(rowSession.getCell(3), CellType.NUMERIC, null, Long.class) ==
                      getCellValue(rowSyllabus.getCell(0), CellType.NUMERIC, null, Long.class)
                    )
                    .map(rowSession ->
                      Session
                        .builder()
                        .id(getCellValue(rowSession.getCell(0), CellType.NUMERIC, null, Long.class))
                        .index(getCellValue(rowSession.getCell(1), CellType.NUMERIC, null, Integer.class))
                        .name(getCellValue(rowSession.getCell(2), CellType.STRING, null, String.class))
                        .units(
                          unitSheet
                            .get()
                            .filter(rowUnit ->
                              getCellValue(rowSession.getCell(0), CellType.NUMERIC, null, Long.class) ==
                              getCellValue(rowUnit.getCell(4), CellType.NUMERIC, null, Long.class)
                            )
                            .map(rowUnit ->
                              Unit
                                .builder()
                                .id(getCellValue(rowUnit.getCell(0), CellType.NUMERIC, null, Long.class))
                                .index(getCellValue(rowUnit.getCell(1), CellType.NUMERIC, null, Integer.class))
                                .name(getCellValue(rowUnit.getCell(2), CellType.STRING, null, String.class))
                                .title(getCellValue(rowUnit.getCell(3), CellType.STRING, null, String.class))
                                .lessons(
                                  lessonSheet
                                    .get()
                                    .filter(rowLesson ->
                                      getCellValue(rowUnit.getCell(0), CellType.NUMERIC, null, Long.class) ==
                                      getCellValue(rowLesson.getCell(7), CellType.NUMERIC, null, Long.class)
                                    )
                                    .map(rowLesson ->
                                      Lesson
                                        .builder()
                                        .id(getCellValue(rowLesson.getCell(0), CellType.NUMERIC, null, Long.class))
                                        .name(getCellValue(rowLesson.getCell(1), CellType.STRING, null, String.class))
                                        .index(getCellValue(rowLesson.getCell(2), CellType.NUMERIC, null, Integer.class))
                                        .duration(getCellValue(rowLesson.getCell(3), CellType.NUMERIC, null, Integer.class))
                                        .delivery(
                                          deliveryRepository
                                            .findById(getCellValue(rowLesson.getCell(4), CellType.NUMERIC, 0L, Long.class))
                                            .orElse(null)
                                        )
                                        .formatType(
                                          formatTypeRepository
                                            .findById(getCellValue(rowLesson.getCell(5), CellType.NUMERIC, 0L, Long.class))
                                            .orElse(null)
                                        )
                                        .outputStandard(
                                          outputStandardRepository
                                            .findById(getCellValue(rowLesson.getCell(6), CellType.NUMERIC, 0L, Long.class))
                                            .orElse(null)
                                        )
                                        .materials(
                                          materialSheet
                                            .get()
                                            .filter(rowMaterial ->
                                              getCellValue(rowLesson.getCell(0), CellType.NUMERIC, null, Long.class) ==
                                              getCellValue(rowMaterial.getCell(3), CellType.NUMERIC, null, Long.class)
                                            )
                                            .map(rowMaterial ->
                                              Material
                                                .builder()
                                                .id(getCellValue(rowMaterial.getCell(0), CellType.NUMERIC, null, Long.class))
                                                .name(getCellValue(rowMaterial.getCell(1), CellType.STRING, null, String.class))
                                                .fileUrl(getCellValue(rowMaterial.getCell(2), CellType.STRING, null, String.class))
                                                .build()
                                            )
                                            .toList()
                                        )
                                        .build()
                                    )
                                    .toList()
                                )
                                .build()
                            )
                            .toList()
                        )
                        .build()
                    )
                    .toList()
                )
                .build();
            })
            .toList(),
          new TypeToken<List<Syllabus>>() {}.getType()
        )
      );
    } catch (IOException e) {
      throw new ResourceBadRequestException("Error reading file", e);
    }
  }

  public List<SyllabusDto.SyllabusListDto> findActivatedSyllabusesByName(String name) {
    List<Syllabus> syllabuses = syllabusRepository.findByNameContainsIgnoreCaseAndStatus(name, SyllabusStatus.ACTIVATED);
    return new ArrayList<>(syllabusMapper.toDtos(syllabuses));
  }
}
