package com.croquis.documentapproval.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "documents")
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId; // 문서 ID
    @Column(name = "title")
    private String title; // 문서 제목
    @Column(name = "content")
    private String content; // 문서 내용
    @Column(name = "classification")
    private String classification; // 문서 분류
    @Column(name = "document_approval_status")
    private String documentApprovalStatus; // 문서 결재 상태
    @Column(name = "create_id")
    private String createId; // 생성자
    @Column(name = "create_date")
    private LocalDateTime createDate; // 생성 일시
    @Column(name = "update_id")
    private String updateId; // 수정자
    @Column(name = "update_date")
    private LocalDateTime updateDate; // 수정 일시

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<DocumentApproval> documentApprovals = new ArrayList<>();
}
