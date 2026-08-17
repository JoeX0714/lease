package com.atguigu.lease.web.app.mapper;

import com.atguigu.lease.model.entity.RepairInfo;
import com.atguigu.lease.web.app.vo.repair.RepairItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【repair_info(报修信息表)】的数据库操作Mapper
 * @Entity com.atguigu.lease.model.entity.RepairInfo
 */
public interface RepairMapper extends BaseMapper<RepairInfo> {

    List<RepairItemVo> listByUserId(Long userId);
}
