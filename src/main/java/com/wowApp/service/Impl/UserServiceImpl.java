package com.wowApp.service.Impl;

import com.wowApp.entity.User;
import com.wowApp.enums.ResponseEnum;
import com.wowApp.exception.CustomException;
import com.wowApp.payload.request.ChangePassword;
import com.wowApp.payload.request.VerifyOtp;
import com.wowApp.payload.request.EmailDetails;
import com.wowApp.payload.request.LogIn;
import com.wowApp.payload.response.UserProfile;
import com.wowApp.repository.UserRepository;
import com.wowApp.service.UserService;
import com.wowApp.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    private static final long OTP_VALID_DURATION = 2 * 60 * 1000;   // 5 minutes
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserProfile logIn(LogIn request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            User req = userRepository.findByEmail(request.getEmail());
            var jwtToken = jwtService.generateToken(req);
            UserProfile response = new UserProfile();
            response.setEmail(req.getEmail());
            response.setFirstname(req.getFirstname());
            response.setLastname(req.getLastname());
            response.setRole(req.getRole());
            response.setToken(jwtToken);
            return response;
        } catch (Exception e) {
            throw new CustomException(ResponseEnum.USER_NOT_FOUNT);
        }
    }

    @Override
    public Collection<User> findByRole(String role) {
        return null;
    }

    @Override
    public UserProfile save(User request) {
        try {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(request);
            var jwtToken = jwtService.generateToken(request);
            UserProfile profile = new UserProfile();
            profile.setFirstname(request.getFirstname());
            profile.setLastname(request.getLastname());
            profile.setEmail(request.getEmail());
            profile.setRole(request.getRole());
            profile.setToken(jwtToken);
            return profile;
        } catch (Exception e) {
            throw new CustomException(ResponseEnum.VALID_ERROR);
        }
    }

    @Override
    public UserProfile update(User request) {
        try {
            User oldUser = userRepository.findByEmail(request.getEmail());
            oldUser.setFirstname(request.getFirstname());
            oldUser.setLastname(request.getLastname());
            userRepository.save(oldUser);
            var jwtToken = jwtService.generateToken(oldUser);
            UserProfile profile = new UserProfile();
            profile.setFirstname(oldUser.getFirstname());
            profile.setLastname(oldUser.getLastname());
            profile.setEmail(oldUser.getEmail());
            profile.setRole(oldUser.getRole());
            profile.setToken(jwtToken);
            return profile;
        }catch(Exception e) {
            throw new CustomException(ResponseEnum.USER_NOT_FOUNT);
        }
     }
    @Override
    public String reSendOTP(String email){
        try {
            Random random = new Random();
            int otp = random.nextInt(999999);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            User user = userRepository.findByEmail(email);
            user.setOtp(otp);
            user.setOtprequestedtime(new Date());
            isOTPRequired(user);
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText("Hello "+user.getFirstname()+" \n\n For security reason, you're required to use the following (OTP) One Time Password to reset your password:\n\n"
                    + otp +
                    "\n\n Note: this OTP is set to expire in 5 minutes.\n\n"
            );
            mailMessage.setSubject("OPT");
            javaMailSender.send(mailMessage);
            return "OTP sent successfully with this email. Please Check your mail "+ email;
        }catch (Exception e){
            return "Error while Sending Mail";
        }
    }
    @Override
    public UserProfile changePassword(ChangePassword request) {
              User user = userRepository.findByEmail(request.getEmail());
              try{
                  if(request.getOtp().equals(user.getOtp())){
                      user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                      user.setOtp(null);
                      user.setOtprequestedtime(null);
                      userRepository.save(user);
                      var jwtToken = jwtService.generateToken(user);
                      UserProfile profile = new UserProfile();
                      profile.setFirstname(user.getFirstname());
                      profile.setLastname(user.getLastname());
                      profile.setEmail(user.getEmail());
                      profile.setRole(user.getRole());
                      profile.setToken(jwtToken);
                      return profile;
                  }else{
                      UserProfile profile = new UserProfile();
                      return profile;
                  }
              }catch (Exception e){
                  throw  new CustomException(ResponseEnum.USER_NOT_FOUNT);
              }
        }
    @Override
    public String forgetPassword(String email) {
        try {
            Random random = new Random();
            int otp = random.nextInt(999999);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            User user = userRepository.findByEmail(email);
            user.setOtp(otp);
            user.setOtprequestedtime(new Date());
            isOTPRequired(user);
            mailMessage.setFrom(sender);
            mailMessage.setTo(email);
            mailMessage.setText("Hello "+user.getFirstname()+" \n\n For security reason, you're required to use the following (OTP) One Time Password to reset your password:\n\n"
                      + otp +
                    "\n\n Note: this OTP is set to expire in 5 minutes.\n\n"
                );
            mailMessage.setSubject("OPT");
            javaMailSender.send(mailMessage);
            return "OTP sent successfully with this email. Please Check your mail "+ email;
        }catch (Exception e){
            return "Error while Sending Mail";
        }
    }
    public void isOTPRequired(User user) {
        userRepository.save(user);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               user.setOtp(null);
               user.setOtprequestedtime(null);
               userRepository.save(user);
            }
        }, 300000);
    }
    @Override
    public String verifyOtp(VerifyOtp request) {
        User user = userRepository.findByEmail(request.getEmail());
        try{
            if(request.getOtp().equals(user.getOtp())){
                return "OTP verify success !";
            }else{
                return "Invalid OTP";
            }
        }catch (Exception e){
          throw new CustomException(ResponseEnum.USER_NOT_FOUNT);
        }

    }
}
