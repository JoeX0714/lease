package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.NotificationInfo;
import com.atguigu.lease.model.entity.RepairInfo;
import com.atguigu.lease.model.enums.NotificationType;
import com.atguigu.lease.model.enums.RepairStatus;
import com.atguigu.lease.web.admin.mapper.RepairMapper;
import com.atguigu.lease.web.admin.service.NotificationService;
import com.atguigu.lease.web.admin.service.RepairService;
import com.atguigu.lease.web.admin.vo.repair.RepairItemVo;
import com.atguigu.lease.web.admin.vo.repair.RepairQueryVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liubo
 * @description 针对表【repair_info(报修信息表)】的数据库操作Service实现
 * @createDate 2026-08-10
 */
@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, RepairInfo>
        implements RepairService {

    @Autowired
    private RepairMapper repairMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public IPage<RepairItemVo> pageRepair(Page<RepairItemVo> page, RepairQueryVo queryVo) {
        return repairMapper.pageRepair(page, queryVo);
    }

    @Override
    public RepairItemVo getRepairById(Long id) {
        return repairMapper.getRepairById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusById(Long id, RepairStatus status) {
        if (status != RepairStatus.COMPLETED && status != RepairStatus.CANCELED) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "非法的报修目标状态");
        }
        //条件更新实现乐观并发：仅待受理(PENDING)可流转，未命中说明记录不存在或状态已被并发修改
        LambdaUpdateWrapper<RepairInfo> repairUpdateWrapper = new LambdaUpdateWrapper<>();
        repairUpdateWrapper.eq(RepairInfo::getId, id);
        repairUpdateWrapper.eq(RepairInfo::getStatus, RepairStatus.PENDING);
        repairUpdateWrapper.set(RepairInfo::getStatus, status);
        if (!update(repairUpdateWrapper)) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "当前报修状态不允许办结/撤销");
        }

        //报修状态变化后生成通知
        RepairItemVo vo = getRepairById(id);
        if (vo != null && vo.getUserId() != null) {
            NotificationInfo notificationInfo = new NotificationInfo();
            notificationInfo.setUserId(vo.getUserId());
            notificationInfo.setType(NotificationType.REPAIR_UPDATE);
            notificationInfo.setBizType("REPAIR");
            notificationInfo.setBizId(id);
            notificationInfo.setTitle("报修更新");
            String statusText = status == RepairStatus.COMPLETED ? "已办结" : "已撤销";
            notificationInfo.setContent("您提交的【" + vo.getApartmentName() + "】【" + vo.getRoomNumber() + "】房间报修" + statusText + "。");
            notificationService.createNotification(notificationInfo);
        }
        return true;
    }
}
