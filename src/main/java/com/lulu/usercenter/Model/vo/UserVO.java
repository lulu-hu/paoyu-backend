package com.lulu.usercenter.Model.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *  用户包装类（脱敏）
 * @author lulu
 * @date 2022/11/18 13:50
 */
@Data
public class UserVO implements Serializable {

    /**
     *
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     *  标签列表 json
     */
    private String tags;

    /**
     * 状态 0-正常
     */
    private Integer userStatus;

    /**
     * 电话
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    private static final long serialVersionUID = 1L;
}
