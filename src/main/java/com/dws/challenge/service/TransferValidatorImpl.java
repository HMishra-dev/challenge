package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferRequest;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.NotEnoughFundsException;
import com.dws.challenge.exception.TransferBetweenSameAccountException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferValidatorImpl implements TransferValidator {

    @Override
    public void validate(Account accountFrom, Account accountTo, TransferRequest transfer) throws AccountNotFoundException, NotEnoughFundsException, TransferBetweenSameAccountException {
        if (transfer == null) {
            throw new NullPointerException("Transfer request is null");
        }

        validateAccount("Account with ID " + transfer.getAccountFromId(), accountFrom);
        validateAccount("Account with ID " + transfer.getAccountToId(), accountTo);

        if (accountFrom.getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new NotEnoughFundsException("Not enough funds in the account");
        }

        if (accountFrom.getAccountId().equals(accountTo.getAccountId())) {
            throw new TransferBetweenSameAccountException("Cannot transfer between the same account");
        }
    }

    private void validateAccount(String accountDescription, Account account) throws AccountNotFoundException {
        if (account == null) {
            throw new AccountNotFoundException(accountDescription + " not found");
        }
    }
}
