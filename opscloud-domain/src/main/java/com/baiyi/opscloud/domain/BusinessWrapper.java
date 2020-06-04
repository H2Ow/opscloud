package com.baiyi.opscloud.domain;

import lombok.Data;


@Data
public class BusinessWrapper<T> {

    public static final BusinessWrapper<Boolean> SUCCESS = new BusinessWrapper<>(true);

    private T body;

    private boolean success;

    private int code;

    private String desc;

    public BusinessWrapper(T body) {
        this.body = body;
        this.success = true;
    }

    public BusinessWrapper() {
        this.success = true;
    }

    public BusinessWrapper(int code, String desc) {
        this.code = code;
        this.desc = desc;
        this.success = false;
    }

    public BusinessWrapper(ErrorEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.desc = errorEnum.getMessage();
        this.success = false;
    }
}
