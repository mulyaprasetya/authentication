package id.aegis.authentication.payload.request;

import lombok.Data;

@Data
public class Register {
    private String email;
    private String password;
    private String fullName;
    private Long roleId;
}
