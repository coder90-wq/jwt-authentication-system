package com.machine.auth.auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private record Entry(String code,Instant expiresAt){}
    private final ConcurrentHashMap<String,Entry> entries=new ConcurrentHashMap<>();
    private final SecureRandom random=new SecureRandom();
    private final long ttl;
    public OtpService(@Value("${app.otp.expiration-ms}") long ttl){this.ttl=ttl;}
    public String create(String email){
        String code=String.format("%06d",random.nextInt(1_000_000));
        entries.put(email,new Entry(code,Instant.now().plusMillis(ttl)));
        return code;
    }
    public boolean verify(String email,String code){
        Entry e=entries.get(email);
        if(e==null || Instant.now().isAfter(e.expiresAt()) || !e.code().equals(code)) return false;
        entries.remove(email); return true;
    }
}
