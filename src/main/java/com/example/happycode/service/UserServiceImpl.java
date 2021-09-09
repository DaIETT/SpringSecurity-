package com.example.happycode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * 实现自定义登录验证
 */
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {


        System.out.println("执行自定义登录逻辑");

        //1 数据库中查找用户
        if (!"admin".equals(s)) {
            throw new UsernameNotFoundException("用户密码错误");
        }

        //2 获取数据库中的明文密码
        String password = passwordEncoder.encode("123456");

        //3 返回userDetails
        //AuthorityUtils.commaSeparatedStringToAuthorityList()  分割字符串 成 用户权限list
        return new User(s, password,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin,common,ROLE_vip")); //角色一定是ROLE_aaa

    }

}
