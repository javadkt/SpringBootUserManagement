package com.rak.usermanagement.common.service;

import com.rak.usermanagement.common.model.User;
import com.rak.usermanagement.common.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Mohammmed Javad
 * @version 1.0
 */

@Service
public class UserService implements UserDetailsService {

    private User user;
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        user = userRepo.findOneByLoginId(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(user.getLoginId(), user.getPassword(),
                new ArrayList<>());
    }

    public User getUserByLogin(String login) {
        return userRepo.findByLoginId(login);
    }

    public User getUser() {
        return user;
    }

    public User saveUser(User user) {
        return userRepo.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userRepo.findUserById(id));  // Find user by ID
    }


    @Transactional
    public void deleteUserById(Long id) {
        userRepo.deleteUserById(id);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();  // Fetch all users
    }

}
