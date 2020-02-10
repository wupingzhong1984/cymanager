package com.org.gascylindermng.api;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

public class ApiException {
    private String errorMessage;
    private Integer errorCode;

    public ApiException(Throwable throwable, String massage) {
        if (throwable instanceof SocketTimeoutException) {
            setErrormessage("请求超时");
            setErrorCode(400);
            return;
        } else if (throwable instanceof ConnectException) {
            setErrormessage("服务器连接失败");
            setErrorCode(400);
            return;
        } else if (throwable instanceof HttpException) {
            int code = ((HttpException) throwable).code();
            if (code == 504) {
                setErrormessage("网络异常，请检查您的网络状态");
                setErrorCode(500);
                return;
            } else {
                setErrormessage("请求失败");
                setErrorCode(500);
                return;
            }
        } else {
            if ("".equals(massage) || null == massage)
                massage = "操作失败，请仔细检查填写内容";
            setErrormessage(massage);
            setErrorCode(555);
            return;
        }

    }

    public String getErrormessage() {
        return errorMessage;
    }

    public void setErrormessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}

