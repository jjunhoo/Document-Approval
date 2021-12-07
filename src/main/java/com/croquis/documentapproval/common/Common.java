package com.croquis.documentapproval.common;

import org.springframework.security.core.context.SecurityContextHolder;

public class Common {
    /**
     * 로그인 사용자의 아이디 리턴
     * @return
     */
    public static String getLoginUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
