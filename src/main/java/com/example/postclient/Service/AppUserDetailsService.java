package com.example.postclient.Service;

import com.example.postclient.Config.AppUserDetails;
import com.example.postclient.Models.AppUser;
import com.example.postclient.Repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private AppUserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Optional<AppUser> user = repository.findByUsername(username);
        return user.map(AppUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(("username not found")));
    }
}

