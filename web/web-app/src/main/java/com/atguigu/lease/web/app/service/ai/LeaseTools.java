package com.atguigu.lease.web.app.service.ai;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.model.enums.LeaseStatusRules;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class LeaseTools {

    private final LeaseAgreementService leaseAgreementService;
    private final AiConversationContext conversationContext;

    public LeaseTools(LeaseAgreementService leaseAgreementService, AiConversationContext conversationContext) {
        this.leaseAgreementService = leaseAgreementService;
        this.conversationContext = conversationContext;
    }

    @Tool(name = "getMyRoom", description = "Query current logged-in user's real leased room. Do not accept userId.")
    public MyRoomResult getMyRoom() {
        LeaseSelection selection = selectOccupiedLease();
        if (selection.requiresSelection()) {
            return MyRoomResult.needSelection(selection.options());
        }
        if (selection.agreement() == null) {
            return MyRoomResult.empty("No occupied lease agreement was found for the current user.");
        }
        AgreementDetailVo detail = leaseAgreementService.getDetailById(selection.agreement().getId());
        conversationContext.saveCurrentRoomId(selection.agreement().getRoomId());
        return MyRoomResult.success(toLeaseSummary(detail, selection.agreement()));
    }

    @Tool(name = "getLeaseStatus", description = "Query current logged-in user's real lease status and dates. Do not accept userId.")
    public LeaseStatusResult getLeaseStatus() {
        LeaseSelection selection = selectOccupiedLease();
        if (selection.requiresSelection()) {
            return LeaseStatusResult.needSelection(selection.options());
        }
        if (selection.agreement() == null) {
            return LeaseStatusResult.empty("No occupied lease agreement was found for the current user.");
        }
        AgreementDetailVo detail = leaseAgreementService.getDetailById(selection.agreement().getId());
        conversationContext.saveCurrentRoomId(selection.agreement().getRoomId());
        return LeaseStatusResult.success(toLeaseSummary(detail, selection.agreement()));
    }

    @Tool(name = "applyCheckOut", description = "Apply check-out for the current logged-in user's signed lease. Only call after explicit confirmation. Do not accept userId or status.")
    public CheckOutResult applyCheckOut() {
        LeaseSelection selection = selectOccupiedLease();
        if (selection.requiresSelection()) {
            return CheckOutResult.needSelection(selection.options());
        }
        if (selection.agreement() == null) {
            return CheckOutResult.failed("No occupied lease agreement was found for the current user.");
        }
        LeaseAgreement leaseAgreement = leaseAgreementService.applyCheckOut(selection.agreement().getId());
        conversationContext.saveCurrentRoomId(leaseAgreement.getRoomId());
        return CheckOutResult.success(leaseAgreement.getId(), leaseAgreement.getRoomId(), leaseAgreement.getStatus(),
                statusName(leaseAgreement.getStatus()));
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

    private LeaseSummary toLeaseSummary(AgreementDetailVo detail, AgreementItemVo item) {
        if (detail == null) {
            return new LeaseSummary(item.getId(), item.getRoomId(), item.getApartmentName(), item.getRoomNumber(),
                    item.getRent(), item.getLeaseStartDate(), item.getLeaseEndDate(), item.getLeaseStatus(),
                    statusName(item.getLeaseStatus()), null, null);
        }
        return new LeaseSummary(detail.getId(), detail.getRoomId(), detail.getApartmentName(), detail.getRoomNumber(),
                detail.getRent(), detail.getLeaseStartDate(), detail.getLeaseEndDate(), detail.getStatus(),
                statusName(detail.getStatus()), detail.getPaymentTypeName(), detail.getLeaseTermMonthCount());
    }

    private LeaseOption toLeaseOption(AgreementItemVo item) {
        return new LeaseOption(item.getId(), item.getRoomId(), item.getApartmentName(), item.getRoomNumber(),
                item.getRent(), item.getLeaseStartDate(), item.getLeaseEndDate());
    }

    private String statusName(LeaseStatus status) {
        return status == null ? null : status.getName();
    }

    private record LeaseSelection(AgreementItemVo agreement, boolean requiresSelection, List<LeaseOption> options) {
    }

    public record LeaseOption(Long agreementId, Long roomId, String apartmentName, String roomNumber, BigDecimal rent,
                              Date leaseStartDate, Date leaseEndDate) {
    }

    public record LeaseSummary(Long agreementId, Long roomId, String apartmentName, String roomNumber, BigDecimal rent,
                               Date leaseStartDate, Date leaseEndDate, LeaseStatus leaseStatus, String leaseStatusName,
                               String paymentTypeName, Integer leaseTermMonthCount) {
    }

    public record MyRoomResult(boolean success, boolean requiresSelection, String message, LeaseSummary room,
                               List<LeaseOption> options) {
        static MyRoomResult success(LeaseSummary room) {
            return new MyRoomResult(true, false, null, room, List.of());
        }

        static MyRoomResult needSelection(List<LeaseOption> options) {
            return new MyRoomResult(false, true, "Multiple occupied leases exist. Ask the user to choose a specific room.",
                    null, options);
        }

        static MyRoomResult empty(String message) {
            return new MyRoomResult(false, false, message, null, List.of());
        }
    }

    public record LeaseStatusResult(boolean success, boolean requiresSelection, String message, LeaseSummary lease,
                                    List<LeaseOption> options) {
        static LeaseStatusResult success(LeaseSummary lease) {
            return new LeaseStatusResult(true, false, null, lease, List.of());
        }

        static LeaseStatusResult needSelection(List<LeaseOption> options) {
            return new LeaseStatusResult(false, true,
                    "Multiple occupied leases exist. Ask the user to choose a specific room.", null, options);
        }

        static LeaseStatusResult empty(String message) {
            return new LeaseStatusResult(false, false, message, null, List.of());
        }
    }

    public record CheckOutResult(boolean success, boolean requiresSelection, String message, Long agreementId,
                                 Long roomId, LeaseStatus leaseStatus, String leaseStatusName,
                                 List<LeaseOption> options) {
        static CheckOutResult success(Long agreementId, Long roomId, LeaseStatus leaseStatus, String leaseStatusName) {
            String message = leaseStatus == LeaseStatus.WITHDRAWING
                    ? "Check-out application has been submitted and is waiting for backend confirmation."
                    : null;
            return new CheckOutResult(true, false, message, agreementId, roomId, leaseStatus, leaseStatusName, List.of());
        }

        static CheckOutResult needSelection(List<LeaseOption> options) {
            return new CheckOutResult(false, true,
                    "Multiple occupied leases exist. Ask the user to choose a specific room.", null, null, null, null,
                    options);
        }

        static CheckOutResult failed(String message) {
            return new CheckOutResult(false, false, message, null, null, null, null, List.of());
        }
    }
}
