package com.hoangloc.homilux.domain.res;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestResponse<T> {
    private int status;
    private String error;
    private Object message;
    private T data;
}