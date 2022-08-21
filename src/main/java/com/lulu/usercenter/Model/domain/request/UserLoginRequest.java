package com.lulu.usercenter.Model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * @author lulu
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -3212097559203692967L;

    private String userAccount;

    private String userPassword;

}
