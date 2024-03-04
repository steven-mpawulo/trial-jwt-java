package com.example.trialjwt.services;

import com.example.trialjwt.models.MyUserDetails;
import com.example.trialjwt.models.User;
import com.example.trialjwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.example.trialjwt.models.User> user = userRepository.findUserByEmail(username);
        if (user.isPresent()) {
            User actualUser = user.get();
            return new MyUserDetails(username, actualUser.getPassword());
        } else {
            throw new UsernameNotFoundException("no user found");
        }
    }
}
