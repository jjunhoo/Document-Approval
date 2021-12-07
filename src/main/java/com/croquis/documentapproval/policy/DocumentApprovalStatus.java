package com.croquis.documentapproval.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 결재 진행 상태
 */
@RequiredArgsConstructor
public enum DocumentApprovalStatus {

    ING("ING", "진행중"),
    COMPLETE("COMPLETE", "완료"),
    REJECT("REJECT", "반려"),
    NONE("NONE", "미진행");

    @Getter
    private final String status;
    @Getter
    private final String description;

}
