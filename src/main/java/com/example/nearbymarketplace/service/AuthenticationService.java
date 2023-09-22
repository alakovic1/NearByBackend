package com.example.nearbymarketplace.service;

import com.example.nearbymarketplace.request.SignInRequest;
import com.example.nearbymarketplace.request.SignUpRequest;
import com.example.nearbymarketplace.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    //JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SignInRequest request);
}
