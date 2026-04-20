package com.example.lockingdemo.service.impl;

import com.example.lockingdemo.entity.BankAccount;
import com.example.lockingdemo.repository.BankAccountRepository;
import com.example.lockingdemo.service.PessimisticTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PessimisticTransferServiceImpl implements PessimisticTransferService {

    private final BankAccountRepository bankAccountRepository;

    @Override
    @Transactional
    public void transfer(Long fromId, Long toId, Double amount) {

        BankAccount from = bankAccountRepository.findByIdWithPessimisticWriteLock(fromId)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı: " + fromId));

        BankAccount to = bankAccountRepository.findByIdWithPessimisticWriteLock(toId)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı: " + toId));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz bakiye");
        }

        simulateDelay(500);

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        bankAccountRepository.saveAndFlush(from);
        bankAccountRepository.saveAndFlush(to);

        log.info("[PESSIMISTIC] Transfer başarılı. {} -> {}, miktar: {}", fromId, toId, amount);
    }


    private void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}