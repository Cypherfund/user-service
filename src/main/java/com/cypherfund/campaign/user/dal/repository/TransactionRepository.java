package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserId(String userId);
}