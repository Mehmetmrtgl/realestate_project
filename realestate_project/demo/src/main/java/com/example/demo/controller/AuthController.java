package com.example.demo.controller;

import com.example.demo.entities.Client;
import com.example.demo.repositories.ClientRepository;
import com.example.demo.requests.UserLoginRequest;
import com.example.demo.responses.UserInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/login")
    public ResponseEntity<UserInfoResponse> login(@RequestBody UserLoginRequest request) {
        Client client = clientRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());
        if (client != null) {
            UserInfoResponse userInfo = new UserInfoResponse(client.getId(), client.getUserType());
            return ResponseEntity.ok().body(userInfo);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
