package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.admin.service.ApartmentInfoService;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "公寓信息管理")
@RestController
@RequestMapping("/admin/apartment")
public class ApartmentController {

    @Autowired
    public ApartmentInfoService service;

    @Operation(summary = "保存或更新公寓信息")/*该接口的功能是保存或更新公寓信息，只靠ApartmentInfo是不够的，
    因为添加或修改公寓信息还涉及到公寓的配套、标签、杂费等，故需要单独定义一个Vo类*/
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ApartmentSubmitVo apartmentSubmitVo) {
        service.saveOrUpdateApartment(apartmentSubmitVo);
        return Result.ok();
    }

    @Operation(summary = "根据条件分页查询公寓列表")
    @GetMapping("pageItem")
    public Result<IPage<ApartmentItemVo>> pageItem(@RequestParam long current, @RequestParam long size, ApartmentQueryVo queryVo) {
        /*current 当前是第几页
        * size 每页有多少条
        * queryVo 包含省份id、城市id、区域id
        * 返回值类型IPage<ApartmentItemVo> IPage为分页接口，ApartmentItemVo是查询出来的每条数据的类型*/
        IPage<ApartmentItemVo> page = new Page<>(current, size);
        //service.page()这样写只会查询ApartmentInfo表，而ApartmentItemVo除了包含ApartmentInfo的字段，还包含两个额外的字段，故这里需要定义其他的方法
        IPage<ApartmentItemVo> result=service.pageItem(page,queryVo);
        return Result.ok(result);
    }

    @Operation(summary = "根据ID获取公寓详细信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo apartmentDetailVo=service.getDetailById(id);
        return Result.ok(apartmentDetailVo);
    }

    @Operation(summary = "根据id删除公寓信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        //service.removeById(id)这样写只会删除ApartmentInfo表的信息，而ApartmentSubmitVo除了包含ApartmentInfo的字段，还包含标签、杂费等字段，故这里需要定义其他的方法
        service.removeApartmentById(id);
        return Result.ok();
    }

    @Operation(summary = "根据id修改公寓发布状态")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(@RequestParam Long id, @RequestParam ReleaseStatus status) {
        if (service.getById(id) == null) {
            return Result.fail();
        }
        LambdaUpdateWrapper<ApartmentInfo> apartmentInfoUpdateWrapper = new LambdaUpdateWrapper<>();
        apartmentInfoUpdateWrapper.eq(ApartmentInfo::getId,id);
        apartmentInfoUpdateWrapper.set(ApartmentInfo::getIsRelease,status);
        service.update(apartmentInfoUpdateWrapper);
        return Result.ok();
    }

    @Operation(summary = "根据区县id查询公寓信息列表")
    @GetMapping("listInfoByDistrictId")
    public Result<List<ApartmentInfo>> listInfoByDistrictId(@RequestParam Long id) {
        LambdaQueryWrapper<ApartmentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApartmentInfo::getDistrictId,id);
        List<ApartmentInfo> list = service.list(queryWrapper);
        return Result.ok(list);
    }
}














