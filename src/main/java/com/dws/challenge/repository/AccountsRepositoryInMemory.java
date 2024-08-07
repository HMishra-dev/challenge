package com.dws.challenge.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferUpdate;
import com.dws.challenge.exception.DuplicateAccountIdException;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    @Override
    public boolean transferUpdates(List<TransferUpdate> transferUpdates) {
        transferUpdates
                .stream()
                .forEach(this::transfrUpdate);

        return true;
    }

    private void transfrUpdate(final TransferUpdate transferUpdate) {
        final String accountId = transferUpdate.getAccountId();
        accounts.computeIfPresent(accountId, (key, account) -> {
            account.setBalance(account.getBalance().add(transferUpdate.getAmount()));
            return account;
        });
    }

}
