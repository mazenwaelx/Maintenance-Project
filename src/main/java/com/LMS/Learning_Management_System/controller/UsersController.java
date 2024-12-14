package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.entity.UsersType;
import com.LMS.Learning_Management_System.service.UsersService;
import com.LMS.Learning_Management_System.service.UsersTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersTypeService usersTypeService;
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

    @GetMapping("/types")
    public ResponseEntity<List<UsersType>> getAllUserTypes() {
        List<UsersType> usersTypes = usersTypeService.getAll();
        return ResponseEntity.ok(usersTypes);
    }
}