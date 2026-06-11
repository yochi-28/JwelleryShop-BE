package org.sharmas.jwelleryshopbe.services;

import org.sharmas.jwelleryshopbe.models.UserModel;
import org.sharmas.jwelleryshopbe.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // 1. Fetch the user from MongoDB
//        UserModel myMongoUser = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//        // 2. Translate UserModel into Spring Security UserDetails
//        return User.withUsername(myMongoUser.getUsername())
//                .password(myMongoUser.getPassword())
//                .roles(myMongoUser.getRole().name()) // Maps your ADMIN/USER enum
//                .build();
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("👉 Attempting login for username: '" + username + "'");

        UserModel myMongoUser = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("❌ ERROR: User not found in database!");
                    return new UsernameNotFoundException("User not found: " + username);
                });

        System.out.println("✅ User found in database! Passing to BCrypt...");

        return User.withUsername(myMongoUser.getUsername())
                .password(myMongoUser.getPassword())
                .roles(myMongoUser.getRole().name())
                .build();
    }
}
