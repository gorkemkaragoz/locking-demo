package com.example.lockingdemo.service.impl;

import com.example.lockingdemo.entity.BankAccount;
import com.example.lockingdemo.repository.BankAccountRepository;
import com.example.lockingdemo.service.OptimisticTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimisticTransferServiceImpl implements OptimisticTransferService {

    private final BankAccountRepository bankAccountRepository;

    @Override
    @Transactional
    public void transfer(Long fromId, Long toId, Double amount) {
        try {
            BankAccount from = bankAccountRepository.findById(fromId)
                    .orElseThrow(() -> new RuntimeException("Hesap bulunamadı: " + fromId));

            BankAccount to = bankAccountRepository.findById(toId)
                    .orElseThrow(() -> new RuntimeException("Hesap bulunamadı: " + toId));

            if (from.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Yetersiz Bakiye");
            }

            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);

            simulateDelay(500);

            bankAccountRepository.saveAndFlush(from);
            bankAccountRepository.saveAndFlush(to);

            log.info("[OPTIMISTIC] Transfer başarılı. {} -> {}, miktar: {}", fromId, toId, amount);

        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("[OPTIMISTIC] Çakışma tespit edildi! Version uyuşmazlığı.");
            throw new RuntimeException("Eş zamanlı güncelleme çakışması. Lütfen tekrar deneyin.");
        }
    }

    private void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}