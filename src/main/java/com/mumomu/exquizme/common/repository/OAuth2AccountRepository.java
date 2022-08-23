package com.mumomu.exquizme.common.repository;

import com.mumomu.exquizme.common.entity.OAuth2Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2AccountRepository extends JpaRepository<OAuth2Account, Long> {
    //@EntityGraph(attributePaths = "authorities") // authorities까지 같이 가져오게할 수 있음
    Optional<OAuth2Account> findByUsername(String username);
}
