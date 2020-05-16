package it.thewalkingthread.talky;

import it.thewalkingthread.talky.Notification.MyResponse;
import it.thewalkingthread.talky.Notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key = AAAAFP7-Lr8:APA91bG-tI6fILH24FmBD2gzlV1deO5AYVnCV6lbqgwOZL5Up2EWSTG8Q2z_d1DPppgbNzPSuTMug0Zv1PNHaK4aB8usNprFpwb0iQeN4WE_SWASMDICHS_Mlwhg7PyAnVOW7lH6lEkV"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
