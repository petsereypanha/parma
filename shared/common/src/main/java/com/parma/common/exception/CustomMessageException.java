package com.parma.common.exception;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@NoArgsConstructor
public class CustomMessageException extends RuntimeException {

    @Setter
    @Getter
    @JsonProperty("message")
    private String message;

    @Setter
    @Getter
    @JsonProperty("code")
    private String code;

    @JsonIgnore
    private String cause;

    @JsonIgnore
    private Object stackTrace;

    @JsonIgnore
    private List<String> suppressed;

    @JsonIgnore
    private String localizedMessage;

    @Getter
    @Setter
    @JsonProperty("data")
    private Object object;

    @Getter
    @Setter
    @JsonIgnore
    private HttpStatus httpStatus;

    public CustomMessageException(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public CustomMessageException(String message, String code, Object object) {
        this.message = message;
        this.code = code;
        this.object = object;
    }

    public CustomMessageException(String message, String code, Object object, HttpStatus httpStatus) {
        this.message = message;
        this.code = code;
        this.object = object;
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return "CustomMessageException{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
