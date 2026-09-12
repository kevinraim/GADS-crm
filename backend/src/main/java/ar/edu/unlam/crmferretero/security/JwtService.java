package ar.edu.unlam.crmferretero.security;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Firma y valida los JWT con HS256. El secreto viene de JWT_SECRET y tiene que tener al menos
 * 32 caracteres, o la librería falla al arrancar la aplicación.
 */
@Service
public class JwtService {

    private final SecretKey claveSecreta;
    private final long vencimientoMinutos;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                       @Value("${app.jwt.vencimiento-minutos:480}") long vencimientoMinutos) {
        this.claveSecreta = Keys.hmacShaKeyFor(secreto.getBytes());
        this.vencimientoMinutos = vencimientoMinutos;
    }

    public String generarToken(String email, String rol, String nombre, String uid, String distribuidoraId) {
        Date ahora = new Date();
        Date vencimiento = new Date(ahora.getTime() + vencimientoMinutos * 60 * 1000);
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("rol", rol);
        claims.put("nombre", nombre);
        claims.put("uid", uid);
        claims.put("distribuidoraId", distribuidoraId);
        return Jwts.builder()
                .subject(email)
                .claims(claims)
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(claveSecreta)
                .compact();
    }

    public long getVencimientoSegundos() {
        return vencimientoMinutos * 60;
    }

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public String extraerRol(String token) {
        return extraerClaim(token, claims -> claims.get("rol", String.class));
    }

    public String extraerUid(String token) {
        return extraerClaim(token, claims -> claims.get("uid", String.class));
    }

    public String extraerDistribuidoraId(String token) {
        return extraerClaim(token, claims -> claims.get("distribuidoraId", String.class));
    }

    public boolean esValido(String token, String email) {
        try {
            String emailDelToken = extraerEmail(token);
            return emailDelToken.equals(email) && !estaVencido(token);
        } catch (ExpiredJwtException ex) {
            return false;
        }
    }

    private boolean estaVencido(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(claveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}
