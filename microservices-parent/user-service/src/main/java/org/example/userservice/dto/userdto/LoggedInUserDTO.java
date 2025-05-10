package org.example.userservice.dto.userdto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoggedInUserDTO {
    private Long id;
    private String email;
}
