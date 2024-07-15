package com.javacorner.admin.web;

import com.javacorner.admin.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class UserRestController {

    private UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public boolean checkIfEmailExists(@RequestParam(name = "email", defaultValue = "") String email){
        return userService.loadUserByEmail(email) !=null;
    }
}
