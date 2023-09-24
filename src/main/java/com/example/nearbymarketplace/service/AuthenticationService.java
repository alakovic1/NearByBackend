package com.example.nearbymarketplace.service;

import com.example.nearbymarketplace.request.SignInRequest;
import com.example.nearbymarketplace.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signin(SignInRequest request);
}
