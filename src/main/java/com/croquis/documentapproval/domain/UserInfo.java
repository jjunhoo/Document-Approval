package com.croquis.documentapproval.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_info")
public class UserInfo {
    @Id
    @Column(name = "user_id", unique = true)
    private String userId; // 사용자 ID
    @Column(name = "password")
    private String password; // 패스워드
    @Column(name = "user_name")
    private String userName; // 사용자 이름
    @Column(name = "use_yn")
    private String useYn; // 사용여부
    @Column(name = "create_id")
    private String createId; // 생성자
    @Column(name = "create_date")
    private LocalDateTime createDate; // 생성 일시
    @Column(name = "update_id")
    private String updateId; // 수정자
    @Column(name = "update_date")
    private LocalDateTime updateDate; // 수정 일시
}
