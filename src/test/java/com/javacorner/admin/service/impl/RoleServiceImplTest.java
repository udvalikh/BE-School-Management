package com.javacorner.admin.service.impl;

import com.javacorner.admin.dao.RoleDao;
import com.javacorner.admin.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RoleDao roleDao;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void testCreateRole(){
        Role role= new Role();
        String roleName="Student";
        role.setName(roleName);

        when(roleDao.save(role)).thenReturn(role);

        Role result = roleService.createRole(roleName);

        assertEquals(roleName, result.getName());
        verify(roleDao).save(role);
    }
}
