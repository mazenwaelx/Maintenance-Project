package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.UsersRepository;
import com.LMS.Learning_Management_System.repository.UsersTypeRepository;
import com.LMS.Learning_Management_System.util.UserSignUpRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersTypeRepository usersTypeRepository;


    public UsersService(UsersRepository usersRepository, InstructorService instructorService ,AdminService adminService, PasswordEncoder passwordEncoder,StudentService studentService , UsersTypeRepository usersTypeRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.usersTypeRepository = usersTypeRepository;
    }

    public void save(UserSignUpRequest signUpRequest ) {
        if (usersRepository.findByEmail(signUpRequest.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }
        UsersType userType = usersTypeRepository.findById(signUpRequest.getUserTypeId())
                .orElseThrow(() -> new EntityNotFoundException("User Type not found"));
        Users newUser = new Users(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                userType
        );
        usersRepository.save(newUser);
    }

    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword); // Use the PasswordEncoder to match the passwords
    }

}