package edu.sabanciuniv.howudoin.security;

import java.util.UUID;

import edu.sabanciuniv.howudoin.security.DTO.LoginRequest;
import edu.sabanciuniv.howudoin.security.DTO.SignUpRequest;
import edu.sabanciuniv.howudoin.security.DTO.JwtAuthenticationResponse;
import edu.sabanciuniv.howudoin.security.DTO.ApiResponse;

import edu.sabanciuniv.howudoin.users.UserModel;
import edu.sabanciuniv.howudoin.users.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController
{
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtWebTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest)
    {
        Authentication authentication;
        authentication = authenticationManager.authenticate
                (
                new UsernamePasswordAuthenticationToken
                        (loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = tokenProvider.generateToken(userDetails);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Email is already taken!"));
        }

        UserModel user = new UserModel();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmailVerified(false);

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);

        userService.createUser(user);

        // In a real application, you would send an email here with the verification link
        // For testing, return the token in the response
        return ResponseEntity.ok(new ApiResponse(true,
                "User registered successfully. Verification token: " + verificationToken));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        UserModel user = userService.findByEmailVerificationToken(token);
        if (user != null) {
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null); // Clear the token after use
            userService.updateUser(user.getId(), user);
            return ResponseEntity.ok(new ApiResponse(true, "Email verified successfully"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid verification token"));
    }
}