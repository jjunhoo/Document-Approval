package com.croquis.documentapproval.form;

import lombok.Data;

/**
 * 결재 문서 작성 폼 클래스
 */
@Data
public class DocumentForm {
    private String title; // 문서 제목
    private String classification; // 문서 분류
    private String content; // 내용
    private String approvalUserId; // 결재자 ID
    private String userId; // 현재 사용자 ID
}
