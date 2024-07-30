package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TTraceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TTraceStatusRepository extends JpaRepository<TTraceStatus, String> {
    List<TTraceStatus> findByStrExternalTransaction(String externalId);

    List<TTraceStatus> findFirstByLgTraceIdOrderByDtDateCreatedDesc(String lgTraceId);
}