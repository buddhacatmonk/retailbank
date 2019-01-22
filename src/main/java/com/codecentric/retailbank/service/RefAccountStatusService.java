package com.codecentric.retailbank.service;

import com.codecentric.retailbank.model.domain.RefAccountStatus;
import com.codecentric.retailbank.repository.BankAccountRepository;
import com.codecentric.retailbank.repository.RefAccountStatusRepository;
import com.codecentric.retailbank.repository.TransactionRepository;
import com.codecentric.retailbank.service.interfaces.IRefAccountStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RefAccountStatusService implements IRefAccountStatusService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RefAccountStatusRepository refAccountStatusRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    @Override public RefAccountStatus getById(Long id) {
        RefAccountStatus refAccountStatus = refAccountStatusRepository.getSingle(id);
        return refAccountStatus;
    }

    @Override public RefAccountStatus getByCode(String code) {
        RefAccountStatus refAccountStatus = refAccountStatusRepository.getSingleByCode(code);
        return refAccountStatus;
    }

    @Override public List<RefAccountStatus> getAllRefAccountStatus() {
        List<RefAccountStatus> refAccountStatuses = refAccountStatusRepository.findAll();
        return refAccountStatuses;
    }

    @Override public RefAccountStatus addRefAccountStatus(RefAccountStatus refAccountStatus) {
        RefAccountStatus result = refAccountStatusRepository.add(refAccountStatus);
        return result;
    }

    @Override public RefAccountStatus updateRefAccountStatus(RefAccountStatus refAccountStatus) {
        RefAccountStatus result = refAccountStatusRepository.update(refAccountStatus);
        return result;
    }

    @Override public void deleteRefAccountStatus(RefAccountStatus refAccountStatus) {
        // Recursively find and delete any FK constraints to this refAccountStatus
//        bankAccountRepository.findByStatus(refAccountStatus).forEach(bankAccount -> {
//            transactionRepository.findByAccount(bankAccount).forEach(transaction -> {
//                transactionRepository.delete(transaction);
//            });
//            bankAccountRepository.delete(bankAccount);
//        });

        // Delete the actual refAccountStatus
        refAccountStatusRepository.delete(refAccountStatus);
    }

    @Override public void deleteRefAccountStatus(Long id) {
        RefAccountStatus refAccountStatus = refAccountStatusRepository.getSingle(id);

        // Recursively find and delete any FK constraints to this refAccountStatus
//        bankAccountRepository.findByStatus(refAccountStatus).forEach(bankAccount -> {
//            transactionRepository.findByAccount(bankAccount).forEach(transaction -> {
//                transactionRepository.delete(transaction);
//            });
//            bankAccountRepository.delete(bankAccount);
//        });

        // Delete the actual refAccountStatus
        refAccountStatusRepository.delete(refAccountStatus);
    }
}
