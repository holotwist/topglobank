package com.topglobanksoft.statistics_service.model;

// This enum should mirror the one in transactions_service or use string types.
// Using a local enum for clarity in StatisticsServiceImpl.
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_SENT,
    TRANSFER_RECEIVED
    // Add others if transactions_service has more and they are relevant
}