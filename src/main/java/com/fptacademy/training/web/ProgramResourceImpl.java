package com.fptacademy.training.web;

import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.service.ProgramService;
import com.fptacademy.training.service.dto.ProgramDto;
import com.fptacademy.training.service.dto.SyllabusDto;
import com.fptacademy.training.service.mapper.ProgramMapper;
import com.fptacademy.training.web.api.ProgramResource;
import com.fptacademy.training.web.vm.ProgramExcelImportResponseVM;
import com.fptacademy.training.web.vm.ProgramListResponseVM;
import com.fptacademy.training.web.vm.ProgramVM;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProgramResourceImpl implements ProgramResource {
    private final ProgramService programService;
    private final ProgramMapper programMapper;
    private final ResourceLoader resourceLoader;

    @Override
    public ResponseEntity<ProgramDto> createProgram(ProgramVM programVM) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(programService.createProgram(programVM));
    }

    //Activate the Program by id
    @Override
    public ResponseEntity<ProgramDto> activateProgram(Long id) {
        ProgramDto programDto=programService.activateProgram(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(programDto);
    }

    @Override
    public ResponseEntity<ProgramListResponseVM> getPrograms(List<String> keywords, Boolean activated, String sort, int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (activated != null &&
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList().contains(Permissions.PROGRAM_VIEW)) {
            activated = true;
        }
        List<ProgramDto> programDTOs = programService.getPrograms(keywords, activated, sort);
        int numberOfFoundPrograms = programDTOs.size();
        if (numberOfFoundPrograms <= size) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ProgramListResponseVM(numberOfFoundPrograms, programDTOs));
        }
        // Apply pagination
        int start = (page - 1) * size;
        int end = Math.min(start + size, programDTOs.size());
        Page<ProgramDto> pageOfPrograms = new PageImpl<>(
                programDTOs.subList(start, end),
                PageRequest.of(page - 1, size),
                numberOfFoundPrograms);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ProgramListResponseVM(numberOfFoundPrograms, pageOfPrograms.getContent()));
    }

    @Override
    public ResponseEntity<List<SyllabusDto.SyllabusListDto>> getSyllabusesByProgramId(Long id) {
        List<SyllabusDto.SyllabusListDto> syllabusDTOs = programService.findSyllabusesByProgramId(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(syllabusDTOs);
    }

    @Override
    public ResponseEntity<ProgramDto> getProgramById(Long id) {
        ProgramDto programDto = programService.findProgramByProgramId(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(programDto);
    }


    @Override
    public ResponseEntity<Resource> downloadExcelTemplate() {
        Resource resource = resourceLoader.getResource("classpath:templates/Program-Template.xlsx");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Program-Template.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Override
    public ResponseEntity<ProgramExcelImportResponseVM> importProgramsFromExcel(MultipartFile file, String[] properties, String handler) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(programService.importProgramFromExcel(file, properties, handler));
    }

    @Override
    public ResponseEntity<ProgramDto> deactivateProgram(Long id) {
        ProgramDto programDto = programMapper.toDto(programService.deactivateProgram(id));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(programDto);
    }

    @Override
    public ResponseEntity<ProgramDto> updateProgram(ProgramVM programVM, Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(programService.updateProgram(programVM, id));
    }

    @Override
    public void deleteProgram(Long id) {
        programService.deleteProgram(id);
    }
}
