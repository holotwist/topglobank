package com.topglobanksoft.user_service.entity;

// This enum should ideally be shared or kept in sync with transactions_service
//ENUM class to define the type of transactions supported
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_SENT,
    TRANSFER_RECEIVED
}