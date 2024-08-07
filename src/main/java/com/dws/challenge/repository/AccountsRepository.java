package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferUpdate;
import com.dws.challenge.exception.DuplicateAccountIdException;

import java.util.List;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();

  boolean transferUpdates(List<TransferUpdate> transferUpdates);

}
