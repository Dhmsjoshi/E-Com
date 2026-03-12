package dev.dharam.authservice.exception;

public class ResourceAlreadyExistException extends RuntimeException{
    public ResourceAlreadyExistException(String message){
        super(message);
    }
}
