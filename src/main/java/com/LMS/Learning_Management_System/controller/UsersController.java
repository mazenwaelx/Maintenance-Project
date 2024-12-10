package com.LMS.Learning_Management_System.controller;

import com.LMS.Learning_Management_System.entity.Users;
import com.LMS.Learning_Management_System.entity.UsersType;
import com.LMS.Learning_Management_System.service.UsersService;
import com.LMS.Learning_Management_System.service.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody Users users) {
        Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());
        if (optionalUsers.isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered. Try to login or register with another email.");
        }

        try {
            usersService.addNew(users);
            return ResponseEntity.ok("User registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, @RequestParam String email, @RequestParam String password) {
        try {
            Users user = usersService.findByEmail(email);
            if (usersService.validatePassword(password, user.getPassword())) {
                request.getSession().setAttribute("user", user);
                return ResponseEntity.ok("Login successful. Welcome, " + user.getEmail());
            } else {
                return ResponseEntity.badRequest().body("Invalid email or password.");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }
    }




    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok("Logged out successfully.");
    }
}