package com.example.car_rental_system.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ResponseModel<T> implements Serializable {
    private int responseCode = 200;
    private String responseMessage = "success";
    private String description = "";
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonCreator
    public ResponseModel(@JsonProperty("responseCode") int responseCode,
                         @JsonProperty("responseMessage") String responseMessage,
                         @JsonProperty("description") String description,
                         @JsonProperty("data") T data) {
        this.data = data;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.description = description;
    }

    public ResponseModel(int responseCode) {
        this.responseCode = responseCode;
    }

    public ResponseModel(T data) {
        this.data = data;
    }

    public ResponseModel(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public ResponseModel(int responseCode, String responseMessage, T data) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.data = data;
    }

    public ResponseModel(int responseCode, String responseMessage, String description) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.description = description;
    }

//    public ResponseModel(int responseCode, String responseMessage, String description, T data) {
//        this.responseCode = responseCode;
//        this.responseMessage = responseMessage;
//        this.description = description;
//        this.data = data;
//    }
}
