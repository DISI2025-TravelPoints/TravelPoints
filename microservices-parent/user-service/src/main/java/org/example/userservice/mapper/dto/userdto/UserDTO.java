package org.example.user.dto.userdto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    private String name;

    private String email;

    private String password;

    private String role;
}
