package com.lulu.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lulu.usercenter.Model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.lulu.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.lulu.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {



    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 检验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     *  缓存信息
     * @param pageSize 下标
     * @param pageNum 页数
     * @param request 请求
     * @return 返回
     */
    Page<User> userRecommend(long pageSize, long pageNum, HttpServletRequest request);

    /**
     * 用户注销
     * @param request
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);


    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     *  获取当前登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
     boolean isAdmin(HttpServletRequest request);


    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);
}
