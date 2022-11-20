package com.lulu.usercenter.Model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lulu
 * @date 2022/11/17 16:26
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = -3212097559203692967L;

    /**
     *  id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}
