package com.example.happycode.config;

import com.example.happycode.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity //不开好像也可以
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PersistentTokenRepository tokenRepository;

    @Autowired
    private MyLogoutSuccessHandler logoutSuccessHandler;


    @Autowired
    private MyLogoutHandler logoutHandler;

    /**
     * 配置
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                //自定义登陆页面
                .loginPage("/login.html")
                //自定义登录逻辑（即跳转到userServiceImlp）
                .loginProcessingUrl("/login")
//                //必须是post请求
                .successForwardUrl("/toMain")
//                .failureForwardUrl("/toWrong")
                //限制过大 且前后端分离是发送json 这时候就要自定义  同理失败逻辑
//                .successHandler(new MyForwardAuthenticationSuccessHandler("http://www.baidu.com"))
                //自定义用户名参数
                .usernameParameter("username");


        /*
        *   利用 and() 可以全部连起来
        * */

        //授权
        /*
         *   ant匹配规则
         *   ? 一个字符   * 0个或多个字符   ** 0个或多个目录   相对路径
         *   此外还有regex 正则表达式
         *
         *   开启注解控制 需要在main方法 添加@EnableGlobalMethodSecurity(securedEnabled=true)
         *      或者开启access注解 控制 在main中添加(securedEnabled=true,prePostEnabled = true)
         *
         */
        http.authorizeRequests()
                //放行登陆页面
                .antMatchers("/login.html", "/wrong.html").permitAll()
                //所有请求都需要认证（登录） 一般放最后
                //限制请求方式 HttpMethod.POST...
                .antMatchers(HttpMethod.GET, "/**/*.jpg").hasAuthority("test")
                //权限控制   严格区分大小写     其中hasAny表示有其一即可
//                .antMatchers(HttpMethod.GET,"/**/*.mp4").hasAuthority("admin")
                //角色控制 数据库设置成ROLE_xxxx  必须是这种格式   然后会自动插入ROLE_
                .antMatchers(HttpMethod.GET, "/**/*.mp4").hasRole("vip")
                //还可以控制ip地址hasIpAddress
                //access自定义权限控制方法见视频 https://www.bilibili.com/video/BV1vK4y1H7b1?p=14&spm_id_from=pageDriver
                .anyRequest().authenticated();

        //403权限不够处理
        http.exceptionHandling().accessDeniedHandler(myAccessDeniedHandler);

        //rememberMe
        http.rememberMe()
                //自定义参数remember-me
                .rememberMeParameter("remember-me")
                //过期时间 默认两周
                .tokenValiditySeconds(100000)
                //自定义登录逻辑
                .userDetailsService(userServiceImpl)
                .tokenRepository(tokenRepository);  //内存存放 InMemoryTokenRepositoryImpl()  or   数据库存放

        //退出登录
        http.logout()
                //退出登录处理 加载logoutConfigure 其中logoutHandler中 SecurityContextLogoutHandler 将认证和sessionId清除 从而实现退出登录
                .logoutUrl("/logout")
                //同理自定义 如前后端分离项目
//                .logoutSuccessHandler(logoutSuccessHandler)
                .addLogoutHandler(logoutHandler) //好像这个更合理一些   会保留logoutUrl等等
                //推出成功跳转
                .logoutSuccessUrl("/login");


        //配合 thymeleaf登陆时 携带上_csrf 的token  详情见 https://www.bilibili.com/video/BV1vK4y1H7b1?p=19&spm_id_from=pageDriver
        http.csrf().disable();
    }

    /**
     * 采用BCryptPasswordEncoder实现
     * 生成随即盐 且不用自行存储
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource); //设置数据库
//        jdbcTokenRepository.setCreateTableOnStartup(true);  //自动创建表  只需要执行一次。之后要注释掉

        return jdbcTokenRepository;
    }


}
