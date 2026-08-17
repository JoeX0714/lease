package com.atguigu.lease.web.admin.service;

import com.atguigu.lease.model.entity.RepairInfo;
import com.atguigu.lease.model.enums.RepairStatus;
import com.atguigu.lease.web.admin.vo.repair.RepairItemVo;
import com.atguigu.lease.web.admin.vo.repair.RepairQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author liubo
 * @description 针对表【repair_info(报修信息表)】的数据库操作Service
 * @createDate 2026-08-10
 */
public interface RepairService extends IService<RepairInfo> {

    IPage<RepairItemVo> pageRepair(Page<RepairItemVo> page, RepairQueryVo queryVo);

    RepairItemVo getRepairById(Long id);

    boolean updateStatusById(Long id, RepairStatus status);
}
