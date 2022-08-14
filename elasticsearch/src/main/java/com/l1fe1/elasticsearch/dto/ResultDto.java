package com.l1fe1.elasticsearch.dto;

import lombok.Data;

@Data
public class ResultDto<T> {

    private boolean success;

    private int code;

    private String message;

    private String tag;

    private T data;
}