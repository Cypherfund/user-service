package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TAccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TAccountBalanceRepository extends JpaRepository<TAccountBalance, Long> {
    Optional<TAccountBalance> findByLgUserId(String userId);
}