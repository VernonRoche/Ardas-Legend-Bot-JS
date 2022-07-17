package com.ardaslegends.data.service.exceptions;

import com.ardaslegends.data.domain.Player;

public class PlayerServiceException extends ServiceException {

    public static final String NOT_REGISTERED = "You are not registered! please register your account with /register !";

    public static PlayerServiceException notRegistered() { return new PlayerServiceException(NOT_REGISTERED); }

    protected PlayerServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected PlayerServiceException(String message) {
        super(message);
    }
}
