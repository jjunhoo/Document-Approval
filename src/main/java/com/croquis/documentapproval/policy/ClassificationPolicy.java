package com.croquis.documentapproval.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 결재 구분
 */
@RequiredArgsConstructor
public enum ClassificationPolicy {

    REQUEST("REQUEST", "요청"),
    VACATION("VACATION", "휴가"),
    OFFICIAL("OFFICIAL", "공문");

    @Getter
    private final String status;
    @Getter
    private final String description;

    public static String valueFor(final String type) {
        if (REQUEST.status.equals(type)) {
            return REQUEST.getDescription();
        } else if (VACATION.status.equals(type)) {
            return VACATION.getDescription();
        } else {
            return OFFICIAL.getDescription();
        }
    }
}
