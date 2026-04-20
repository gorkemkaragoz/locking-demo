package com.example.lockingdemo.controller;

import com.example.lockingdemo.entity.BankAccount;
import com.example.lockingdemo.repository.BankAccountRepository;
import com.example.lockingdemo.service.OptimisticTransferService;
import com.example.lockingdemo.service.PessimisticTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountRepository bankAccountRepository;
    private final OptimisticTransferService optimisticTransferService;
    private final PessimisticTransferService pessimisticTransferService;

    @GetMapping
    public List<BankAccount> getAll() {
        return bankAccountRepository.findAll();
    }

    @PostMapping("/init")
    public ResponseEntity<String> init() {
        bankAccountRepository.deleteAll();
        bankAccountRepository.save(new BankAccount("Alice", 1000.0));
        bankAccountRepository.save(new BankAccount("Bob", 1000.0));
        return ResponseEntity.ok("Alice ve Bob oluşturuldu. Bakiye: 1000.0");
    }

    @PostMapping("/transfer/optimistic")
    public ResponseEntity<String> optimisticTransfer() throws InterruptedException {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        Long aliceId = accounts.get(0).getId();
        Long bobId = accounts.get(1).getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> optimisticTransferService.transfer(aliceId, bobId, 100.0));
        executor.submit(() -> optimisticTransferService.transfer(aliceId, bobId, 200.0));

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        return ResponseEntity.ok("Optimistic transfer testi tamamlandı. Logları kontrol et.");
    }

    @PostMapping("/transfer/pessimistic")
    public ResponseEntity<String> pessimisticTransfer() throws InterruptedException {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        Long aliceId = accounts.get(0).getId();
        Long bobId = accounts.get(1).getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> pessimisticTransferService.transfer(aliceId, bobId, 100.0));
        executor.submit(() -> pessimisticTransferService.transfer(aliceId, bobId, 200.0));

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        return ResponseEntity.ok("Pessimistic transfer testi tamamlandı. Logları kontrol et.");
    }

}
