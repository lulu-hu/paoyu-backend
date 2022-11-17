package com.lulu.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用请求参数
 *
 * @author lulu
 * @date 2022/11/17 14:20
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -2952047199356759571L;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前第几页
     */
    protected int pageNum = 1;

}
