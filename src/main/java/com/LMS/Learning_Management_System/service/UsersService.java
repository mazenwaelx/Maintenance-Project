package com.LMS.Learning_Management_System.service;

import com.LMS.Learning_Management_System.entity.*;
import com.LMS.Learning_Management_System.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final AdminRepository AdminRepository;
    private final UsersTypeRepository usersTypeRepository;
    private final InstructorRepository InstructorRepository;
    private final StudentRepository StudentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository, AdminRepository adminRepository, UsersTypeRepository usersTypeRepository, InstructorRepository instructorRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.AdminRepository = adminRepository;
        this.usersTypeRepository = usersTypeRepository;
        this.InstructorRepository = instructorRepository;
        this.StudentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users addNew(Users users) {
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        // Validate and fetch UserType
        Integer userTypeId = users.getUserTypeId() != null ? users.getUserTypeId().getUserTypeId() : null;
        if (userTypeId == null) {
            throw new IllegalArgumentException("UserType cannot be null");
        }
        UsersType usersType = usersTypeRepository.findById(userTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid UserType ID"));
        users.setUserTypeId(usersType);

        // Save user and related entity
        Users savedUser = usersRepository.save(users);
        switch (userTypeId) {
            case 1:
                AdminRepository.save(new Admin(savedUser));
                break;
            case 2:
                StudentRepository.save(new Student(savedUser));
                break;
            case 3:
                InstructorRepository.save(new Instructor(savedUser));
                break;
            default:
                throw new IllegalArgumentException("Invalid UserType ID");
        }

        return savedUser;
    }



    public Object getCurrentUserInformation() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Users users = usersRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("Could not found " + "user"));
            int userId = users.getUserId();
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Admin"))) {
                Admin admin = AdminRepository.findById(userId).orElse(new Admin());
                return admin;
            } else if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Student"))){
                Student student = StudentRepository.findById(userId).orElse(new Student());
                return student;
            }else{
                Instructor instructor = InstructorRepository.findById(userId).orElse(new Instructor());
                return instructor;
            }
        }

        return null;
    }

    public Users getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not found " + "user"));
            return user;
        }

        return null;
    }

    public Users findByEmail(String currentUsername) {
        return usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not " +
                "found"));
    }

    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


}