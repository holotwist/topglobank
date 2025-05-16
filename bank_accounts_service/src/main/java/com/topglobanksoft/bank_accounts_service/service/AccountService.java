package com.topglobanksoft.bank_accounts_service.service;

import com.topglobanksoft.bank_accounts_service.dto.AccountCreateDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountUpdateDTO;

import java.util.List;

public interface AccountService {

    // --- User Operations ---
    AccountDTO addAccount(AccountCreateDTO cuentaCreateDTO, Long usuarioId);
    List<AccountDTO> getAccountByUser(Long usuarioId);
    AccountDTO getAccountByIdAndUser(Long idCuenta, Long usuarioId);
    AccountDTO updateAccount(Long idCuenta, Long usuarioId, AccountUpdateDTO cuentaUpdateDTO);
    void deleteAccount(Long idCuenta, Long usuarioId);

    // --- Administrator Operations ---
    List<AccountDTO> listAllAccountsAdmin();
    AccountDTO getAccountByIdAdmin(Long idCuenta);
    void deleteAccountAdmin(Long idCuenta); // Admin can delete any account
    // More admin methods could be added if needed (e.g. search by criteria).
}