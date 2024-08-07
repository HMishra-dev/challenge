package com.dws.challenge.exception;

public class TransferBetweenSameAccountException extends RuntimeException {

    public TransferBetweenSameAccountException(String message){
        super(message);
    }

}
