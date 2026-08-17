package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.model.enums.AppointmentStatus;
import com.atguigu.lease.web.admin.mapper.ViewAppointmentMapper;
import com.atguigu.lease.web.admin.service.ViewAppointmentService;
import com.atguigu.lease.web.admin.vo.appointment.AppointmentQueryVo;
import com.atguigu.lease.web.admin.vo.appointment.AppointmentVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liubo
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {


    @Autowired
    private ViewAppointmentMapper viewAppointmentMapper;

    @Override
    public IPage<AppointmentVo> pageAppointment(IPage<AppointmentVo> page, AppointmentQueryVo queryVo) {
        return viewAppointmentMapper.pageAppointment(page,queryVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusById(Long id, AppointmentStatus status) {
        ViewAppointment old = getById(id);
        if (old == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR.getCode(), "预约不存在");
        }
        //仅待看房(WAITING)可流转为已看房(VIEWED)/已取消(CANCELED)
        if (old.getAppointmentStatus() != AppointmentStatus.WAITING) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "当前预约状态不允许修改");
        }
        if (status != AppointmentStatus.VIEWED && status != AppointmentStatus.CANCELED) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "非法的预约目标状态");
        }
        LambdaUpdateWrapper<ViewAppointment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ViewAppointment::getId, id);
        updateWrapper.set(ViewAppointment::getAppointmentStatus, status);
        update(updateWrapper);
    }
}




