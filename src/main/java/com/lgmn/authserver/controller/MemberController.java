package com.lgmn.authserver.controller;

import com.lgmn.common.domain.LgmnUserInfo;
import com.lgmn.authserver.service.MyUserDetailService;
import com.lgmn.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 〈会员Controller〉
 *
 * @author Curise
 * @create 2018/12/13
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private MyUserDetailService userDetailService;

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @GetMapping("/member")
    public LgmnUserInfo user(Principal member) {
        String username=member.getName();
        return userDetailService.getLgmnUserInfo(username);
    }

    @GetMapping("/principal")
    public Principal principal(Principal principal1) {
        return principal1;
    }

    @GetMapping(value = "/exit")
    public Result revokeToken(String access_token) {
        Result result = Result.success("注销成功");
        if (consumerTokenServices.revokeToken(access_token)) {
            result.setMessage("注销成功");
        } else {
            result=Result.success("注销失败");
        }
        return result;
    }
}
