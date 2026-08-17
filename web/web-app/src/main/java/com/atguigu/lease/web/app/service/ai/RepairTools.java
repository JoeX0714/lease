package com.atguigu.lease.web.app.service.ai;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.enums.LeaseStatusRules;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.service.RepairService;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.atguigu.lease.web.app.vo.repair.RepairItemVo;
import com.atguigu.lease.web.app.vo.repair.RepairSubmitVo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class RepairTools {

    private final RepairService repairService;
    private final LeaseAgreementService leaseAgreementService;
    private final AiConversationContext conversationContext;

    public RepairTools(RepairService repairService, LeaseAgreementService leaseAgreementService,
                       AiConversationContext conversationContext) {
        this.repairService = repairService;
        this.leaseAgreementService = leaseAgreementService;
        this.conversationContext = conversationContext;
    }

    @Tool(name = "createRepairOrder", description = "Create a real repair order for current logged-in user's leased room. Do not accept userId or trust model-provided roomId.")
    public CreateRepairResult createRepairOrder(
            @ToolParam(description = "Repair content described by user, for example air conditioner is broken.", required = true)
            String repairContent) {
        if (repairContent == null || repairContent.trim().isEmpty()) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "repairContent cannot be blank");
        }
        LeaseSelection selection = selectOccupiedLease();
        if (selection.requiresSelection()) {
            return CreateRepairResult.needSelection(selection.options());
        }
        if (selection.agreement() == null) {
            return CreateRepairResult.failed("No occupied lease agreement was found for the current user.");
        }
        RepairSubmitVo submitVo = new RepairSubmitVo();
        submitVo.setAgreementId(selection.agreement().getId());
        submitVo.setRoomId(selection.agreement().getRoomId());
        submitVo.setRepairContent(repairContent.trim());
        repairService.submitRepair(submitVo);
        conversationContext.saveCurrentRoomId(selection.agreement().getRoomId());
        return CreateRepairResult.success(selection.agreement().getId(), selection.agreement().getRoomId(),
                repairContent.trim());
    }

    @Tool(name = "getMyRepairOrders", description = "Query current logged-in user's real repair orders. Do not accept userId.")
    public List<RepairItemVo> getMyRepairOrders() {
        return repairService.listItemByUserId(currentUser().getUserId());
    }

    private LeaseSelection selectOccupiedLease() {
        String phone = currentUser().getUsername();
        List<AgreementItemVo> occupiedAgreements = leaseAgreementService.listItemByPhone(phone).stream()
                .filter(item -> LeaseStatusRules.isOccupied(item.getLeaseStatus()))
                .toList();
        if (occupiedAgreements.isEmpty()) {
            return new LeaseSelection(null, false, List.of());
        }
        Long currentRoomId = conversationContext.getCurrentRoomId();
        if (currentRoomId != null) {
            List<AgreementItemVo> matched = occupiedAgreements.stream()
                    .filter(item -> Objects.equals(item.getRoomId(), currentRoomId))
                    .toList();
            if (matched.size() == 1) {
                return new LeaseSelection(matched.get(0), false, List.of());
            }
        }
        if (occupiedAgreements.size() == 1) {
            return new LeaseSelection(occupiedAgreements.get(0), false, List.of());
        }
        return new LeaseSelection(null, true, occupiedAgreements.stream().map(this::toLeaseOption).toList());
    }

    private LoginUser currentUser() {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        return loginUser;
    }

    private LeaseOption toLeaseOption(AgreementItemVo item) {
        return new LeaseOption(item.getId(), item.getRoomId(), item.getApartmentName(), item.getRoomNumber(),
                item.getRent(), item.getLeaseStartDate(), item.getLeaseEndDate());
    }

    private record LeaseSelection(AgreementItemVo agreement, boolean requiresSelection, List<LeaseOption> options) {
    }

    public record LeaseOption(Long agreementId, Long roomId, String apartmentName, String roomNumber, BigDecimal rent,
                              Date leaseStartDate, Date leaseEndDate) {
    }

    public record CreateRepairResult(boolean success, boolean requiresSelection, String message, Long agreementId,
                                     Long roomId, String repairContent, List<LeaseOption> options) {
        static CreateRepairResult success(Long agreementId, Long roomId, String repairContent) {
            return new CreateRepairResult(true, false, null, agreementId, roomId, repairContent, List.of());
        }

        static CreateRepairResult needSelection(List<LeaseOption> options) {
            return new CreateRepairResult(false, true,
                    "Multiple occupied leases exist. Ask the user to choose a specific room.", null, null, null, options);
        }

        static CreateRepairResult failed(String message) {
            return new CreateRepairResult(false, false, message, null, null, null, List.of());
        }
    }
}
