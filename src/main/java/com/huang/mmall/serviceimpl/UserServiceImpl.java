package com.huang.mmall.serviceimpl;

import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.common.TokenCache;
import com.huang.mmall.dao.UserMapper;
import com.huang.mmall.service.UserService;
import com.huang.mmall.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //判断用户名是否存在
        ServerResponse validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            log.warn("用户：{}登录失败，用户不存在", username);
            return ServerResponse.createByErrorMessage("登录失败，用户不存在");
        }
        //密码登录MD5
        String md5Password = Md5Util.md5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            log.warn("用户：{}登录失败，密码错误！", username);
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        log.info("用户：{}登录成功", username);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //判断用户名是否存在
        ServerResponse<String> validResponse = checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            log.warn("用户：{}注册失败，用户已存在", user.getUsername());
            return validResponse;
        }
        //判断email是否已存在
        validResponse = checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            log.warn("用户：{}注册失败，email已存在", user.getUsername());
            return validResponse;
        }
        //设置用户角色为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(Md5Util.md5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            log.warn("用户：{}注册失败，插入数据库异常", user.getUsername());
            return ServerResponse.createByErrorMessage("注册失败");
        } else {
            log.info("用户：{}注册成功", user.getUsername());
            return ServerResponse.createBySuccessMessage("注册成功");
        }
    }

    /**
     * 判断用户名或者邮箱是否存在
     *
     * @param str  用户名|邮箱
     * @param type username | email
     * @return ServerResponse
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //校验用户名
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    log.warn("校验用户名：{}是否可用，用户名已经存在", str);
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
            //校验email
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    log.warn("校验邮箱：{}是否可用，邮箱已经存在", str);
                    return ServerResponse.createByErrorMessage("邮箱已经注册过");
                }
            }
        } else {
            log.warn("校验邮箱|用户名是否可用，参数错误，type为空");
            return ServerResponse.createByErrorMessage("参数错误");
        }
        log.info("校验{}:{}是否可用，校验成功", type, str);
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            log.warn("查找用户：{}的密保问题失败，用户不存在", username);
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            log.info("查找用户：{}的密保问题成功", username);
            return ServerResponse.createBySuccess(question);
        } else {
            log.warn("查找用户：{}的密保问题失败，密保问题是空的", username);
            return ServerResponse.createByErrorMessage("找回密码的问题是空的");
        }
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount == 0) {
            log.warn("校验问题：{}答案不通过，答案错误", question);
            return ServerResponse.createByErrorMessage("问题答案错误");
        } else {
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            log.info("校验问题：{}答案通过，返回token：{}", question, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        //校验token是否存在
        if (StringUtils.isBlank(forgetToken)) {
            log.warn("修改用户：{}密码失败，forgetToken为空或无效", username);
            return ServerResponse.createByErrorMessage("修改密码失败，请重试");
        }
        //校验用户是否存在
        ServerResponse<String> validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            log.warn("修改用户：{}密码失败，用户不存在", username);
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        //校验token是否有效
        if (StringUtils.isBlank(token)) {
            log.warn("修改用户：{}密码失败，token已过时或无效", username);
            return ServerResponse.createByErrorMessage("修改密码失败，请重试");
        }
        if (StringUtils.equals(token, forgetToken)) {
            String md5PasswordNew = Md5Util.md5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, md5PasswordNew);
            if (resultCount > 0) {
                log.info("修改用户：{}密码成功", username);
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            log.warn("修改用户：{}密码失败，token不相等", username);
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        log.warn("修改用户：{}密码失败", username);
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权，要验证一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count(1)，如果不指定id，那么结果就是true(即count>0)
        int resultCount = userMapper.checkPassword(Md5Util.md5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            log.warn("登录状态修改用户：{}密码失败，旧密码错误", user.getUsername());
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(Md5Util.md5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount == 0) {
            log.warn("登录状态修改用户：{}密码失败，update语句执行错误", user.getUsername());
            return ServerResponse.createByErrorMessage("密码更新失败");
        } else {
            log.info("登录状态修改用户：{}密码成功", user.getUsername());
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
    }

    @Override
    public ServerResponse<User> updateInfo(User user) {
        log.info("修改用户信息->用户：{}", user);
        //username不能被更新
        //校验新Email是否存在，并且存在的email不能是当前用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            log.warn("email已经存在，更新用户信息失败");
            return ServerResponse.createByErrorMessage("email已存在，请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (resultCount > 0) {
            log.info("修改用户信息成功");
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        log.warn("修改用户信息失败");
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }
}
