package com.machine.auth.security;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/** In-memory revocation list. For multi-instance production deployments, use Redis or a database. */
@Component
public class TokenBlacklist {
    private final Set<String> revoked=ConcurrentHashMap.newKeySet();
    public void revoke(String token){revoked.add(token);}
    public boolean isRevoked(String token){return revoked.contains(token);}
}
