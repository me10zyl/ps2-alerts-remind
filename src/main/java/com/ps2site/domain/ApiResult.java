package com.ps2site.domain;

import lombok.Data;

@Data
public class ApiResult {
    private String message;
    private Object data;
    private boolean success;

    private ApiResult(){

    }

    public static ApiResult success(String message, Object data){
        ApiResult objectApiResult = new ApiResult();
        objectApiResult.success = true;
        objectApiResult.message = message;
        objectApiResult.data = data;
        return objectApiResult;
    }


    public static ApiResult fail(String message, Object data){
        ApiResult objectApiResult = new ApiResult();
        objectApiResult.success = false;
        objectApiResult.message = message;
        objectApiResult.data = data;
        return objectApiResult;
    }
}
