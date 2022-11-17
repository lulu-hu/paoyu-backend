package com.lulu.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lulu.usercenter.Model.domain.Team;
import com.lulu.usercenter.Model.domain.User;


/**
* @author lulu
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2022-11-16 18:14:09
*/
public interface TeamService extends IService<Team> {
    /**
     *  创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

}
