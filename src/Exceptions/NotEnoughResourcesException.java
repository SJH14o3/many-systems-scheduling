package Exceptions;

public class NotEnoughResourcesException extends Exception {
    public NotEnoughResourcesException(){

    }
    public NotEnoughResourcesException(String message){
        super(message);
    }
}
