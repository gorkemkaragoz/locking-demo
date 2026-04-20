package com.example.lockingdemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;

    private Double balance;

    @Version
    private Long version;

    public BankAccount(String owner, Double balance) {
        this.owner = owner;
        this.balance = balance;
    }
}