package com.LMS.Learning_Management_System.conifg;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
//class customizes the behavior that occurs after a successful login
// . It implements AuthenticationSuccessHandler from Spring Security,
// allowing it to define specific actions for when a user successfully authenticates
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("The username " + username + " is logged in.");
        boolean hasAdminRole = authentication.getAuthorities().stream().anyMatch(r->r.getAuthority().equals("Admin"));
        boolean hasInstructorRole = authentication.getAuthorities().stream().anyMatch(r->r.getAuthority().equals("Instructor"));
        boolean hasStudentRole = authentication.getAuthorities().stream().anyMatch(r->r.getAuthority().equals("Student"));

        if (hasAdminRole || hasInstructorRole || hasStudentRole) {
            response.sendRedirect("/dashboard/");
        }
    }
}