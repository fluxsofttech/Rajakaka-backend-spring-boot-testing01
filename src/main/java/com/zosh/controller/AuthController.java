package com.zosh.controller;


import com.zosh.domain.USER_ROLE;
import com.zosh.dto.GoogleDto;
import com.zosh.exception.SellerException;
import com.zosh.exception.UserException;
import com.zosh.model.*;
import com.zosh.repository.VerificationCodeRepository;
import com.zosh.request.ResetPasswordRequest;
import com.zosh.request.SignupRequest;
import com.zosh.service.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zosh.request.LoginRequest;
import com.zosh.response.ApiResponse;
import com.zosh.response.AuthResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentLoginOtp(
            @RequestBody VerificationCode req) throws MessagingException, UserException {

        authService.sentLoginOtp(req.getEmail());

        ApiResponse res = new ApiResponse();
        res.setMessage("otp sent");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(
            @Valid
            @RequestBody SignupRequest req)
            throws SellerException {


        String token = authService.createUser(req);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        authResponse.setRole(USER_ROLE.ROLE_CUSTOMER);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) throws SellerException {

        AuthResponse authResponse = authService.signin(loginRequest);
       System.out.println("Jwt of email + otp "+authResponse.getJwt());
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
    
   

//    @PostMapping("/google")
//    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleDto googleUser) throws UserException {
//        // Process the user (create new or fetch existing)
//        User user = authService.processFirebaseUser(googleUser);
//
//        // Generate your own HS256 JWT
//        String token = authService.generateToken(user.getEmail());
//
//        AuthResponse authResponse = new AuthResponse();
//        authResponse.setJwt(token);
//        authResponse.setMessage("Login Successful");
//        authResponse.setRole(user.getRole());
//
//        return new ResponseEntity<>(authResponse, HttpStatus.OK);
//    }
//    @PostMapping("/google")
//    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleDto googleUser) throws UserException {
//    	String email=googleUser.getEmail();
//    	String name=googleUser.getName();
//    	System.out.println("THis is email and passwor"+email+" "+name);
//        String token = authService.googleLoginOrSignup(email, name);
//        System.out.println("🟢 Backend issued JWT: " + token);
//
//        AuthResponse authResponse = new AuthResponse();
//        authResponse.setJwt(token);
//        authResponse.setMessage("Google Login Success");
//        authResponse.setRole(USER_ROLE.ROLE_CUSTOMER);
//
//        return new ResponseEntity<>(authResponse, HttpStatus.OK);
//    }
    
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleDto googleUser) throws UserException {
        System.out.println("🔎 Google login request → email: " + googleUser.getEmail() + ", name: " + googleUser.getName());

        AuthResponse authResponse = authService.googleLoginOrSignup(googleUser.getEmail(), googleUser.getName());

        System.out.println("🟢 Backend issued JWT: " + authResponse.getJwt());

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }


    
    



}
