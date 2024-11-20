package edu.sabanciuniv.howudoin.groups.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMemberRequest
{
    @NotBlank(message = "User ID is required")
    private String userId;
}