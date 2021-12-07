package com.croquis.documentapproval.service;

import com.croquis.documentapproval.common.Common;
import com.croquis.documentapproval.domain.Document;
import com.croquis.documentapproval.domain.UserInfo;
import com.croquis.documentapproval.form.DocumentForm;
import com.croquis.documentapproval.policy.DocumentApprovalStatus;
import com.croquis.documentapproval.repository.DocumentRepository;
import com.croquis.documentapproval.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserInfoRepository userInfoRepository;

    /**
     * 유저 정보 조회
     * @return
     */
    public List<UserInfo> getUserInfoList() {
        return userInfoRepository.findAll();
    }

    /**
     * 결재 문서 작성
     * - 생성 시 최초 결재자의 결재 상태는 'ING' 로 INSERT (그래야 최초 결재자가 결재해야 하는지 상태를 알 수 있음)
     * @param documentForm
     */
    @Transactional
    public void createNewDocument(DocumentForm documentForm) {
        documentRepository.createNewDocument(documentForm); // 문서 생성
    }

    /**
     * 문서 결재
     * @param documentId
     * @param documentApproverId
     * @param documentApprovalStatus
     */
    @Transactional
    public void setApprovalDocument(Long documentId, String documentApproverId, String documentApprovalStatus, String documentApprovalOpinion) {
        // 1. 해당 문서에 나 다음 결재자가 있는지 확인
        long count = documentRepository.isNextDocumentApprover(documentId, documentApproverId);

        // 해당 문서에 나 다음 결재자가 있는 경우
        if (count > 0) {
            // 내 결재 상태 UPDATE (REJECT / COMPLETE)
            documentRepository.setDocumentApprovalStatus(documentId, documentApproverId, documentApprovalStatus, documentApprovalOpinion);
            // '반려'한 경우, 문서 상태값을 '반려' 상태로 UPDATE
            if (DocumentApprovalStatus.REJECT.getStatus().equals(documentApprovalStatus)) { // 반려
                // 해당 문서 결재 상태 UPDATE (COMPLETE, REJECT)
                documentRepository.setDocumentStatus(documentId, documentApproverId, documentApprovalStatus);
            }
        } else {  // 내가 해당 문서 마지막 결재자인 경우
            // 내 결재 상태 UPDATE (REJECT / COMPLETE)
            documentRepository.setDocumentApprovalStatus(documentId, documentApproverId, documentApprovalStatus, documentApprovalOpinion);
            // 해당 문서 결재 상태 UPDATE (COMPLETE, REJECT)
            documentRepository.setDocumentStatus(documentId, documentApproverId, documentApprovalStatus);
        }
    }

    /**
     * 문서 상세 조회
     * @param documentId
     * @return
     */
    public List<Document> getDocumentDetail(Long documentId) {
        return documentRepository.getDocumentDetail(documentId);
    }

    /**
     * 내가 생성한 문서 중 결재 '진행중' 인 문서 목록 (OUTBOX)
     * @return
     */
    public List<Document> getProceedingDocumentList() {
        return documentRepository.getProceedingDocumentList(Common.getLoginUserId());
    }

    /**
     * 내가 결재해야 할 문서 조회 (INBOX)
     * @return
     */
    public List<Document> getApprovalDocumentList() {
        return documentRepository.getApprovalDocumentList(Common.getLoginUserId());
    }

    /**
     * 내가 관여한 문서중 결재가 완료된 문서 (ARCHIVE)
     * 1. 문서 생성자 & 완료/거절
     * 2. 결재자 & 완료/거절
     * @return
     */
    public List<Document> getCompletedByInvolvedDocument() {
        return documentRepository.getCompletedByInvolvedDocument(Common.getLoginUserId(), Common.getLoginUserId());
    }
}
