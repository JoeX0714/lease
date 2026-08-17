package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.service.LabelInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/admin/label")
public class LabelController {

    @Autowired
    LabelInfoService service;

    @Operation(summary = "（根据类型）查询标签列表")
    @GetMapping("list")
    public Result<List<LabelInfo>> labelList(@RequestParam(required = false) ItemType type) {
        LambdaQueryWrapper<LabelInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type!=null,LabelInfo::getType,type);
        List<LabelInfo> list = service.list(queryWrapper);
        return Result.ok(list);
    }
    /*GET /admin/label/list?type=1
→ Spring 取得字符串 "1"
→ WebDataBinder 调用 Converter
→ "1" 转成 ItemType.APARTMENT
→ Controller 得到 ItemType 对象
→ service.list(queryWrapper)
→ Mapper 执行查询
→ TypeHandler：ItemType.APARTMENT 转成数据库整数 1
→ SQL：where type = 1
→ 数据库返回 type=1
→ TypeHandler：整数 1 转成 ItemType.APARTMENT
→ MyBatis 组装 LabelInfo 对象
→ 多个 LabelInfo 组成 List
→ Service 返回 Controller
→ Controller 包装成 Result<List<LabelInfo>>
→ HTTPMessageConverter/Jackson 转成 JSON
→ 返回前端*/

    @Operation(summary = "新增或修改标签信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdateLabel(@RequestBody LabelInfo labelInfo) {
        service.saveOrUpdate(labelInfo);
        return Result.ok();
    }

    @Operation(summary = "根据id删除标签信息")
    @DeleteMapping("deleteById")
    public Result deleteLabelById(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }
}
