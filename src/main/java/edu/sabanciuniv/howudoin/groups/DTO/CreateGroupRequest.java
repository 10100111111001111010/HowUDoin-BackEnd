package edu.sabanciuniv.howudoin.groups.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class CreateGroupRequest
{
    @NotBlank(message = "Group name is required")
    private String name;

    @NotEmpty(message = "Group must have at least one member")
    private Set<String> memberIds;
}