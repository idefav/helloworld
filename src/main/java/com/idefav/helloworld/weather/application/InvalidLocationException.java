package com.idefav.helloworld.weather.application;

public class InvalidLocationException extends RuntimeException {
    public InvalidLocationException() { super("location is invalid or could not be resolved"); }
}
