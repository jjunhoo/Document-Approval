package com.croquis.documentapproval.controller;

import com.croquis.documentapproval.common.Common;
import com.croquis.documentapproval.domain.Document;
import com.croquis.documentapproval.domain.UserInfo;
import com.croquis.documentapproval.form.DocumentForm;
import com.croquis.documentapproval.policy.ClassificationPolicy;
import com.croquis.documentapproval.policy.DocumentApprovalStatus;
import com.croquis.documentapproval.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * 결재 문서 작성 폼
     * @param model
     * @return
     */
    @GetMapping("/documents/new")
    public String createNewDocumentForm(Model model) {
        List<UserInfo> userInfos = documentService.getUserInfoList();
        List<ClassificationPolicy> classificationPolicyList = Arrays.asList(ClassificationPolicy.REQUEST, ClassificationPolicy.VACATION, ClassificationPolicy.OFFICIAL);

        model.addAttribute("documentForm", new DocumentForm());
        model.addAttribute("userInfos", userInfos);
        model.addAttribute("classificationPolicyList", classificationPolicyList);

        return "documents/createDocument";
    }

    /**
     * 결재 문서 작성
     * @param documentForm
     * @param model
     * @return
     */
    @PostMapping("/documents/new")
    public String createNewDocument(DocumentForm documentForm, Model model) {
        String detailType = "proceeding";
        documentService.createNewDocument(documentForm); // 문서 생성

        model.addAttribute("detailType", detailType); // detailType : proceeding, approval, completed

        return "redirect:/documents/proceedingDocumentList/" + detailType;
    }

    /**
     * 문서 결재
     * @param documentId - 문서 번호
     * @param documentApproverId - 문서 결재자
     * @param documentApprovalStatus - 문서 결재 상태
     * @param documentApprovalOpinion - 결재 의견
     * @param detailType - 상세 화면 타입 (proceeding, approval, completed)
     * @return
     */
    @PostMapping("/documents/approvalDocuments")
    public String approvalDocuments(@Param("documentId") Long documentId
                                  , @Param("documentApproverId") String documentApproverId
                                  , @Param("documentApprovalStatus") String documentApprovalStatus
                                  , @Param("documentApprovalOpinion") String documentApprovalOpinion
                                  , @Param("detailType") String detailType) {
        documentService.setApprovalDocument(documentId, documentApproverId, documentApprovalStatus, documentApprovalOpinion);

        return "redirect:/documents/approvalDocumentList/" + detailType;
    }

    /**
     * 문서 상세 조회
     * @param documentId
     * @return
     */
    @GetMapping("/documents/{documentId}/{detailType}")
    public String getDocumentDetail(@PathVariable("documentId") Long documentId, @PathVariable("detailType") String detailType, Model model) {
        List<Document> documentDetail = documentService.getDocumentDetail(documentId);
        List<DocumentApprovalStatus> documentApprovalStatusList = Arrays.asList(DocumentApprovalStatus.COMPLETE, DocumentApprovalStatus.REJECT);

        model.addAttribute("loginUser", Common.getLoginUserId());
        model.addAttribute("documentDetail", documentDetail); // 문서 상세 정보
        model.addAttribute("documentApprovalStatusList", documentApprovalStatusList); // 문서 결재 상태
        model.addAttribute("detailType", detailType); // 문서 타입

        return "documents/documentDetail";
    }

    /**
     * 내가 생성한 문서 중 결재 '진행중' 인 문서 목록 (OUTBOX)
     * @return
     */
    @GetMapping("/documents/proceedingDocumentList/{detailType}")
    public String getProceedingDocumentList(@PathVariable("detailType") String detailType, Model model) {
        List<Document> proceedingDocumentList = documentService.getProceedingDocumentList();

        model.addAttribute("proceedingDocumentList", proceedingDocumentList);
        model.addAttribute("detailType", detailType);

        return "documents/proceedingDocumentList";
    }

    /**
     * 내가 결재해야 할 문서 조회 (INBOX)
     * @return
     */
    @GetMapping("/documents/approvalDocumentList/{detailType}")
    public String getApprovalDocumentList(@PathVariable("detailType") String detailType, Model model) {
        List<Document> approvalDocumentList = documentService.getApprovalDocumentList();

        model.addAttribute("approvalDocumentList", approvalDocumentList);
        model.addAttribute("detailType", detailType);

        return "documents/approvalDocumentList";
    }

    /**
     * 내가 관여한 문서중 결재가 완료된 문서 (ARCHIVE)
     * @return
     */
    @GetMapping("/documents/completedDocumentList/{detailType}")
    public String getCompletedByInvolvedDocument(@PathVariable("detailType") String detailType, Model model) {
        List<Document> completedDocumentList = documentService.getCompletedByInvolvedDocument();

        model.addAttribute("completedDocumentList", completedDocumentList);
        model.addAttribute("detailType", detailType);

        return "documents/completedDocumentList";
    }
}
