package com.topglobanksoft.transactions_service.mapper;

import com.topglobanksoft.transactions_service.dto.transaction.TransactionDTO;
import com.topglobanksoft.transactions_service.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Maps between Transaction entities and DTOs
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "category", target = "category") // MapStruct will use CategoryMapper for this
    TransactionDTO toDto(Transaction transaction);

    @Mapping(source = "category", target = "category")
    Transaction toEntity(TransactionDTO transactionDTO);
}