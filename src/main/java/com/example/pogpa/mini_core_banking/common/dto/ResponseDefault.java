package com.example.pogpa.mini_core_banking.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({
        "status",
        "time",
        "data"
})
public class ResponseDefault <T>{
    private String status;
    private LocalDate time;
    private T data;

    public static <T> ResponseDefault<T> success(T data) {
        ResponseDefault<T> response = new ResponseDefault<>();
        response.setStatus("0000");
        response.setTime( LocalDate.now());
        response.setData(data);
        return response;
    }

    public static <T> ResponseDefault<T> error(T data) {
        ResponseDefault<T> response = new ResponseDefault<>();
        response.setStatus("9999");
        response.setTime( LocalDate.now());
        response.setData(data);
        return response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
