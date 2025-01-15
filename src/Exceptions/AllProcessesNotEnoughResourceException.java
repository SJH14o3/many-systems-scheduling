package Exceptions;

public class AllProcessesNotEnoughResourceException extends Exception {
    public AllProcessesNotEnoughResourceException() {}
    public AllProcessesNotEnoughResourceException(String message) {
        super(message);
    }
}
