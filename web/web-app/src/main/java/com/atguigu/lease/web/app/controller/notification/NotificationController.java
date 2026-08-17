package com.atguigu.lease.web.app.controller.notification;

import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.web.app.service.NotificationService;
import com.atguigu.lease.web.app.vo.notification.NotificationItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/notification")
@Tag(name = "通知信息")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "查询个人通知列表")
    @GetMapping("listItem")
    public Result<List<NotificationItemVo>> listItem() {
        Long userId = LoginUserHolder
                .getLoginUser()
                .getUserId();
        List<NotificationItemVo> list = notificationService.listItemByUserId(userId);
        return Result.ok(list);
    }

    @Operation(summary = "根据id标记通知已读")
    @PostMapping("readById")
    public Result readById(@RequestParam Long id) {
        Long userId = LoginUserHolder
                .getLoginUser()
                .getUserId();
        notificationService.readById(userId, id);
        return Result.ok();
    }
}
