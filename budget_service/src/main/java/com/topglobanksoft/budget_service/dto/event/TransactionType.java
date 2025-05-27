package com.topglobanksoft.budget_service.dto.event;

// Enum to match the one in transactions_service or a shared definition
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_SENT,
    TRANSFER_RECEIVED
    // Add other types if they become relevant for budgeting as expenses
}