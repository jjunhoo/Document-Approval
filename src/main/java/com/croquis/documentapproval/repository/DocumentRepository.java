package com.croquis.documentapproval.repository;

import com.croquis.documentapproval.common.Common;
import com.croquis.documentapproval.domain.Document;
import com.croquis.documentapproval.domain.DocumentApproval;
import com.croquis.documentapproval.domain.QDocument;
import com.croquis.documentapproval.domain.QDocumentApproval;
import com.croquis.documentapproval.form.DocumentForm;
import com.croquis.documentapproval.policy.ClassificationPolicy;
import com.croquis.documentapproval.policy.DocumentApprovalStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.jpa.JPAExpressions.select;

@Repository
@RequiredArgsConstructor
public class DocumentRepository {

    private final EntityManager em;
    private final UserInfoRepository userInfoRepository;

    QDocument document = QDocument.document;
    QDocumentApproval documentApproval = QDocumentApproval.documentApproval;

    /**
     * 결재 문서 작성
     * @param documentForm
     */
    public void createNewDocument(DocumentForm documentForm) {
        List<DocumentApproval> documentApprovalList = new ArrayList<>();
        String[] approvalUserIdArr = documentForm.getApprovalUserId().split(","); // 결재자 수

        // 문서 정보
        Document document = new Document();
        document.setTitle(documentForm.getTitle());
        document.setClassification(ClassificationPolicy.valueFor(documentForm.getClassification()));
        document.setContent(documentForm.getContent());
        document.setDocumentApprovalStatus(DocumentApprovalStatus.ING.getStatus()); // 최초 작성 시 '진행중' 상태 셋팅
        document.setCreateId(Common.getLoginUserId());
        document.setCreateDate(LocalDateTime.now());
        document.setUpdateId(Common.getLoginUserId());
        document.setUpdateDate(LocalDateTime.now());

        // 문서 결재자 정보
        for (int index = 0; index < approvalUserIdArr.length; index++) {
            DocumentApproval documentApproval = new DocumentApproval(document);
            documentApproval.setDocumentApprovalOrder(index + 1);
            documentApproval.setDocumentApprovalStatus(DocumentApprovalStatus.NONE.getStatus()); // 최초 작성 시 '미진행' 상태 셋팅
            documentApproval.setDocumentApproverId(approvalUserIdArr[index]);
            documentApproval.setDocumentApproverName(userInfoRepository.findById(approvalUserIdArr[index]).get().getUserName());
            documentApproval.setCreateId(Common.getLoginUserId());
            documentApproval.setCreateDate(LocalDateTime.now());
            documentApproval.setUpdateId(Common.getLoginUserId());
            documentApproval.setUpdateDate(LocalDateTime.now());
            documentApproval.setDocument(document);
            documentApprovalList.add(documentApproval);
        }

        document.setDocumentApprovals(documentApprovalList);

        em.persist(document);
    }

    /**
     * 문서 상세 조회
     * @param documentId
     * @return
     */
    public List<Document> getDocumentDetail(Long documentId) {
        JPAQueryFactory query = new JPAQueryFactory(em);

        return query.selectFrom(document)
                .where(document.documentId.eq(documentId))
                .fetch();
    }

    /**
     * 해당 문서에 나 다음 결재자가 있는지 확인 (다음 결재자가 존재하는 경우, 다음 결재자 수 리턴)
     * @param documentId
     * @param documentApproverId
     * @return
     */
    public long isNextDocumentApprover(Long documentId, String documentApproverId) {
        QDocumentApproval subDocumentApproval = QDocumentApproval.documentApproval;
        JPAQueryFactory query = new JPAQueryFactory(em);

        return query.selectFrom(documentApproval)
                .where(documentApproval.documentApprovalOrder.gt(
                        select(subDocumentApproval.documentApprovalOrder)
                                .from(subDocumentApproval)
                                .where(subDocumentApproval.document.documentId.eq(documentId), subDocumentApproval.documentApproverId.eq(documentApproverId))
                ), documentApproval.document.documentId.eq(documentId))
                .fetchCount();
    }

    /**
     * 해당 문서 결재자 결재 상태 UPDATE (COMPLETE, REJECT)
     * @param documentId
     * @param documentApproverId
     * @param documentApprovalStatus
     */
    public void setDocumentApprovalStatus(Long documentId, String documentApproverId, String documentApprovalStatus, String documentApprovalOpinion) {
        JPAQueryFactory query = new JPAQueryFactory(em);

        query.update(documentApproval)
                .set(documentApproval.documentApprovalStatus, documentApprovalStatus)
                .set(documentApproval.documentApprovalOpinion, documentApprovalOpinion)
                .set(documentApproval.updateId, documentApproverId)
                .set(documentApproval.updateDate, LocalDateTime.now())
                .where(documentApproval.document.documentId.eq(documentId), documentApproval.documentApproverId.eq(documentApproverId))
                .execute();
    }

    /**
     * 해당 문서 결재 상태 UPDATE (COMPLETE, REJECT)
     * @param documentId
     * @param documentApproverId
     * @param documentApprovalStatus
     */
    public void setDocumentStatus(Long documentId, String documentApproverId, String documentApprovalStatus) {
        JPAQueryFactory query = new JPAQueryFactory(em);

        query.update(document)
                .set(document.documentApprovalStatus, documentApprovalStatus)
                .set(document.updateId, documentApproverId)
                .set(document.updateDate, LocalDateTime.now())
                .where(document.documentId.eq(documentId))
                .execute();
    }

    /**
     * 내가 생성한 문서 중 결재 '진행중'인 문서 목록 조회 (OUTBOX)
     * @param createId
     * @return
     */
    public List<Document> getProceedingDocumentList(String createId) {
        JPAQueryFactory query = new JPAQueryFactory(em);

        return query.select(document)
                .from(document)
                .join(document.documentApprovals, documentApproval)
                .where(document.documentApprovalStatus.eq(DocumentApprovalStatus.ING.getStatus()),
                        document.createId.eq(createId)
                )
                .fetch().stream().distinct().collect(Collectors.toList());
    }

    /**
     * 내가 결재해야 할 문서 조회 (INBOX)
     * @param approverId
     * @return
     */
    public List<Document> getApprovalDocumentList(String approverId) {
        JPAQueryFactory query = new JPAQueryFactory(em);

        return query.select(document)
                .from(document)
                .join(document.documentApprovals, documentApproval)
                .where(documentApproval.documentApprovalStatus.eq(DocumentApprovalStatus.NONE.getStatus()),
                        documentApproval.documentApproverId.eq(approverId)
                )
                .fetch().stream().distinct().collect(Collectors.toList());
    }

    /**
     * 내가 관여한 문서중 결재가 완료된 문서 (ARCHIVE)
     * @param createId
     * @param approverId
     * @return
     */
    public List<Document> getCompletedByInvolvedDocument(String createId, String approverId) {
        JPAQueryFactory query = new JPAQueryFactory(em);

        return query.select(document)
                .from(document)
                .join(document.documentApprovals, documentApproval)
                .where(document.documentApprovalStatus.in(DocumentApprovalStatus.COMPLETE.getStatus(), DocumentApprovalStatus.REJECT.getStatus()),
                        eqCreateId(createId).or(eqDocumentApproverId(approverId)))
                .fetch().stream().distinct().collect(Collectors.toList());
    }

    /**
     * 문서 생성자 일치 여부
     * @param createId
     * @return
     */
    private BooleanExpression eqCreateId(String createId) {
        if (StringUtils.isEmpty(createId)) {
            return null;
        }
        return documentApproval.createId.eq(createId);
    }

    /**
     * 문서 결재자 일치 여부
     * @param documentApproverId
     * @return
     */
    private BooleanExpression eqDocumentApproverId(String documentApproverId) {
        if (StringUtils.isEmpty(documentApproverId)) {
            return null;
        }
        return documentApproval.documentApproverId.eq(documentApproverId);
    }
}
