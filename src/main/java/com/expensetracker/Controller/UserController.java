package com.expensetracker.Controller;

import com.expensetracker.Entities.User;
import com.expensetracker.Helpers.ApiResponse;
import com.expensetracker.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/authUser")
    public ResponseEntity<User> getAuthUser(){
        User authUser = userService.getAuthUser();
        return new ResponseEntity<>(authUser, HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId){
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.FOUND);
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable int userId){
        User updateUser = userService.updateUserById(user, userId);
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }
    @PatchMapping("/users/{userId}")
    public ResponseEntity<User> updateUserRole(@PathVariable int userId, @RequestParam String role) {
        User updatedUser = userService.updateUserRole(userId, role);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUserById(@PathVariable int userId){
        userService.deleteUser(userId);
        ApiResponse response = new ApiResponse("User deleted successfully!!", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
