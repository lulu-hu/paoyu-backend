package com.lulu.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lulu.usercenter.Model.domain.Team;
import com.lulu.usercenter.Model.domain.User;
import com.lulu.usercenter.Model.dto.TeamQuery;
import com.lulu.usercenter.Model.request.TeamJoinRequest;
import com.lulu.usercenter.Model.request.TeamUpdateRequest;
import com.lulu.usercenter.Model.vo.TeamUserVO;

import java.util.List;


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

    /**
     *  搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     *  更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     *  加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);
}
