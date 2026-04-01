package com.youyinda.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThirdApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    private T data;

    private Long timestamp;

    public static <T> ThirdApiResponse<T> success(T data) {
        ThirdApiResponse<T> response = new ThirdApiResponse<>();
        response.setCode(200);
        response.setMsg("success");
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static <T> ThirdApiResponse<T> fail(String msg) {
        ThirdApiResponse<T> response = new ThirdApiResponse<>();
        response.setCode(500);
        response.setMsg(msg);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
