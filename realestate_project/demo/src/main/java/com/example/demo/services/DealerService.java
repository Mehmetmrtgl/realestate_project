package com.example.demo.services;

import com.example.demo.entities.Client;
import com.example.demo.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealerService {
    private final ClientRepository clientRepository;
    @Autowired
    public DealerService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client saveOneClient(Client newClient) {
        return clientRepository.save(newClient);
    }

    public Client getOneDealerById(Long clientId) {
        return clientRepository.findById(clientId).orElse(null);
    }




}
