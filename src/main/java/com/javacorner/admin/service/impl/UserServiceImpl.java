package com.javacorner.admin.service.impl;

import com.javacorner.admin.dao.RoleDao;
import com.javacorner.admin.dao.UserDao;
import com.javacorner.admin.entity.Role;
import com.javacorner.admin.entity.User;
import com.javacorner.admin.service.UserService;
import jakarta.transaction.Transactional;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private RoleDao roleDao;
//    private PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
//        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public User loadUserByEmail(String email) {
        //test
        return userDao.findByEmail(email);
    }

    @Override
    public User createUser(String email, String password) {
//        String encodedPassword=passwordEncoder.encode(password);
        return userDao.save(new User(email, password));
    }

    @Override
    public void assignRoleToUser(String email, String roleName) {
        User user= loadUserByEmail(email);
        Role role= roleDao.findByName(roleName);
        user.assignRoleToUser(role);
    }
}
