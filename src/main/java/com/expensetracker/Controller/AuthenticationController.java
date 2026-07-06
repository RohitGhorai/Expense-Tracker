package com.expensetracker.Controller;

import com.expensetracker.Entities.User;
import com.expensetracker.Exceptions.ApiException;
import com.expensetracker.Helpers.AuthenticationRequest;
import com.expensetracker.Helpers.AuthenticationResponse;
import com.expensetracker.Security.JwtService;
import com.expensetracker.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createToken(@RequestBody AuthenticationRequest request) {
        authenticate(request.getUsername(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = null;
        try {
            token = jwtService.generateToken(userDetails);
        } catch (Exception e) {
            throw new ApiException("Unable to generate token", HttpStatus.CONFLICT);
        }
        AuthenticationResponse authResponse = new AuthenticationResponse(token);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private void authenticate(String username, String password){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new ApiException("Bad Credentials", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerNewUser(@Valid @RequestBody User user){
        User userResponse = userService.createUser(user);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
}
