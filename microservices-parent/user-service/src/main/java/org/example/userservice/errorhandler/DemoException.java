package org.example.userservice.errorhandler;

public class DemoException extends Exception{
    public DemoException(String message) {
        super(message);
    }

    public DemoException (String message, Throwable cause) {
        super(message, cause);
    }

}
