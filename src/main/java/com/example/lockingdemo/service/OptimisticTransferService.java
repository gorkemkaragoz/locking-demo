package com.example.lockingdemo.service;

import com.example.lockingdemo.entity.BankAccount;

public interface OptimisticTransferService {
    void transfer(Long fromId, Long toId, Double amount);
}
