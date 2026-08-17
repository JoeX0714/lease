package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.RepairInfo;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.model.enums.RepairStatus;
import com.atguigu.lease.web.app.mapper.RepairMapper;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.service.RepairService;
import com.atguigu.lease.web.app.vo.repair.RepairItemVo;
import com.atguigu.lease.web.app.vo.repair.RepairSubmitVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private LeaseAgreementService leaseAgreementService;

    @Override
    public void submitRepair(RepairSubmitVo repairSubmitVo) {
        //校验报修内容
        if (repairSubmitVo.getRepairContent() == null || repairSubmitVo.getRepairContent().trim().isEmpty()) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR);
        }

        //根据租约id查询租约，校验归属
        LeaseAgreement leaseAgreement = leaseAgreementService.getById(repairSubmitVo.getAgreementId());
        if (leaseAgreement == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }
        String username = LoginUserHolder.getLoginUser().getUsername();
        if (leaseAgreement.getPhone() == null || !leaseAgreement.getPhone().equals(username)) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }
        if (repairSubmitVo.getRoomId() == null || !repairSubmitVo.getRoomId().equals(leaseAgreement.getRoomId())) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }
        if (leaseAgreement.getStatus() != LeaseStatus.SIGNED
                && leaseAgreement.getStatus() != LeaseStatus.WITHDRAWING) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }

        //组装并保存报修信息
        RepairInfo repairInfo = new RepairInfo();
        repairInfo.setUserId(LoginUserHolder.getLoginUser().getUserId());
        repairInfo.setAgreementId(leaseAgreement.getId());
        repairInfo.setApartmentId(leaseAgreement.getApartmentId());
        repairInfo.setRoomId(repairSubmitVo.getRoomId());
        repairInfo.setRepairContent(repairSubmitVo.getRepairContent());
        repairInfo.setStatus(RepairStatus.PENDING);
        repairMapper.insert(repairInfo);
    }

    @Override
    public List<RepairItemVo> listItemByUserId(Long userId) {
        return repairMapper.listByUserId(userId);
    }
}
