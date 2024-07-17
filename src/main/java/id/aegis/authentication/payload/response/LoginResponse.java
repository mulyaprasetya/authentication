package id.aegis.authentication.payload.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private long expiresIn;
    
}
