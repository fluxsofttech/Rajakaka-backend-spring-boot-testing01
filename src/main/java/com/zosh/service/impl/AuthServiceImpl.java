package com.zosh.service.impl;

import com.zosh.config.JwtProvider;
import com.zosh.domain.USER_ROLE;
import com.zosh.dto.GoogleDto;
import com.zosh.exception.SellerException;
import com.zosh.exception.UserException;
import com.zosh.model.Cart;
import com.zosh.model.PasswordResetToken;
import com.zosh.model.User;
import com.zosh.model.VerificationCode;
import com.zosh.repository.CartRepository;
import com.zosh.repository.UserRepository;
import com.zosh.repository.VerificationCodeRepository;
import com.zosh.request.LoginRequest;
import com.zosh.request.ResetPasswordRequest;
import com.zosh.request.SignupRequest;
import com.zosh.response.ApiResponse;
import com.zosh.response.AuthResponse;
import com.zosh.service.AuthService;
import com.zosh.service.EmailService;
import com.zosh.service.UserService;
import com.zosh.utils.OtpUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;
    private final CustomeUserServiceImplementation customUserDetails;
    private final CartRepository cartRepository;
    
  
    
//    public User processFirebaseUser(GoogleDto googleUser) throws UserException {
//        // Check if user exists
//        User existingUser = userRepository.findByEmail(googleUser.getEmail());
//
//        if (existingUser!=null) {
//            return existingUser;
//        }
//
//        // Else create new user
//        User newUser = new User();
//        newUser.setEmail(googleUser.getEmail());
//        newUser.setFullName(googleUser.getName());
//        newUser.setFirebaseUid(googleUser.getFirebaseUid());
//        newUser.setRole(USER_ROLE.ROLE_CUSTOMER);
//
//        return userRepository.save(newUser);
//    }
//
//    public String generateToken(String email) {
//        // your HS256 JWT generation logic
//        return jwtProvider.generateToken(email);
//    }


//    @Override
//    public String googleLoginOrSignup(String email, String name) throws UserException {
//        // Step 1: Check if user exists
//        User user = userRepository.findByEmail(email);
//
//        if (user == null) {
//            // Step 2: New Google user → create record
//            user = new User();
//            user.setEmail(email);
//            user.setFullName(name);
//            user.setRole(USER_ROLE.ROLE_CUSTOMER);
//            user.setMobile("0000000000"); // default
//            user.setPassword(passwordEncoder.encode("google-auth")); // dummy password
//
//            user = userRepository.save(user);
//
//            Cart cart = new Cart();
//            cart.setUser(user);
//            cartRepository.save(cart);
//        } else {
//            // Step 3: Existing customer → optional update
//            user.setFullName(name); // keep profile info fresh
//            userRepository.save(user);
//        }
//
//        // Step 4: Authorities
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
//
//        // Step 5: Create Authentication object
//        Authentication authentication =
//                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
//
//        // Step 6: Put into SecurityContext
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Step 7: Generate JWT
//        return jwtProvider.generateToken(authentication);
//    }
    
    @Override
    public AuthResponse googleLoginOrSignup(String email, String name) throws UserException {
        // Step 1: Check if user exists
        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setRole(USER_ROLE.ROLE_CUSTOMER);
            user.setMobile("0000000000");
            user.setPassword(passwordEncoder.encode("google-auth"));
            user = userRepository.save(user);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        } else {
            user.setFullName(name);
            userRepository.save(user);
        }

        // Authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        // Authentication
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT
        String token = jwtProvider.generateToken(authentication);

        // ✅ Return structured AuthResponse (same as signin)
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Google Login Success");
        authResponse.setRole(user.getRole());

        return authResponse;
    }


    @Override
    public void sentLoginOtp(String email) throws UserException, MessagingException {


        String SIGNING_PREFIX = "signing_";

        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());
            userService.findUserByEmail(email);
        }

        VerificationCode isExist = verificationCodeRepository
                .findByEmail(email);

        if (isExist != null) {
            verificationCodeRepository.delete(isExist);
        }

        String otp = OtpUtils.generateOTP();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = "Rajakaka Premimum Login/Signup Otp";
        String text = "your login otp is - ";
        emailService.sendVerificationOtpEmail(email, otp, subject, text);
    }

    @Override
    public String createUser(SignupRequest req) throws SellerException {

        String email = req.getEmail();

        String fullName = req.getFullName();

        String otp = req.getOtp();

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("wrong otp...");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {

            User createdUser = new User();
            createdUser.setEmail(email);
            createdUser.setFullName(fullName);
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("9083476123");
            createdUser.setPassword(passwordEncoder.encode(otp));

            System.out.println(createdUser);

            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }


        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(
                USER_ROLE.ROLE_CUSTOMER.toString()));


        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signin(LoginRequest req) throws SellerException {

        String username = req.getEmail();
        String otp = req.getOtp();

        System.out.println(username + " ----- " + otp);

        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login Success");
        authResponse.setJwt(token);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();


        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();


        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;

    }



    private Authentication authenticate(String username, String otp) throws SellerException {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        System.out.println("sign in userDetails - " + userDetails);

        if (userDetails == null) {
            System.out.println("sign in userDetails - null ");
            throw new BadCredentialsException("Invalid username or password");
        }
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new SellerException("wrong otp...");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

//    @Override
//    public String googleLoginOrSignup(String email, String name) throws UserException {
//        User user = userRepository.findByEmail(email);
//
//        if (user == null) {
//            // New Google user -> create account
//            user = new User();
//            user.setEmail(email);
//            user.setFullName(name);
//            user.setRole(USER_ROLE.ROLE_CUSTOMER);
//            user.setMobile("0000000000"); // optional default
//            user.setPassword(passwordEncoder.encode("google-auth")); // dummy password for record
//
//            user = userRepository.save(user);
//
//            // also create empty cart
//            Cart cart = new Cart();
//            cart.setUser(user);
//            cartRepository.save(cart);
//        }
//
//        // Set authorities
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));
//
//        // Create authentication object
//        Authentication authentication =
//                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
//
//        // Put into security context
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Generate JWT for this user
//        return jwtProvider.generateToken(authentication);
//    }

}
