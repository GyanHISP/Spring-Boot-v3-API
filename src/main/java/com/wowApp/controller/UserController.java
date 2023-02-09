package com.wowApp.controller;

import com.wowApp.entity.User;
import com.wowApp.payload.request.ChangePassword;
import com.wowApp.payload.request.VerifyOtp;
import com.wowApp.payload.request.EmailDetails;
import com.wowApp.payload.request.LogIn;
import com.wowApp.payload.response.UserProfile;
import com.wowApp.repository.UserRepository;
import com.wowApp.service.JwtService;
import com.wowApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    @PostMapping("/register")
    public ResponseEntity<UserProfile> register(@RequestBody User request) {
        try {
            return ResponseEntity.ok(userService.save(request));
        }catch (Exception e){
            return  ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/login")
    public ResponseEntity<UserProfile> logIn(@RequestBody LogIn request) {
        try {
            return ResponseEntity.ok(userService.logIn(request));
        }catch (Exception e ){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<UserProfile> updateProfile(@RequestBody User request, Principal principal){
        try {
            if (!principal.getName().equals(request.getEmail())) throw new IllegalArgumentException();
            return ResponseEntity.ok(userService.update(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> sendMail(@RequestBody String email) {
        try {
            return ResponseEntity.ok(userService.forgetPassword(email));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/verifyOtp")
    public ResponseEntity<String> changePassword(@RequestBody VerifyOtp request) {
        try{
            return ResponseEntity.ok(userService.verifyOtp(request));
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/reSendOTP")
    public ResponseEntity<String>reSendOTP(@RequestBody String email){
        try{
            return ResponseEntity.ok(userService.reSendOTP(email));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/changePassword")
    public ResponseEntity<UserProfile> changePassword(@RequestBody ChangePassword request) {
        try {
            return ResponseEntity.ok(userService.changePassword(request));
        }catch(Exception e ){
            return ResponseEntity.badRequest().build();
        }
    }

}
