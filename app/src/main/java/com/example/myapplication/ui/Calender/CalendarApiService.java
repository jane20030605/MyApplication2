package com.example.myapplication.ui.Calender;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

// 定義 API 服務接口
public interface CalendarApiService {
    @GET("http://100.96.1.3/api_get_calendar.php")
    Call<List<CalendarEvent>> getCalendarEvents(@Query("account") String account);

    @POST("http://100.96.1.3/api_update_calendar.php")
    Call<Void> updateCalendarEvent(@Body CalendarEvent event);

    @DELETE("http://100.96.1.3/api_delete_calendar.php")
    Call<Void> deleteCalendarEvent(@Query("event_id") String eventId);

    @PUT("http://100.96.1.3/api_add_calendar.php")
    Call<Void> addCalendarEvent(@Body CalendarEvent event);
}

