package org.example.userservice.dto.userdto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDTO {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
