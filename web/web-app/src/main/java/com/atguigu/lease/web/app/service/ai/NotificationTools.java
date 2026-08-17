package com.atguigu.lease.web.app.service.ai;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.web.app.service.NotificationService;
import com.atguigu.lease.web.app.vo.notification.NotificationItemVo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationTools {

    private final NotificationService notificationService;

    public NotificationTools(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Tool(name = "getMessages", description = "Query current logged-in user's real business notifications. Do not mark them read and do not accept userId.")
    public List<NotificationItemVo> getMessages(
            @ToolParam(description = "Maximum notification count. Default 10, max 20.", required = false)
            Integer limit) {
        int size = limit == null ? 10 : Math.max(1, Math.min(limit, 20));
        return notificationService.listItemByUserId(currentUser().getUserId()).stream()
                .limit(size)
                .toList();
    }

    private LoginUser currentUser() {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        return loginUser;
    }
}
