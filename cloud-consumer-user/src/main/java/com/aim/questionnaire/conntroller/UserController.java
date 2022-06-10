package com.aim.questionnaire.conntroller;

import com.aim.questionnaire.beans.HttpResponseEntity;
import com.aim.questionnaire.common.Constans;
import com.aim.questionnaire.dao.UserEntityMapper;
import com.aim.questionnaire.entity.UserEntity;
import com.aim.questionnaire.service.UserService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by wln on 2018\8\9 0009.
 */
@RestController
@RequestMapping("/admin")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserEntityMapper userEntityMapper;

    /**
     * 用户登录
     *
     * @param userEntity
     * @return
     */
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity userLogin(@RequestBody UserEntity userEntity) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        try {
            UserEntity hasUser = userService.selectUserInfoByName(userEntity);
            if (hasUser == null) {//用户名错误
                httpResponseEntity = logout();
                httpResponseEntity.setMessage(Constans.LOGIN_USERNAME_MESSAGE);
                return httpResponseEntity;
            } else if (hasUser.getPassword().equals(userEntity.getPassword())) {

                if (hasUser.getStopTime().compareTo(new Date()) >= 0) {//状态为开启且没有到达截止实践
                    httpResponseEntity.setCode(Constans.SUCCESS_CODE);
                    httpResponseEntity.setData(hasUser);
                    httpResponseEntity.setMessage(Constans.LOGIN_MESSAGE);
                } else {//状态为开启，但是到达截止时间
                    Map<String, Object> userinfo = new HashMap<>();
                    userinfo.put("username", hasUser.getUsername());
                    userinfo.put("status", "0");
                    userService.modifyUserStatus(userinfo);
                    httpResponseEntity = logout();
                    httpResponseEntity.setMessage(Constans.LOGOUT_PERMISSION_MESSAGE);
                    return httpResponseEntity;
                }
            } else {//密码错误
                httpResponseEntity.setCode(Constans.EXIST_CODE);
                httpResponseEntity.setData(null);
                httpResponseEntity.setMessage(Constans.LOGIN_PASSWORD_MESSAGE);
            }
        } catch (Exception e) {
            logger.info("userLogin 用户登录>>>>>>>>>>>" + e.getLocalizedMessage());
            httpResponseEntity.setCode(Constans.EXIST_CODE);
            httpResponseEntity.setMessage(Constans.EXIST_MESSAGE);
        }
        return httpResponseEntity;
    }

    /**
     * 查询用户列表（模糊搜索）
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/queryUserList", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity queryUserList(@RequestBody Map<String, Object> map) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        PageInfo userList = userService.queryUserList(map);
        httpResponseEntity.setData(userList);
        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        httpResponseEntity.setMessage(Constans.STATUS_MESSAGE);
        return httpResponseEntity;
    }

    /**
     * 创建用户的基本信息
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/addUserInfo", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity addUserInfo(@RequestBody Map<String, Object> map) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        try {
            int result = userService.addUserInfo(map);
            if (result == 3) {
                httpResponseEntity.setCode(Constans.USER_USERNAME_CODE);
                httpResponseEntity.setMessage(Constans.USER_USERNAME_MESSAGE);
            } else {
                httpResponseEntity.setCode(Constans.SUCCESS_CODE);
                httpResponseEntity.setMessage(Constans.ADD_MESSAGE);
            }
        } catch (Exception e) {
            logger.info("addUserInfo 创建用户的基本信息>>>>>>>>>>>" + e.getLocalizedMessage());
            httpResponseEntity.setCode(Constans.EXIST_CODE);
            httpResponseEntity.setMessage(Constans.EXIST_MESSAGE);
        }
        return httpResponseEntity;
    }

    /**
     * 编辑用户的基本信息
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/modifyUserInfo", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity modifyUserInfo(@RequestBody Map<String, Object> map) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        userService.modifyUserInfo(map);
        httpResponseEntity.setMessage(Constans.UPDATE_MESSAGE);
        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        return httpResponseEntity;
    }


    /**
     * 根据用户id查询用户基本信息
     *
     * @param userEntity
     * @return
     */
    @RequestMapping(value = "/selectUserInfoById", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity selectUserInfoById(@RequestBody UserEntity userEntity) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        Map<String, Object> user = userEntityMapper.selectUserInfoById(userEntity);

        httpResponseEntity.setData(user);
        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        httpResponseEntity.setMessage(Constans.STATUS_MESSAGE);
        return httpResponseEntity;
    }


    /**
     * 修改用户状态
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/modifyUserStatus", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity modifyUserStatus(@RequestBody Map<String, Object> map) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        return httpResponseEntity;
    }

    /**
     * 删除用户信息
     *
     * @param userEntity
     * @return
     */
    @RequestMapping(value = "/deleteUserInfoById", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity deteleUserInfoById(@RequestBody UserEntity userEntity) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        int result = userService.deteleUserInfoById(userEntity);
        if (result == 1) {
            httpResponseEntity.setMessage(Constans.DELETE_MESSAGE);
            httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        } else {
            httpResponseEntity.setCode(Constans.EXIST_CODE);
        }
        return httpResponseEntity;
    }


    /**
     * 用户没有权限
     *
     * @return
     */
    @RequestMapping(value = "/error")
    public HttpResponseEntity logout() {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        httpResponseEntity.setCode(Constans.EXIST_CODE);
        httpResponseEntity.setData(null);
        return httpResponseEntity;
    }
}
