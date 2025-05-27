package com.topglobanksoft.user_service.entity;

// This enum should ideally be shared or kept in sync with transactions_service
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_SENT,
    TRANSFER_RECEIVED
}