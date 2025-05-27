package com.topglobanksoft.bank_accounts_service.controller;

import com.topglobanksoft.bank_accounts_service.dto.AccountCreateDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountUpdateDTO;
import com.topglobanksoft.bank_accounts_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j // Added for logging
public class AccountController {

    private final AccountService accountService;

    // Helper method to get user ID (Keycloak 'sub') from JWT token
    private String getUserIdFromToken(Jwt jwt) {
        String userId = jwt.getSubject(); // 'sub' claim is the Keycloak user ID (UUID)
        if (userId == null || userId.isBlank()) {
            log.error("Keycloak 'sub' claim (userId) is missing or blank in JWT: {}", jwt.getClaims());
            throw new IllegalArgumentException("User ID ('sub' claim) not found in JWT token");
        }
        return userId;
    }


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountDTO> addAccount(@Valid @RequestBody AccountCreateDTO accountCreateDTO,
                                                 @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        AccountDTO newAccount = accountService.addAccount(accountCreateDTO, userId);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @GetMapping("/mis-accounts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AccountDTO>> getMyAccounts(@AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        List<AccountDTO> accounts = accountService.getAccountByUser(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountDTO> getMyAccountById(@PathVariable Long accountId,
                                                       @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        AccountDTO cuenta = accountService.getAccountByIdAndUser(accountId, userId);
        return ResponseEntity.ok(cuenta);
    }

    @PutMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountDTO> updateMyAccount(@PathVariable Long accountId,
                                                      @Valid @RequestBody AccountUpdateDTO accountUpdateDTO,
                                                      @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        AccountDTO cuentaActualizada = accountService.updateAccount(accountId, userId, accountUpdateDTO);
        return ResponseEntity.ok(cuentaActualizada);
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyAccount(@PathVariable Long accountId,
                                                @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        accountService.deleteAccount(accountId, userId);
        return ResponseEntity.noContent().build();
    }

    // Admin endpoints remain largely the same in signature, as they don't use userId from token for operations.
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountDTO>> listAllAccountsAdmin() {
        List<AccountDTO> accounts = accountService.listAllAccountsAdmin();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/admin/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDTO> getAccountByIdAdmin(@PathVariable Long accountId) {
        AccountDTO cuenta = accountService.getAccountByIdAdmin(accountId);
        return ResponseEntity.ok(cuenta);
    }

    @DeleteMapping("/admin/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccountAdmin(@PathVariable Long accountId) {
        accountService.deleteAccountAdmin(accountId);
        return ResponseEntity.noContent().build();
    }
}