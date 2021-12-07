package com.croquis.documentapproval.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@ToString(of = { "documentApprovalId", "documentApprovalOrder", "documentApprovalStatus", "documentApproverId", "documentApprovalOpinion", "createId", "createDate", "updateId", "updateDate" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "documents_approval")
public class DocumentApproval {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentApprovalId; // 문서 결재 ID
    @Column(name = "document_approval_order")
    private int documentApprovalOrder; // 문서 결재 순서
    @Column(name = "document_approval_status")
    private String documentApprovalStatus; // 문서 결재 상태 (미진행/승인/거절) - NONE/COMPLETE/REJECT
    @Column(name = "document_approver_id")
    private String documentApproverId; // 문서 결재자 ID
    @Column(name = "document_approver_name")
    private String documentApproverName; // 문서 결재자 이름
    @Column(name = "document_approval_opinion")
    private String documentApprovalOpinion; // 문서 결재 의견
    @Column(name = "create_id")
    private String createId; // 생성자
    @Column(name = "create_date")
    private LocalDateTime createDate; // 생성 일시
    @Column(name = "update_id")
    private String updateId; // 수정자
    @Column(name = "update_date")
    private LocalDateTime updateDate; // 수정 일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    public DocumentApproval(Document document) {
        if (document != null) {
            changeDocumentApproval(document);
        }
    }

    private void changeDocumentApproval(Document document) {
        this.document = document;
        document.getDocumentApprovals().add(this);
    }
}
