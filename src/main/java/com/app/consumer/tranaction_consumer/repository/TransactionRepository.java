package com.app.consumer.tranaction_consumer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.consumer.tranaction_consumer.entity.ConsumerTransaction;

@Repository
public interface TransactionRepository extends JpaRepository<ConsumerTransaction,Integer>{

    @Query(value = "SELECT amount FROM merchant_details WHERE merchantId = :merchantId", nativeQuery = true)
    double getAmountByMerchentId(@Param("merchantId") int merchantId);

    @Modifying
    @Query(value = "UPDATE merchant_details SET amount = :amount WHERE merchantId = :merchantId", nativeQuery = true)
    void updateAmountByMerchantId(@Param("merchantId") int merchantId, @Param("amount") double amount);

}
