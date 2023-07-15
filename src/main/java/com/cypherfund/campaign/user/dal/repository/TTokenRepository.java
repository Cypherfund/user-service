package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TTokenRepository extends JpaRepository<TToken, String> {
}
