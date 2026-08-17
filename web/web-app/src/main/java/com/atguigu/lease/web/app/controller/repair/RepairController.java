package com.atguigu.lease.web.app.controller.repair;

import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.web.app.service.RepairService;
import com.atguigu.lease.web.app.vo.repair.RepairItemVo;
import com.atguigu.lease.web.app.vo.repair.RepairSubmitVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/repair")
@Tag(name = "报修信息")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @Operation(summary = "提交报修")
    @PostMapping("save")
    public Result save(@RequestBody RepairSubmitVo repairSubmitVo) {
        repairService.submitRepair(repairSubmitVo);
        return Result.ok();
    }

    @Operation(summary = "查询个人报修列表")
    @GetMapping("listItem")
    public Result<List<RepairItemVo>> listItem() {
        Long userId = LoginUserHolder
                .getLoginUser()
                .getUserId();
        List<RepairItemVo> list = repairService.listItemByUserId(userId);
        return Result.ok(list);
    }
}
