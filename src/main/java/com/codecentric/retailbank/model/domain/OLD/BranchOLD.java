package com.codecentric.retailbank.model.domain.OLD;

import com.codecentric.retailbank.model.domain.Address;
import com.codecentric.retailbank.model.domain.Bank;
import com.codecentric.retailbank.model.domain.RefBranchType;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "branches")
public class BranchOLD {

    @Id
    @Column(name = "branch_id")
    @GeneratedValue
    private Long id;

    @OneToOne(targetEntity = AddressOLD.class)
    @JoinColumn(name = "address_id")
    private Address address;

    @NotNull
    @OneToOne(targetEntity = BankOLD.class)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @NotNull
    @OneToOne(targetEntity = RefBranchTypeOLD.class)
    @JoinColumn(name = "branch_type_id")
    private RefBranchType type;

    @Length(max = 255)
    @Column(name = "branch_details")
    private String details;


    public BranchOLD() {
        super();
    }

    public BranchOLD(Long id) {
        super();
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public RefBranchType getType() {
        return type;
    }

    public void setType(RefBranchType branchTypeCode) {
        this.type = branchTypeCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    public void setFields(Address address,
                          @NotNull Bank bank,
                          @NotNull RefBranchType type,
                          @Length(max = 255) String details) {
        this.address = address;
        this.bank = bank;
        this.type = type;
        this.details = details;
    }

}
