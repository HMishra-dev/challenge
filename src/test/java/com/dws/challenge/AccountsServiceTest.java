package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.UUID;

import com.dws.challenge.domain.Account;

import com.dws.challenge.domain.TransferRequest;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @MockBean
  private NotificationService notificationService;

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  public void makeTransfer_should_transferFunds() {
    final String accountFromId = UUID.randomUUID().toString();
    final String accountToId = UUID.randomUUID().toString();
    final Account accountFrom = new Account(accountFromId, new BigDecimal("7002.67"));
    final Account accountTo = new Account(accountToId, new BigDecimal("101.67"));

    this.accountsService.createAccount(accountFrom);
    this.accountsService.createAccount(accountTo);

    TransferRequest transfer = new TransferRequest(accountFromId, accountToId, new BigDecimal("4010.67"));

    this.accountsService.transferMoney(transfer);

    assertThat(this.accountsService.getAccount(accountFromId).getBalance()).isEqualTo(new BigDecimal("2992.00"));
    assertThat(this.accountsService.getAccount(accountToId).getBalance()).isEqualTo(new BigDecimal("4112.34"));

    verifyNotifications(accountFrom, accountTo, transfer);
  }

  @Test
  public void makeTransfer_should_transferFunds_when_balanceJustEnough() {

    final String accountFromId = UUID.randomUUID().toString();
    final String accountToId = UUID.randomUUID().toString();
    final Account accountFrom = new Account(accountFromId, new BigDecimal("5550.35"));
    final Account accountTo = new Account(accountToId, new BigDecimal("270.10"));

    this.accountsService.createAccount(accountFrom);
    this.accountsService.createAccount(accountTo);

    TransferRequest transfer = new TransferRequest(accountFromId, accountToId, new BigDecimal("570.01"));

    this.accountsService.transferMoney(transfer);

    assertThat(this.accountsService.getAccount(accountFromId).getBalance()).isEqualTo(new BigDecimal("4980.34"));
    assertThat(this.accountsService.getAccount(accountToId).getBalance()).isEqualTo(new BigDecimal("840.11"));

    verifyNotifications(accountFrom, accountTo, transfer);
  }

  private void verifyNotifications(final Account accountFrom, final Account accountTo, final TransferRequest transfer) {
    verify(notificationService, Mockito.times(1)).notifyAboutTransfer(accountFrom, "Transferred " + transfer.getAmount() + " to account ID: " + accountTo.getAccountId());
    verify(notificationService, Mockito.times(1)).notifyAboutTransfer(accountTo, "Received " + transfer.getAmount() + " from account ID: " + accountFrom.getAccountId());
  }


}
