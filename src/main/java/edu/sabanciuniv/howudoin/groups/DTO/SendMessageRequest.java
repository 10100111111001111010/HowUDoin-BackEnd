package edu.sabanciuniv.howudoin.groups.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest
{
    @NotBlank(message = "Message cannot be empty")
    private String content;
}