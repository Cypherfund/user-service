package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TRoleUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TRoleUserRepository extends JpaRepository<TRoleUser, Long> {
}
