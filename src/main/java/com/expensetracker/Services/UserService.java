package com.expensetracker.Services;

import com.expensetracker.Entities.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User getAuthUser();
    User getUserById(int id);
    List<User> getAllUsers();
    User updateUserById(User user, int id);
    User updateUserRole(int id, String role);
    void deleteUser(int id);
}
