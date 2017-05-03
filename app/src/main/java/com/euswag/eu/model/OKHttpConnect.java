package com.euswag.eu.model;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ASUS on 2017/3/7.
 */

public class OKHttpConnect {
    private OkHttpClient client = new OkHttpClient();

    public String getdata(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public String postdata(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public String postfile(String url, File file) throws IOException{
        RequestBody filebody = RequestBody.create(MediaType.parse("image/jpeg"),file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "file", filebody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()){
            return response.body().string();
        }
        else {
            throw new IOException("Unexpected code " + response);
        }
    }
}
