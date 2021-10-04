package io.github.archeox.lgunenuit.exception;

public class RoleConfigException extends RuntimeException{

    private String preciseMessage;

    public RoleConfigException(String message, String preciseMessage) {
        super(message);
        this.preciseMessage = preciseMessage;
    }

    public String getPreciseMessage() {
        return preciseMessage;
    }
}
