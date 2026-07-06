package com.expensetracker.Services.Impl;

import com.expensetracker.Entities.User;
import com.expensetracker.Exceptions.ApiException;
import com.expensetracker.Exceptions.ResourceNotFoundException;
import com.expensetracker.Repositories.UserRepository;
import com.expensetracker.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @CacheEvict(value = "users", key = "'allUsers'")
    public User createUser(User user) {
        if (userRepo.existsByEmail(user.getEmail())) throw new ApiException("User already exists with this email : " + user.getEmail(), HttpStatus.CONFLICT);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        return userRepo.save(user);
    }

    @Override
    public User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByEmail(authentication.getName());
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public User getUserById(int id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "Id", id));
        return user;
    }

    @Override
    @Cacheable(value = "users", key = "'allUsers'")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    @Caching(put = @CachePut(value = "users", key = "#id"),
            evict = @CacheEvict(value = "users", key = "'allUsers'"))
    public User updateUserById(User user, int id) {
        User oldUser = getUserById(id);
        oldUser.setName(user.getName());
        oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(oldUser);
    }

    @Override
    @Caching(put = @CachePut(value = "users", key = "#id"),
            evict = @CacheEvict(value = "users", key = "'allUsers'"))
    public User updateUserRole(int id, String role) {
        User user = getUserById(id);
        user.setRole(role);
        return userRepo.save(user);
    }

    @Override
    @Caching(evict = {
                    @CacheEvict(value = "users", key = "#id"),
                    @CacheEvict(value = "users", key = "'allUsers'")
            }
    )
    public void deleteUser(int id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepo.save(user);
    }
}
