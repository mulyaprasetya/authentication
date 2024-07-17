package id.aegis.authentication.payload.request;

import lombok.Data;

@Data
public class Login {
    private String email;
    private String password;
    
}
