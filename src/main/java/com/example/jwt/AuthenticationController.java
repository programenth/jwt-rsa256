package com.example.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final JwtService jwtService;

    public AuthenticationController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/tokens/{username}")
    public String generateUserToken(@PathVariable String username) {
        return jwtService.generateToken(username);
    }

    @GetMapping("/tokens/validate")
    public DecodedJWT validateUserToken(@RequestParam("token") String token) {
        return jwtService.validateToken(token);
    }
}