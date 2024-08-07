package com.dws.challenge.service;


import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferRequest;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.NotEnoughFundsException;

interface TransferValidator {

    void validate(final Account accountFrom, final Account accountTo, final TransferRequest transfer) throws AccountNotFoundException, NotEnoughFundsException;

}
