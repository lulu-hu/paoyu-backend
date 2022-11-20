package com.lulu.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lulu.usercenter.Model.domain.Team;
import com.lulu.usercenter.Model.domain.User;
import com.lulu.usercenter.Model.dto.TeamQuery;
import com.lulu.usercenter.Model.request.TeamAddRequest;
import com.lulu.usercenter.Model.request.TeamJoinRequest;
import com.lulu.usercenter.Model.request.TeamUpdateRequest;
import com.lulu.usercenter.Model.vo.TeamUserVO;
import com.lulu.usercenter.common.BaseResponse;
import com.lulu.usercenter.common.ErrorCode;
import com.lulu.usercenter.common.ResultUtils;
import com.lulu.usercenter.exception.BusinessException;
import com.lulu.usercenter.service.TeamService;
import com.lulu.usercenter.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 用户接口
 * @author lulu
 */
@RestController
@RequestMapping("/team")
@Slf4j
@CrossOrigin(origins = "http://127.0.0.1:5173/",allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

   @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
       if(teamAddRequest == null){
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
       User loginUser = userService.getLoginUser(request);
       Team team = new Team();
       BeanUtils.copyProperties(teamAddRequest,team);
       long teamId = teamService.addTeam(team,loginUser);
       return ResultUtils.success(teamId);
   }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean update = teamService.updateTeam(teamUpdateRequest,loginUser) ;
        if(!update){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询失败");
        }
        return ResultUtils.success(team);
    }

//    @GetMapping("/list")
//    public BaseResponse<List<Team>> listTeam(TeamQuery teamQuery){
//        if(teamQuery == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Team team = new Team();
//        BeanUtils.copyProperties(teamQuery,team);
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
//        List<Team> teamList = teamService.list(queryWrapper);
//        return ResultUtils.success(teamList);
//    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeam(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,isAdmin);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(team,teamQuery);
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page,queryWrapper);
        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
            if(teamJoinRequest == null){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            User loginUser = userService.getLoginUser(request);
            boolean result = teamService.joinTeam(teamJoinRequest,loginUser);
            return ResultUtils.success(result);
    }


}
