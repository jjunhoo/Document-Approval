package com.croquis.documentapproval.repository;

import com.croquis.documentapproval.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    /**
     * 사용자 정보 조회
     * @param userId
     * @return
     */
    Optional<UserInfo> findByUserId(String userId);
}
