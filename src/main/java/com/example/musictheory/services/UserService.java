package com.example.musictheory.services;

import com.example.musictheory.exception.UserAlreadyExistsException;
import com.example.musictheory.models.AppUserRole;
import com.example.musictheory.models.Proficiency;
import com.example.musictheory.models.Role;
import com.example.musictheory.models.User;
import com.example.musictheory.repositories.RoleRepository;
import com.example.musictheory.repositories.UserRepository;
import com.example.musictheory.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    Util util;

    public User findUserByUsername(String username) throws FileNotFoundException {
        Optional<User> response = userRepository.findUserByUsername(username);
        if (response.isEmpty()){
            throw new FileNotFoundException("User not Found. Try Again");
        }

        return response.get();
    }

    public User createNewUser(User user) {

        Optional<User> response = userRepository.findUserByUsername(user.getUsername());
        if (response.isPresent()){
            throw new UserAlreadyExistsException("Username already exists. Retry with new username");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setProficiency(Proficiency.BEGINNER.getProficiency());
            List<Integer> score = new ArrayList<>();
            score.add(0);
            user.setScore(score);
            Role role = new Role(user.getUsername(), AppUserRole.USER.name());
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRole(roles);
//            System.out.println("User" + user.toString());
            roleRepository.save(role);
            return userRepository.save(user);
        }

    }


//    @Secured("ADMIN") // only allows admin to call this service.
//    public void deleteUser(String username){};
//
////    Example:
///     @Secured("ROLE_ADMIN")
////    public void deleteUser(String userId) {
////        // ... only users with ROLE_ADMIN can call this ...
////    }
////
////    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
////    public User getUserDetails(String username) {
////        // ... users with either role can call this ...
////    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
//
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        if(user.isEmpty())
        {
            throw new UsernameNotFoundException("User not found :- " + username);
        }
        List<GrantedAuthority> authorities = user.get().getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),user.get().getPassword(),
                authorities);
    }


}
