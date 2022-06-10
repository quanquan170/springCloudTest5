package com.aim.questionnaire.service;

import com.aim.questionnaire.common.utils.DateUtil;
import com.aim.questionnaire.common.utils.UUIDUtil;
import com.aim.questionnaire.dao.QuestionnaireEntityMapper;
import com.aim.questionnaire.dao.UserEntityMapper;
import com.aim.questionnaire.entity.UserEntity;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

//import com.aim.questionnaire.config.shiro.SysUserService;
//import com.aim.questionnaire.config.shiro.entity.UserOnlineBo;
//import com.alibaba.fastjson.JSONArray;

/**
 * Created by wln on 2018\8\9 0009.
 */
@Service
public class UserService {

    @Autowired
    private UserEntityMapper userEntityMapper;

    //@Autowired
    //private SysUserService sysUserService;

    @Autowired
    private QuestionnaireEntityMapper questionnaireEntityMapper;

    /**
     * 查询用户列表（模糊搜索）
     *
     * @param map
     * @return
     */
    public PageInfo queryUserList(Map<String, Object> map) {
//        List<Map<String, Object>> userList = userEntityMapper.queryUserList(map);
//        String currentTime=DateUtil.getCurrentDateToString(DateUtil.DEF_DATE_FORMAT_STR);
////        String[] currs=currentTime.split("-: ");
////        String currentJ=String.join("",currs);
//        for (Map<String, Object> user : userList) {
//            String[] strs = user.get("endTime").toString().split("T");
//            String endTime=strs[0] + " " + strs[1];
////            String endTime = String.join("", strs);
//            if (currentTime.compareTo(endTime) <= 0) {
//                Map<String, Object> userinfo = new HashMap<>();
//                userinfo.put("username", user.get("username"));
//                userinfo.put("status", "0");
//                modifyUserStatus(userinfo);
//            }
//        }
        PageInfo page = new PageInfo(userEntityMapper.queryUserList(map));
        return page;
    }

    /**
     * 创建用户的基本信息
     *
     * @param map
     * @return
     */
    public int addUserInfo(Map<String, Object> map) {
        if (map.get("username") != null) {
            int userResult = userEntityMapper.queryExistUser(map);
            if (userResult != 0) {
                //用户名已经存在
                return 3;
            }
        }
        String id = UUIDUtil.getOneUUID();
        map.put("id", id);
        //创建时间
        Date date = DateUtil.getCreateTime();
        map.put("creationDate", date);
        map.put("lastUpdateDate", date);
        //创建人
        String user = "admin";
        map.put("createdBy", user);
        map.put("lastUpdatedBy", user);
        //前台传入的时间戳转换
        String startTimeStr = map.get("startTime").toString();
        String endTimeStr = map.get("stopTime").toString();
        Date startTime = DateUtil.getMyTime(startTimeStr);
        Date endTime = DateUtil.getMyTime(endTimeStr);
        map.put("startTime", startTime);
        map.put("stopTime", endTime);

        int result = userEntityMapper.addUserInfo(map);
        return result;
    }

    /**
     * 编辑用户的基本信息
     *
     * @param map
     * @return
     */
    public int modifyUserInfo(Map<String, Object> map) {

        return 0;
    }

    /**
     * 修改用户状态
     *
     * @param map
     * @return
     */
    public int modifyUserStatus(Map<String, Object> map) {
        return userEntityMapper.modifyUserStatus(map);
    }

    /**
     * 根据id查询用户信息
     *
     * @param userEntity
     * @return
     */
    public Map<String, Object> selectUserInfoById(UserEntity userEntity) {

        return null;
    }

    /**
     * 根据id查询用户信息
     *
     * @param userEntity
     * @return
     */
    public UserEntity selectUserInfoByName(UserEntity userEntity) {
        List<UserEntity> hasuser = userEntityMapper.selectUserInfo(userEntity);
        if ((CollectionUtils.isEmpty(hasuser)))
            return null;
        return hasuser.get(0);
    }

    /**
     * 删除用户信息
     *
     * @param userEntity
     * @return
     */
    public int deteleUserInfoById(UserEntity userEntity) {
        return userEntityMapper.deteleUserInfoById(userEntity);
    }
}
