package ru.itmo.secureapi.auth;

import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration lifetime;

    public TokenService(JwtEncoder jwtEncoder,
                        @Value("${app.jwt.issuer}") String issuer,
                        @Value("${app.jwt.lifetime}") Duration lifetime) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.lifetime = lifetime;
    }

    public LoginResponse issue(String username) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(lifetime))
                .subject(username)
                .claim("scope", "api.read api.write")
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", lifetime.toSeconds());
    }
}

