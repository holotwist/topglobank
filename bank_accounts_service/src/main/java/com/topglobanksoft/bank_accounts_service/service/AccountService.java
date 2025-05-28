package com.topglobanksoft.bank_accounts_service.service;

import com.topglobanksoft.bank_accounts_service.dto.AccountCreateDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountUpdateDTO;

import java.util.List;

public interface AccountService {

    //Creates and saves a new account
    AccountDTO addAccount(AccountCreateDTO accountCreateDTO, String userId);
    List<AccountDTO> getAccountByUser(String userId);
    AccountDTO getAccountByIdAndUser(Long accountId, String userId);
    AccountDTO updateAccount(Long accountId, String userId, AccountUpdateDTO accountUpdateDTO);
    void deleteAccount(Long accountId, String userId);

    //Obtains all account in the system
    List<AccountDTO> listAllAccountsAdmin();
    AccountDTO getAccountByIdAdmin(Long accountId);
    void deleteAccountAdmin(Long accountId);
}