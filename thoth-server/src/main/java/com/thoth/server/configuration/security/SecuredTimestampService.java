package com.thoth.server.configuration.security;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Formatter;

@Service
public class SecuredTimestampService {

    private final String secret;

    private final int defaultTokenDuration;

    public SecuredTimestampService(@Value("${thoth.security.secret}") String secret, @Value("${thoth.security.timestamp.token.duration:180}") int defaultTokenDuration) throws NoSuchAlgorithmException {
        this.secret = secret;
        this.defaultTokenDuration = defaultTokenDuration;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String generate() {
        return generate(defaultTokenDuration);
    }

    public String generate(int duration) {
        var timestamp = Instant.now();
        MessageDigest crypt = null;
        try {
            crypt = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        crypt.reset();
        crypt.update((duration + secret + timestamp.toEpochMilli()).getBytes());
        var sha = byteToHex(crypt.digest());
        var str = timestamp.toEpochMilli() + ":" + duration + ":" + sha;
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public SecuredTimestamp parse(String string) {
        try {
            var str = new String(Base64.getDecoder().decode(string));
            var parts = str.split(":");
            var timestamp = Instant.ofEpochMilli(Long.parseLong(parts[0]));
            var duration = Integer.parseInt(parts[1]);
            var crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update((duration + secret + timestamp.toEpochMilli()).getBytes());
            var sha = byteToHex(crypt.digest());

            if (!sha.equals(parts[2])) {
                throw new AccessDeniedException("Invalid Token");
            }

            if (timestamp.isBefore(Instant.now().minus(duration, ChronoUnit.SECONDS))) {
                throw new AccessDeniedException("Token Expired");
            }

            var t = new SecuredTimestamp();
            t.setTimestamp(timestamp);
            t.setSecret(sha);
            return t;

        } catch (Exception e) {
            if (e instanceof AccessDeniedException ae) {
                throw ae;
            }
            throw new AccessDeniedException("Invalid Token");
        }
    }
}
