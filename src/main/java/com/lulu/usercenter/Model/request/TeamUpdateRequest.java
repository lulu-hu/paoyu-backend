package com.lulu.usercenter.Model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lulu
 * @date 2022/11/17 16:26
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -3212097559203692967L;

    /**
     *  id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

}
