package dev.dharam.authservice.exception;

public class ResourceNotFoundExistException extends RuntimeException{
    public ResourceNotFoundExistException(String message){
        super(message);
    }
}
