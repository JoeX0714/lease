package com.atguigu.lease.model.enums;

import java.util.EnumSet;
import java.util.Set;

public final class LeaseStatusRules {

    private static final Set<LeaseStatus> OCCUPIED_STATUSES = EnumSet.of(
            LeaseStatus.SIGNED,
            LeaseStatus.WITHDRAWING
    );

    private static final Set<LeaseStatus> NEW_WORKFLOW_BLOCKING_STATUSES = EnumSet.of(
            LeaseStatus.SIGNING,
            LeaseStatus.SIGNED,
            LeaseStatus.WITHDRAWING,
            LeaseStatus.RENEWING
    );

    private LeaseStatusRules() {
    }

    public static boolean isOccupied(LeaseStatus status) {
        return OCCUPIED_STATUSES.contains(status);
    }

    public static boolean blocksNewWorkflow(LeaseStatus status) {
        return NEW_WORKFLOW_BLOCKING_STATUSES.contains(status);
    }

    public static Set<LeaseStatus> occupiedStatuses() {
        return OCCUPIED_STATUSES;
    }

    public static Set<LeaseStatus> newWorkflowBlockingStatuses() {
        return NEW_WORKFLOW_BLOCKING_STATUSES;
    }

    public static boolean canTransition(LeaseStatus oldStatus, LeaseStatus newStatus) {
        return oldStatus == newStatus
                || (oldStatus == LeaseStatus.SIGNING && newStatus == LeaseStatus.SIGNED)
                || (oldStatus == LeaseStatus.RENEWING && newStatus == LeaseStatus.SIGNED)
                || (oldStatus == LeaseStatus.SIGNING && newStatus == LeaseStatus.CANCELED)
                || (oldStatus == LeaseStatus.SIGNED && newStatus == LeaseStatus.WITHDRAWING)
                || (oldStatus == LeaseStatus.WITHDRAWING && newStatus == LeaseStatus.WITHDRAWN);
    }
}
