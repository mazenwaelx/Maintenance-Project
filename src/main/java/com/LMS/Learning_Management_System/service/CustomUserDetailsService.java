package com.LMS.Learning_Management_System.service;


import com.LMS.Learning_Management_System.entity.Users;
import com.LMS.Learning_Management_System.repository.UsersRepository;
import com.LMS.Learning_Management_System.util.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

//The CustomUserDetailsService class implements UserDetailsService,
// a Spring Security interface used to retrieve user data for authentication.
// This service is a bridge between Spring Security and your application's data source (in this case, UsersRepository)
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Autowired
    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    //Wraps the Users object in a CustomUserDetails instance,
    // which adapts the Users entity to Spring Security’s UserDetails interface.
    // This allows Spring Security to recognize and use the user’s credentials and authorities during authentication.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not found user"));
        return new CustomUserDetails(user);
    }
}