package com.rak.usermanagement.common.controller;

import com.rak.usermanagement.common.model.AuthenticationRequest;
import com.rak.usermanagement.common.model.AuthenticationResponse;
import com.rak.usermanagement.common.model.User;
import com.rak.usermanagement.common.service.UserService;
import com.rak.usermanagement.common.util.JwtUtil;
import com.rak.usermanagement.common.util.StringUtils;
import io.swagger.annotations.*;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mohammmed Javad
 * @version 1.0
 */
@Api(value = "User Account Management", tags = {"User Management"})
@RestController
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final UserService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;

    public UserController(AuthenticationManager authenticationManager, JwtUtil jwtTokenUtil, UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, RestTemplate restTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.restTemplate = restTemplate;
    }

    @ApiOperation(value = "Health check", notes = "Simple endpoint to check the health of the service")
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @ApiOperation(value = "Authenticate User", notes = "Authenticate a user and generate a JWT token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully authenticated"),
            @ApiResponse(code = 401, message = "Unauthorized - Invalid credentials")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userDetailsService.getUserByLogin(userDetails.getUsername());
            String authToken = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthenticationResponse(user, authToken));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "Create a new user", notes = "Register a new user with valid details")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created successfully"),
            @ApiResponse(code = 400, message = "Bad request - invalid input")
    })
    @PostMapping("/users")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        if (!StringUtils.isValidPassword(user.getPassword())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Password must be at least 8 characters long and contain only letters and numbers.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        if (userDetailsService.getUserByLogin(user.getLoginId()) != null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User with this login ID already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        User savedUser = userDetailsService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

/*    @ApiOperation(value = "Check if user exists", notes = "Check if a user exists by login ID")
    @GetMapping("/is-user-exists/{loginId}")
    public ResponseEntity<?> isUserExists(@PathVariable String loginId) {
        return userDetailsService.getUserByLogin(loginId) != null ? new ResponseEntity<>(HttpStatus.CONFLICT) : new ResponseEntity<>(HttpStatus.OK);
    }*/

    @ApiOperation(value = "Get user details using ID", notes = "Retrieve user details by user ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User found"),
            @ApiResponse(code = 400, message = "User not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userObj = userDetailsService.getUserById(id);
        return userObj.isPresent() ? ResponseEntity.ok(userObj) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "List all users", notes = "Retrieve all users from the database")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userDetailsService.getAllUsers());
    }

    @ApiOperation(value = "Delete a user", notes = "Delete a user by ID")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userDetailsService.getUserById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userDetailsService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Update user details", notes = "Update a user's details by user ID")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existingUserOptional = userDetailsService.getUserById(id);
        if (!existingUserOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User existingUser = existingUserOptional.get();
        existingUser.setLoginId(updatedUser.getLoginId());
        existingUser.setEmail(updatedUser.getEmail());
        return ResponseEntity.ok(userDetailsService.saveUser(existingUser));
    }

    @ApiOperation(value = "Change User Password", notes = "Change the password of a user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Password changed successfully"),
            @ApiResponse(code = 400, message = "Bad request - Invalid input"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 401, message = "Unauthorized - Invalid credentials")
    })

    @PatchMapping("/users/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordMap) {

        // Validate request input
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");

        if (!StringUtils.isValidPassword(newPassword)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "New password must be at least 8 characters long and contain only letters and numbers.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Optional<User> userOptional = userDetailsService.getUserById(id);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        // Verify the old password
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            return new ResponseEntity<>("Old password is incorrect", HttpStatus.UNAUTHORIZED);
        }

        // Update to the new password
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userDetailsService.saveUser(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
