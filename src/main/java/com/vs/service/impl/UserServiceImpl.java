package com.vs.service.impl;

import com.vs.dao.UserDao;
import com.vs.entity.User;
import com.vs.entity.UserClass;
import com.vs.enums.AuthorityEnum;
import com.vs.result.Result;
import com.vs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 欢
 */
@Service(value = "empService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    private static String parentCode;

    /**
     * 查询登录用户信息
     * @param user
     * @return
     */
    public Result login(User user){
        User userInfo = userDao.login(user);
        Result result = new Result();
        if (userInfo != null){
            result.setStatus(0);
            result.setMsg("登录成功");
            result.setSuccess(true);
            UserClass userClass = userDao.findClass(userInfo.getClassId());
            User teach = userDao.findTeachByClassAndQ(userInfo.getClassId());
            if (userClass != null){
                userInfo.setClassName(userClass.getClassName());
            }
            if (teach != null){
                userInfo.setTeachName(teach.getUserName());
            }
            result.setData(userInfo);
        } else {
            result.setStatus(1);
            if (userDao.findUserByCode(user.getUserCode()) > 0){
                result.setMsg("密码错误");
            } else {
                result.setMsg("该用户不存在或已被删除");
            }
            result.setSuccess(false);
            result.setData(null);
        }
        return result;
    }

    /**
     * 检测三码合一
     * @param userCode
     * @param UserName
     * @param activationCode
     * @return
     */
    public Result checkThreeCode(String userCode, String UserName, String activationCode){
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(UserName);
        user.setActivationCode(activationCode);
        Result result = new Result();
        Integer count =userDao.checkThreeCode(user);
        Integer times = userDao.checkThreeCodeTimes(userCode);
        if (count > 0 && times > 0){
            parentCode = (6 - times) + userCode.substring(1);
            result.setStatus(0);
            result.setSuccess(true);
            result.setMsg("三码匹配成功，您即将创建的账号为："+parentCode+"!");
        } else if (times == 0){
            result.setStatus(1);
            result.setSuccess(false);
            result.setMsg("该生激活码次数已经使用两次！");
        } else {
            result.setStatus(1);
            result.setSuccess(false);
            result.setMsg("三码匹配失败，请仔细检查您的输入！");
        }
        return result;
    }

    /**
     * 家长账号注册
     * @param userCode
     * @param UserPwd
     * @return
     */
    public Result register(String userCode,String userName,String UserPwd){
        User user = new User();
        Integer times = userDao.checkThreeCodeTimes(userCode);
        String code = (6 - times) + userCode.substring(1);
        user.setUserCode(code);
        user.setUserName(userName);
        user.setUserPwd(UserPwd);
        user.setAuthority(AuthorityEnum.PARENTS.getAuthority_code());
        Integer success = userDao.insertSelective(user);
        Result result = new Result();
        if (success > 0){
            user.setTimes(--times);
            user.setUserCode(userCode);
            userDao.subUserTimesOne(user);
            result.setStatus(0);
            result.setSuccess(true);
            result.setMsg("注册成功，账号为" + code + "!请牢记，然后点击确定按钮！");
        } else {
            result.setStatus(1);
            result.setSuccess(false);
            result.setMsg("注册失败！");
        }
        return result;
    }

}
