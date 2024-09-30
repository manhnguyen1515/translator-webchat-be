package com.translator.webchat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSessionDTO {

    @NotBlank(message = "UserName must not be blank")
    private String username1;

    @NotBlank(message = "UserName must not be blank")
    private String username2;
}
