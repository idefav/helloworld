package com.idefav.helloworld.weather.api;

public class ApiErrorResponse {
    private final Error error;
    public ApiErrorResponse(String code, String message) { this.error = new Error(code, message); }
    public Error getError() { return error; }

    public static class Error {
        private final String code;
        private final String message;
        public Error(String code, String message) { this.code = code; this.message = message; }
        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
}
