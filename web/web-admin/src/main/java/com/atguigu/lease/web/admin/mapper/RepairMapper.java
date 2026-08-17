package com.atguigu.lease.web.admin.mapper;

import com.atguigu.lease.model.entity.RepairInfo;
import com.atguigu.lease.web.admin.vo.repair.RepairItemVo;
import com.atguigu.lease.web.admin.vo.repair.RepairQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author liubo
 * @description 针对表【repair_info(报修信息表)】的数据库操作Mapper
 * @Entity com.atguigu.lease.model.entity.RepairInfo
 */
public interface RepairMapper extends BaseMapper<RepairInfo> {

    IPage<RepairItemVo> pageRepair(Page<RepairItemVo> page, RepairQueryVo queryVo);

    RepairItemVo getRepairById(Long id);
}
