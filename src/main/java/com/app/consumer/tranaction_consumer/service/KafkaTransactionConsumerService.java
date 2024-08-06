package com.app.consumer.tranaction_consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final Logger logger = LoggerFactory.getLogger(KafkaTransactionConsumerService.class);

    @Autowired
    @Qualifier("producer")
    KafkaTemplate<String,ConsumerTransaction> producerKafkaTemplate;

    @Autowired
    TransactionRepository transactionRepository;    

     @KafkaListener(topics = "transaction",groupId = "trans-group")
     public void fetchTransactionDetails(String message) throws JsonMappingException, JsonProcessingException{
        logger.info("=======listening as consumer of transaction with group trans-group========================");
        ObjectMapper objectMapper = new ObjectMapper();
        ConsumerTransaction transaction = objectMapper.readValue(message, ConsumerTransaction.class);
        try{
           // validate  ammount from merchent account 
           int merchantId = transaction.getMerchantId();
           double amount = transaction.getAmount();
           if (isValidAmount(merchantId,amount)) {
            transaction.setStatus("Completed");
            transactionRepository.save(transaction);
            logger.info("Transaction saved: {}", transaction);
            double balanceInCard = transactionRepository.getAmountByMerchentId(merchantId);
            double amountLeftInCard = balanceInCard - transaction.getAmount();
            transactionRepository.updateAmountByMerchantId(transaction.getMerchantId(),amountLeftInCard);
            logger.info("Updated merchant balance: {}", amountLeftInCard);
        } else {
            transaction.setStatus("Failed");
            transactionRepository.save(transaction);
            logger.warn("Transaction failed due to insufficient balance: {}", transaction);
        }
    }catch(Exception e){
        logger.error("Error processing transaction: {}", transaction, e);
       producerKafkaTemplate.send("dead-trs-que", transaction); 
       }
     }


    private boolean isValidAmount(int merchantId,double amount) {
        double balanceInCard  = transactionRepository.getAmountByMerchentId(merchantId);
        return (amount<balanceInCard);
    }


}
