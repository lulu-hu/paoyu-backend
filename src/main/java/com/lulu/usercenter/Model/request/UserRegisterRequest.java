package com.lulu.usercenter.Model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author lulu
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -3212097559203692967L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
