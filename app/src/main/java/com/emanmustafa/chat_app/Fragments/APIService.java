package com.emanmustafa.chat_app.Fragments;

import com.emanmustafa.chat_app.Notifications.MyResponse;
import com.emanmustafa.chat_app.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAVca0oyI:APA91bE5rRoTY7WfN2W38PQv72BhH-YhlOlnu5TCE8x2e2wyjZXvj4hTXNAmZ1qH6xXWzeq3Tr-mDKbjvVm4UKzI88yh5Ev5L6FhR_QeYxNQX5zOZplgLkVThai8tUIaaGuMEU2SAi-z"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
