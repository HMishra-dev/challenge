package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferUpdate;
import com.dws.challenge.domain.TransferRequest;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.NotEnoughFundsException;
import com.dws.challenge.exception.TransferBetweenSameAccountException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class AccountsService {

    private final Lock lock = new ReentrantLock();

    @Getter
    private final AccountsRepository accountsRepository;

    @Getter
    private final NotificationService notificationService;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }


    public void transferMoney(TransferRequest transfer) throws AccountNotFoundException,
            NotEnoughFundsException, TransferBetweenSameAccountException {
        lock.lock();
        try {
          final Account accountFrom = accountsRepository.getAccount(transfer.getAccountFromId());
          final Account accountTo = accountsRepository.getAccount(transfer.getAccountToId());
          final BigDecimal amount = transfer.getAmount();

            //transferValidator.validate(accountFrom, accountTo, transfer);
           if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughFundsException("Not enough funds in the account");
             }

           if (accountFrom.getAccountId().equals(accountTo.getAccountId())) {
            throw new TransferBetweenSameAccountException("Cannot transfer between the same account");
             }

            boolean success = accountsRepository.transferUpdates(Arrays.asList(
                    new TransferUpdate(accountFrom.getAccountId(), amount.negate()),
                    new TransferUpdate(accountTo.getAccountId(), amount)
            ));

            if (success) {
                notificationService.notifyAboutTransfer(accountFrom, "Transferred " + amount + " to account ID: " + accountTo.getAccountId());
                notificationService.notifyAboutTransfer(accountTo, "Received " + amount + " from account ID: " + accountFrom.getAccountId());
            }

        } finally {
            lock.unlock();
        }
    }
}

