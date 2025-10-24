package com.partcraft.back.controller;

import com.partcraft.back.dto.*;
import com.partcraft.back.exception.AuthException;
import com.partcraft.back.exception.UserServiceException;
import com.partcraft.back.exception.ValidationException;
import com.partcraft.back.security.JwtUtils;
import com.partcraft.back.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService){
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) throws Exception {
        try{
            var user = userService.getUserByEmail(request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );
            var jwtTokens = new JwtTokensDTO();
            jwtTokens.setAccessToken(jwtUtils.generateToken(authentication.getName()));
            jwtTokens.setRefreshToken(jwtUtils.generateRefreshToken(authentication.getName()));
            return ResponseEntity.ok(new AuthResponseDTO(user, jwtTokens));
        } catch (Exception e) {
            if (e instanceof UserServiceException) throw (UserServiceException) e;
            else if (e instanceof ValidationException) throw (ValidationException) e;
            else throw new AuthException("Invalid username or password");
        }

    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDTO> signUp(@RequestBody CreateUserDTO request) throws Exception {
        try {
            var user = userService.createUser(request);
            var jwtTokens = new JwtTokensDTO();
            jwtTokens.setAccessToken(jwtUtils.generateToken(user.getUsername()));
            jwtTokens.setRefreshToken(jwtUtils.generateRefreshToken(user.getUsername()));
            return ResponseEntity.ok(new AuthResponseDTO(user, jwtTokens));
        }catch (Exception e) {
            if (e instanceof UserServiceException) throw (UserServiceException) e;
            else if (e instanceof ValidationException) throw (ValidationException) e;
            else throw new AuthException("Invalid username or password");
        }
    }
    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestHeader("Authorization") String authorizationHeader ) throws AuthException {
        try{
            String refreshToken = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                refreshToken = authorizationHeader.substring(7);
            }
            if (jwtUtils.validateRefreshToken(refreshToken)) {
                String username = jwtUtils.getUsernameFromRefreshToken(refreshToken);
                var userFromDb = userService.getUserByUsername(username);
                if (username != null && userFromDb != null) {
                    String token = jwtUtils.generateToken(userFromDb.getUsername());
                    return ResponseEntity.ok(token);
                }
            }
            throw new AuthException("Invalid refresh token");
        } catch (Exception e) {
            if (e instanceof UserServiceException) throw (UserServiceException) e;
            else if (e instanceof ValidationException) throw (ValidationException) e;
            else if (e instanceof AuthException) throw (AuthException) e;
            else throw new AuthException("Error validating refresh token");
        }
    }

    @GetMapping("/username-availability/{username}")
    public ResponseEntity<Boolean> isUsernameAvailable(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.verifyUsernameAvailability(username));
    }

    @GetMapping("/email-availability/{email}")
    public ResponseEntity<Boolean> isEmailAvailable(@PathVariable String email) {
        return ResponseEntity.ok().body(userService.verifyEmailAvailability(email));
    }
}