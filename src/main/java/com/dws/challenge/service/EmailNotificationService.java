package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailNotificationService implements NotificationService {

  @Override
  public void notifyAboutTransfer(Account account, String transferDetails) {
    //Do NOT provide implementation for this service - it is assumed another colleague would implement it.
    log
      .info("Sending notification to owner of {}: {}", account.getAccountId(), transferDetails);
  }

}
