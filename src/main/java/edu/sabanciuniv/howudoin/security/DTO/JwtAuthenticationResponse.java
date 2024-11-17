// JwtAuthenticationResponse.java
package edu.sabanciuniv.howudoin.security.DTO;

import lombok.Data;

@Data
public class JwtAuthenticationResponse
{
    private String accessToken;
    private final String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken)
    {
        this.accessToken = accessToken;
    }
}