package com.example.lockingdemo.service;

public interface PessimisticTransferService {
    void transfer(Long fromId, Long toId, Double amount);
}
