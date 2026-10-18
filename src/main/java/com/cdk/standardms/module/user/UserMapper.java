package com.cdk.standardms.module.user;

import com.cdk.standardms.common.domain.dao.UserDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    // ########## insert
    void create(UserDao userDao);


    // ########## select
    List<UserDao> selectList(Map<String, Object> params);
    List<UserDao> selectByMobile(@Param("mobile") String mobile);
    List<UserDao> selectByEmail(@Param("email")String email);
    List<UserDao> selectByAccount(@Param("account")String account);
    UserDao selectById(@Param("id") Long id);




    // ########## update
    void update(UserDao userDao);



    // ########## delete




}