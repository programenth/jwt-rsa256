package com.example.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.security.jwt.keystore-path}")
    private String keystorePath;

    @Value("${app.security.jwt.keystore-password}")
    private String keystorePassword;

    @Value("${app.security.jwt.key-alias}")
    private String keyAlias;

    private Algorithm rsaAlgorithm;

    @PostConstruct
    public void init() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream is = getClass().getResourceAsStream(keystorePath)) {
            keyStore.load(is, keystorePassword.toCharArray());
        }

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keystorePassword.toCharArray());
        RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(keyAlias).getPublicKey();

        this.rsaAlgorithm = Algorithm.RSA256(publicKey, (RSAPrivateKey) privateKey);
    }

    public String generateToken(String username) {
        return JWT.create()
          .withSubject(username)
          .withIssuer("my-spring-app")
          .withIssuedAt(new Date())
          .withExpiresAt(new Date(System.currentTimeMillis() + 900_000)) // 15 mins
          .sign(rsaAlgorithm);
    }

    public DecodedJWT validateToken(String token) {
        JWTVerifier verifier = JWT.require(rsaAlgorithm)
          .withIssuer("my-spring-app")
          .build();
        return verifier.verify(token);
    }
}