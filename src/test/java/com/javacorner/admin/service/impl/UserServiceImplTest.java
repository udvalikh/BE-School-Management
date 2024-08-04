package com.javacorner.admin.service.impl;

import com.javacorner.admin.dao.RoleDao;
import com.javacorner.admin.dao.UserDao;
import com.javacorner.admin.entity.Role;
import com.javacorner.admin.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testLoadUserByEmail(){
        String email="user@gmail.com";
        User user= new User();
        user.setEmail(email);

        when(userDao.findByEmail(email)).thenReturn(user);

        User result = userService.loadUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userDao).findByEmail(email);

    }

    @Test
    void testCreateUser(){
        User user= new User();
        String email="udval@gmail.com";
        String password="1234";
        String encodedPassword="Encoded1234";
        user.setEmail(email);
        user.setPassword(encodedPassword);


        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userDao.save(user)).thenReturn(user);

        User result = userService.createUser(email, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(encodedPassword, result.getPassword());
        verify(passwordEncoder).encode(password);
        verify(userDao).save(user);
 }

    @Test
    void assignRoleToUser(){
        String email = "test@example.com";
        String roleName = "ROLE_USER";
        User user = mock(User.class);
        Role role = new Role();
        role.setName(roleName);

        when(userDao.findByEmail(email)).thenReturn(user);
        when(roleDao.findByName(roleName)).thenReturn(role);

        // Act
        userService.assignRoleToUser(email, roleName);

        // Assert
        verify(userDao).findByEmail(email);
        verify(roleDao).findByName(roleName);
        verify(user).assignRoleToUser(role);
    }

}
