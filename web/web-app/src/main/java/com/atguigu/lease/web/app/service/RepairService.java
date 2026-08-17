package com.atguigu.lease.web.app.service;

import com.atguigu.lease.model.entity.RepairInfo;
import com.atguigu.lease.web.app.vo.repair.RepairItemVo;
import com.atguigu.lease.web.app.vo.repair.RepairSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【repair_info(报修信息表)】的数据库操作Service
 * @createDate 2026-08-10
 */
public interface RepairService extends IService<RepairInfo> {

    void submitRepair(RepairSubmitVo repairSubmitVo);

    List<RepairItemVo> listItemByUserId(Long userId);
}
