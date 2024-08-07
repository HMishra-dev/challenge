package com.dws.challenge.exception;

public class NotEnoughFundsException extends RuntimeException {

    public NotEnoughFundsException(String message){
        super(message);
    }
}
