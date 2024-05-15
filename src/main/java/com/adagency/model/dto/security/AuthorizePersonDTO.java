package com.adagency.model.dto.security;

import com.adagency.model.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizePersonDTO {
   private Long id;
   private String email;
   private String password;
   private Role role;
}
