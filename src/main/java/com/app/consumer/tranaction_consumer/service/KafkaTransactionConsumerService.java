package com.app.consumer.tranaction_consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.app.consumer.tranaction_consumer.entity.ConsumerTransaction;
import com.app.consumer.tranaction_consumer.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaTransactionConsumerService {
    
    @Autowired
    @Qualifier("producer")
    KafkaTemplate<String,ConsumerTransaction> producerKafkaTemplate;

    @Autowired
    TransactionRepository transactionRepository;    

     @KafkaListener(topics = "transaction",groupId = "trans-group")
     public void fetchTransactionDetails(String message) throws JsonMappingException, JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        ConsumerTransaction transaction = objectMapper.readValue(message, ConsumerTransaction.class);
        try{
           // validate  ammount from merchent account 
           int merchantId = transaction.getMerchantId();
           double amount = transaction.getAmount();
           if (isValidAmount(merchantId,amount)) {
            transaction.setStatus("Completed");
            transactionRepository.save(transaction);
            log.info("Transaction saved: {}", transaction);
            System.out.println("Transaction saved: " + transaction);
            double balanceInCard = transactionRepository.getAmountByMerchentId(merchantId);
            double amountLeftInCard = balanceInCard - transaction.getAmount();
            transactionRepository.updateAmountByMerchantId(transaction.getMerchantId(),amountLeftInCard);
            log.info("Updated merchant balance: {}", amountLeftInCard);
        } else {
            transaction.setStatus("Failed");
            transactionRepository.save(transaction);
            log.warn("Transaction failed due to insufficient balance: {}", transaction);
        }
    }catch(Exception e){
        log.error("Error processing transaction: {}", transaction, e);
       producerKafkaTemplate.send("dead-trs-que", transaction); 
       }
     }


    private boolean isValidAmount(int merchantId,double amount) {
        double balanceInCard  = transactionRepository.getAmountByMerchentId(merchantId);
        return (amount<balanceInCard);
    }


}
