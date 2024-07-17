package id.aegis.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.aegis.authentication.model.CustomResponse;
import id.aegis.authentication.model.User;
import id.aegis.authentication.payload.request.Login;
import id.aegis.authentication.payload.request.Register;
import id.aegis.authentication.payload.response.LoginResponse;
import id.aegis.authentication.services.EmailService;
import id.aegis.authentication.services.JwtService;
import id.aegis.authentication.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<CustomResponse<User>> register(@RequestBody Register register) {
        User registeredUser = userService.signup(register);
        // Kirim email setelah pengguna berhasil didaftarkan
        String subject = "Your Registration Details";
        String text = "Dear " + registeredUser.getUsername() + ",\n\nYour registration is successful. " +
                  "Here is your password: " + register.getPassword() + "\n\nThank you!";
        emailService.sendEmail(registeredUser.getEmail(), subject, text);
        CustomResponse<User> response = new CustomResponse<>(registeredUser, 200, "User registered successfully");
        
    return ResponseEntity.ok(response);
}

@PostMapping("/login")
public ResponseEntity<CustomResponse<LoginResponse>> authenticate(@RequestBody Login login) {
    User authenticatedUser = userService.authenticate(login);
    String jwtToken = jwtService.generateToken(authenticatedUser);
    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setToken(jwtToken);
    loginResponse.setExpiresIn(jwtService.getExpirationTime());
    CustomResponse<LoginResponse> response = new CustomResponse<>(loginResponse, 200, "Login successful");

    return ResponseEntity.ok(response);
}
    
}
