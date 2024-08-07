package com.dws.challenge.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferUpdate {

    private final String accountId;
    private final BigDecimal amount;

}
