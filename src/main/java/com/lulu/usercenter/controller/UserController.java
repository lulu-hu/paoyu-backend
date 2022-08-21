package com.lulu.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lulu.usercenter.Model.domain.User;
import com.lulu.usercenter.Model.domain.request.UserLoginRequest;
import com.lulu.usercenter.Model.domain.request.UserRegisterRequest;
import com.lulu.usercenter.common.BaseResponse;
import com.lulu.usercenter.common.ErrorCode;
import com.lulu.usercenter.common.ResultUtils;
import com.lulu.usercenter.exception.BusinessException;
import com.lulu.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lulu.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.lulu.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * @author lulu
 */
@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = "http://127.0.0.1:5173/",allowCredentials = "true")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
//        return new BaseResponse<>(0,result,"ok");
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return  ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout( HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = currentUser.getId();
        //TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    //查询用户
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request){
        if (!userService.isAdmin(request)) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request){
//        User loginUser = userService.getLoginUser(request);
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        //如果有缓存，直接读缓存
//        String redisKey = String.format("paoyu:user:recommend:%s", loginUser.getId());
//        Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
//        if(userPage != null){
//            return ResultUtils.success(userPage);
//        }
//        //无缓存，查数据库
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        userPage = userService.page(new Page<>(pageNum ,pageSize),queryWrapper);
//        //写缓存
//        try {
//            valueOperations.set(redisKey,userPage);
//        } catch (Exception e) {
//            log.error("redis set key error",e);
//        }
//        return ResultUtils.success(userPage);
        Page<User> userPage = userService.userRecommend(pageSize, pageNum, request);
        return ResultUtils.success(userPage);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }


    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request){
        //1. 校验参数是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 校验权重
        //3. 触发更新
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);

    }

}
