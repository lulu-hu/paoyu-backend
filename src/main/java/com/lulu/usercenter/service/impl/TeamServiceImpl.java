package com.lulu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lulu.usercenter.Mapper.TeamMapper;
import com.lulu.usercenter.Model.domain.Team;
import com.lulu.usercenter.Model.domain.User;
import com.lulu.usercenter.Model.domain.UserTeam;
import com.lulu.usercenter.Model.dto.TeamQuery;
import com.lulu.usercenter.Model.enums.TeamStatusEnum;
import com.lulu.usercenter.Model.request.TeamJoinRequest;
import com.lulu.usercenter.Model.request.TeamUpdateRequest;
import com.lulu.usercenter.Model.vo.TeamUserVO;
import com.lulu.usercenter.Model.vo.UserVO;
import com.lulu.usercenter.common.ErrorCode;
import com.lulu.usercenter.exception.BusinessException;
import com.lulu.usercenter.service.TeamService;

import com.lulu.usercenter.service.UserService;
import com.lulu.usercenter.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 29210
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2022-11-16 18:14:09
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {

        // 1. 请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录, 未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        final long userId = loginUser.getId();
        // 3. 校验信息
        // 1. 队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        // 2. 队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // 3. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // 4. status 是否公开（int） 不传默认为0（公开）
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnums = TeamStatusEnum.getEnumByValue(status);
        if (statusEnums == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //5. 如果status 是加密状态，一定要有密码，且密码 <= 32
        String passWord = team.getPassword();
        if (statusEnums.equals(TeamStatusEnum.SECRET) && StringUtils.isBlank(passWord) || passWord.length() > 32) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不正确");
        }
        // 6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
        // 7. 校验用户最多创建 5 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验用户最多创建 5 个队伍");
        }
        // 8.  插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        //9. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数相等的
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根据创建人来查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            Integer status = teamQuery.getStatus();
            // 根据状态来查询
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }

        //不展示已过期的队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }

        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        //关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            //脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {

        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isNotBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须设置密码");
            }

        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);

    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {

        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long teamId = teamJoinRequest.getTeamId();
        if(teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍必须存在
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        //只能加入未过期的队伍
        Date expireTime = team.getExpireTime();
        if(expireTime != null && expireTime.before( new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        //禁止加入私有的队伍
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(teamStatusEnum.equals(TeamStatusEnum.SECRET)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        //如果是加密，密码必须匹配
        String password = teamJoinRequest.getPassword();
        if(TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if(StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }

        long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        //用户最多加入5个队伍
        long hasJoinNum = userTeamService.count(queryWrapper);
        if(hasJoinNum > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建和加入5个队伍");
        }

        //不能重复加入已加入的队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        queryWrapper.eq("teamId",teamId);
        //用户最多加入5个队伍
        long hasUserJoinTeam = userTeamService.count(queryWrapper);
        if(hasUserJoinTeam > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已加入该队伍");
        }

        // 已加入队伍的人数
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        long teamHasJoinNum = userTeamService.count(queryWrapper);
        if(teamHasJoinNum >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已满");
        }

        //加入队伍
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }
}





