package com.playtomic.tests.wallet.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Response {
    @JsonProperty("data")
    private Object data;
    @JsonProperty("error_code")
    private Integer errorCode;
    @JsonProperty("error_message")
    private String errorMessage;

    public static Response buildSuccess(Object data) {
        Response response = new Response();
        response.setData(data);
        return response;
    }

    public static Response buildError(ErrorResponse error) {
        Response response = new Response();
        response.errorCode = error.getCode();
        response.errorMessage = error.getMessage();
        return response;
    }
}
