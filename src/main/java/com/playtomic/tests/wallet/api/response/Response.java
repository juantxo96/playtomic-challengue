package com.playtomic.tests.wallet.api.response;

import lombok.Data;

@Data
public class Response {
    private Object data;
    private Integer errorCode;
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
