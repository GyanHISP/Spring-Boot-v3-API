package com.wowApp.service;

import java.util.Collection;
import com.wowApp.entity.User;
import com.wowApp.payload.request.ChangePassword;
import com.wowApp.payload.request.VerifyOtp;
import com.wowApp.payload.request.LogIn;
import com.wowApp.payload.response.UserProfile;

public interface UserService {
    UserProfile logIn(LogIn request);
    Collection<User> findByRole(String role);
    UserProfile save(User user);
    UserProfile update(User user);
    UserProfile changePassword(ChangePassword request);
    String forgetPassword(String email);
    String reSendOTP(String email);
    String verifyOtp(VerifyOtp otp);
}
