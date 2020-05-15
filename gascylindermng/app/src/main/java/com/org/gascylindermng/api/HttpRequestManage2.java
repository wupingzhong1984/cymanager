package com.org.gascylindermng.api;

import android.util.Log;

import com.org.gascylindermng.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/***
 * 网络请求管理
 */
public class HttpRequestManage2 {
    private final String TAG = "Logout";
    private static final int DEFAULT_TIMEOUT = 15;
    private Retrofit retrofit;
    private OkHttpClient.Builder httpClientBuilder;

    private HttpRequestManage2() {
        if (httpClientBuilder == null) {
            httpClientBuilder = initOkHttpClient();
        }
        if (retrofit == null) {
            retrofit = initRetrofit();
        }

    }

    private OkHttpClient.Builder initOkHttpClient() {
        File httpCacheDirectory = new File(MyAppContext.getInstance().getCacheDir(), "siya_cache");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e(TAG, ":****************:" + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder
                .addInterceptor(headerInterceptor)
                .addInterceptor(logging)
                .addNetworkInterceptor(interceptorss)
                .cache(cache)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        return httpClientBuilder;
    }


    private Interceptor headerInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder()
      //              .addHeader("Accept-Encoding", "gzip")
      //              .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
//                    .addHeader("MobileInfo", initMobileInfo())
                    .method(originalRequest.method(), originalRequest.body());
            // requestBuilder.addHeader("Authorization", "Bearer " + BaseConstant.TOKEN);//添加请求头信息，服务器进行token有效性验证
            Log.i("test",originalRequest.body().toString());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    };

//    private String initMobileInfo() {
////        StringBuilder stringBuilder = new StringBuilder();
////        stringBuilder.append(Build.MODEL).append("-").append(Build.BRAND).append("-").append(StringTools.UUID());
////        return stringBuilder.toString();
//    }

    private Retrofit initRetrofit() {
        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .baseUrl(Constants.Base_Url2)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    private static class HttpRequestManageHolder {
        private static HttpRequestManage2 networkManageTools = new HttpRequestManage2();
    }

    public static HttpRequestManage2 getInstanc() {
        return HttpRequestManageHolder.networkManageTools;
    }

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

    Interceptor interceptorss = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!Network.isNetworkAvailable()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                Log.e(TAG, "no network");
            }
            Response response = chain.proceed(request);
            if (Network.isNetworkAvailable()) {
                int maxAge = 0 * 60; // 有网络时 设置缓存超时时间为0;

                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            } else {
                int maxStale = 60 * 60 * 24; // 无网络时，设置超时为1天
                Log.e(TAG, "has maxStale=" + maxStale);
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
            return response;

        }
    };

}