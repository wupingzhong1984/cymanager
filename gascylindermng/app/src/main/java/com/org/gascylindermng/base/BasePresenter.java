package com.org.gascylindermng.base;

import com.org.gascylindermng.api.ApiException;


import io.reactivex.disposables.Disposable;

public class BasePresenter {
    private Disposable disposable;
    protected String message;
    private ApiException apiException;

    protected void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }

    protected String errorMassage(Throwable throwable) {
        apiException = new ApiException(throwable,message);
        return apiException.getErrormessage();
    }

    protected void dispose() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

}
