package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TTrace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TTraceRepository extends JpaRepository<TTrace, String> {
    Optional<TTrace> findByStrOriginatingTransaction(String reference);
}