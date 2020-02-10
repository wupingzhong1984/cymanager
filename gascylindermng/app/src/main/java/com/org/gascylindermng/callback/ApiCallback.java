package com.org.gascylindermng.callback;

public interface ApiCallback {

    <T> void successful(String api, T success);

    <T> void failure(String api, T failure);
}
