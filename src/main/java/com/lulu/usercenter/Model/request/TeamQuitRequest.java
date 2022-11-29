package com.lulu.usercenter.Model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lulu
 * @date 2022/11/17 16:26
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -3212097559203692967L;

    /**
     *  id
     */
    private Long teamId;

}
