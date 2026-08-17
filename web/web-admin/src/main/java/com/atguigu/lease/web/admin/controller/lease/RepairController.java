package com.atguigu.lease.web.admin.controller.lease;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.enums.RepairStatus;
import com.atguigu.lease.web.admin.service.RepairService;
import com.atguigu.lease.web.admin.vo.repair.RepairItemVo;
import com.atguigu.lease.web.admin.vo.repair.RepairQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报修信息管理")
@RestController
@RequestMapping("/admin/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @Operation(summary = "分页查询报修信息")
    @GetMapping("page")
    public Result<IPage<RepairItemVo>> page(@RequestParam long current, @RequestParam long size, RepairQueryVo queryVo) {
        Page<RepairItemVo> page = new Page<>(current, size);
        IPage<RepairItemVo> result = repairService.pageRepair(page, queryVo);
        return Result.ok(result);
    }

    @Operation(summary = "根据id查询报修详情")
    @GetMapping("getDetailById")
    public Result<RepairItemVo> getDetailById(@RequestParam Long id) {
        RepairItemVo repairItemVo = repairService.getRepairById(id);
        return Result.ok(repairItemVo);
    }

    @Operation(summary = "根据id修改报修状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam RepairStatus status) {
        repairService.updateStatusById(id, status);
        return Result.ok();
    }
}
