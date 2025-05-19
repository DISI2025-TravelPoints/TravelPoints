package org.example.userservice.dto.userdto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.userservice.entity.UserRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSyncDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
