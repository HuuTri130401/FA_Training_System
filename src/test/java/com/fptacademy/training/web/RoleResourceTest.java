package com.fptacademy.training.web;

import com.fptacademy.training.domain.Role;
import com.fptacademy.training.repository.RoleRepository;
import com.fptacademy.training.security.Permissions;
import com.fptacademy.training.service.RoleService;
import com.fptacademy.training.service.UserService;
import com.fptacademy.training.service.dto.RoleDto;
import com.fptacademy.training.service.mapper.RoleMapper;
import com.fptacademy.training.service.mapper.UserMapper;
import com.fptacademy.training.web.vm.RoleVM;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RoleResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RoleService roleService;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleResourceImpl roleResource;

    @BeforeEach
    public void getAccessToken() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleResource).build();
    }

//    @Test
//    public void testUpdateRolePermission() throws Exception {
//        String roleName1 = "Super Admin";
//        List<String> permission1 = Arrays.asList(
//                Permissions.SYLLABUS_FULL_ACCESS,
//                Permissions.PROGRAM_FULL_ACCESS,
//                Permissions.CLASS_FULL_ACCESS,
//                Permissions.MATERIAL_FULL_ACCESS,
//                Permissions.USER_FULL_ACCESS);
//
//        List<RoleVM> listRoleVM = new ArrayList<>();
//        listRoleVM.add(new RoleVM(roleName1, permission1));
//
//        Role role = Role.builder()
//                .id(1L)
//                .name(roleName1)
//                .permissions(permission1)
//                .build();
//        RoleDto roleDto = roleMapper.toDto(role);
//        System.out.println(roleDto);
//
//        List<RoleDto> roleDtos = new ArrayList<>();
//        roleDtos.add(roleDto);
//
//        when(roleService.updatePermission(listRoleVM)).thenReturn(new ArrayList<>());
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/api/users/permission/", listRoleVM)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
}
