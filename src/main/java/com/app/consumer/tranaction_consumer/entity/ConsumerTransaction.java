package com.app.consumer.tranaction_consumer.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "transaction")
public class ConsumerTransaction {
    @Id
    int transactionId;
    long cardNumber;
    double amount;
    Timestamp timestamp;
    int merchantId;
    String status;
}
