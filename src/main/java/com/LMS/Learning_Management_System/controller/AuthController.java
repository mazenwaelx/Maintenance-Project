package com.LMS.Learning_Management_System.controller;
import com.LMS.Learning_Management_System.entity.Users;
import com.LMS.Learning_Management_System.service.*;
import com.LMS.Learning_Management_System.util.UserSignUpRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsersService usersService;

    public AuthController(UsersService usersService) {
        this.usersService = usersService;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpRequest signUpRequest, HttpServletRequest request) {
        Users loggedInUser = (Users) request.getSession().getAttribute("user");

        // Check if the logged-in user is an admin
        if (loggedInUser == null || loggedInUser.getUserTypeId().getUserTypeId() != 1) {  // assuming 1 is Admin
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin only can create account");
        }

        try {
            usersService.save(signUpRequest, request);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request, @RequestParam String email, @RequestParam String password) {
        try {
            Users user = usersService.findByEmail(email);
            if(user==null) return ResponseEntity.badRequest().body("invalid email");
            if (usersService.validatePassword(password, user.getPassword())) {
                request.getSession().setAttribute("user", user);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return ResponseEntity.ok("Login successful. Welcome, " + user.getEmail());
            } else {
                return ResponseEntity.badRequest().body("Invalid email or password.");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Successfully logged out");
    }
}
