package com.croquis.documentapproval.service;

import com.croquis.documentapproval.domain.UserInfo;
import com.croquis.documentapproval.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserInfoRepository userInfoRepository;

    /**
     * UserDetailsService > loadUserByUsername 구현
     * @param userId
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoRepository.findByUserId(userId).orElseThrow((() -> new UsernameNotFoundException((userId))));
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        return new User(userInfo.getUserId(), userInfo.getPassword(), authorities);
    }
}
