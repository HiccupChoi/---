package com.vs.dao;

import com.vs.entity.User;
import com.vs.entity.UserClass;

public interface UserDao {
    /**
     * 登录用户信息检测
     * @param user
     * @return
     */
    public User login(User user);

    /**
     * 查询班级
     * @param classId
     * @return
     */
    public UserClass findClass(Integer classId);

    /**
     * 查询学生对应班级老师
     * @param classId
     * @return
     */
    User findTeachByClassAndQ(Integer classId);
}