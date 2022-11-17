package com.lulu.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.lulu.usercenter.Mapper.UserTeamMapper;
import com.lulu.usercenter.Model.domain.UserTeam;
import com.lulu.usercenter.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author 29210
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2022-11-16 18:15:36
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




