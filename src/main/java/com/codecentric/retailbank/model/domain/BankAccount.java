package com.codecentric.retailbank.model.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class BankAccount {

    private Long id;

    @NotNull
    private RefAccountStatus status;

    @NotNull
    private RefAccountType type;

    @NotNull
    private Customer customer;

    @NotNull
    private BigDecimal balance;

    @Size(max = 255)
    private String details;


    public BankAccount(Long id,
                       @NotNull RefAccountStatus status,
                       @NotNull RefAccountType type,
                       @NotNull Customer customer,
                       @NotNull BigDecimal balance,
                       @Size(max = 255) String details) {
        this.id = id;
        this.status = status;
        this.type = type;
        this.customer = customer;
        this.balance = balance;
        this.details = details;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RefAccountStatus getStatus() {
        return status;
    }

    public void setStatus(RefAccountStatus status) {
        this.status = status;
    }

    public RefAccountType getType() {
        return type;
    }

    public void setType(RefAccountType type) {
        this.type = type;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
