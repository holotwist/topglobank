package com.topglobanksoft.transactions_service.controller;

import com.topglobanksoft.transactions_service.dto.transaction.*;
import com.topglobanksoft.transactions_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Handles transaction operations (deposits, withdrawals, transfers)
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    /**
     * Extracts user ID from JWT token
     */
    private String getUserIdFromToken(Jwt jwt) { // Changed return type to String
        String userId = jwt.getSubject();
        if (userId == null || userId.isBlank()) {
            log.error("Keycloak 'sub' claim (userId) is missing or blank in JWT: {}", jwt.getClaims());
            throw new IllegalArgumentException("User ID ('sub' claim) not found in JWT token");
        }
        return userId;
    }
    /**
     * Processes deposit request (user)
     */
    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDTO> deposit(@Valid @RequestBody DepositRequestDTO depositRequestDTO,
                                                  @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        TransactionDTO transaction = transactionService.performDeposit(userId, depositRequestDTO);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
    /**
     * Processes withdrawal request (user)
     */
    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDTO> withdraw(@Valid @RequestBody WithdrawalRequestDTO withdrawalRequestDTO,
                                                   @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        TransactionDTO transaction = transactionService.performWithdrawal(userId, withdrawalRequestDTO);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
    /**
     * Processes transfer request (user)
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDTO> transfer(@Valid @RequestBody TransferRequestDTO transferRequestDTO,
                                                   @AuthenticationPrincipal Jwt jwt) {
        String senderUserId = getUserIdFromToken(jwt);
        TransactionDTO transaction = transactionService.performTransfer(senderUserId, transferRequestDTO);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
    /**
     * Gets paginated transactions for current user (user)
     */

    @GetMapping("/my-transactions") // "mis-transacciones" consistent with bank_accounts
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TransactionDTO>> getMyTransactions(
            TransactionFilterDTO filters, // Consider @ModelAttribute if GET with body not desired
            @PageableDefault(size = 10, sort = "date") Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        Page<TransactionDTO> transactions = transactionService.getTransactionsForUser(userId, filters, pageable);
        return ResponseEntity.ok(transactions);
    }
    /**
     * Gets single transaction by ID (user)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionDTO> getMyTransactionById(@PathVariable Long id,
                                                               @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromToken(jwt);
        TransactionDTO transaction = transactionService.getTransactionByIdForUser(id, userId);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Gets all transactions (admin)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactionsAdmin(
            TransactionFilterDTO filters, // Consider @ModelAttribute
            @PageableDefault(size = 20, sort = "date") Pageable pageable) {
        Page<TransactionDTO> transactions = transactionService.getAllTransactionsAdmin(filters, pageable);
        return ResponseEntity.ok(transactions);
    }
    /**
     * Gets transaction by ID (admin)
     */
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionDTO> getTransactionByIdAdmin(@PathVariable Long id) {
        TransactionDTO transaction = transactionService.getTransactionByIdAdmin(id);
        return ResponseEntity.ok(transaction);
    }
    /**
     * Gets transactions for report (user+admin)
     */

    @GetMapping("/user-range")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TransactionDTO>> getTransactionsForReport(
            @RequestParam("userId") String userIdParam, // Changed to String
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal Jwt jwt) {

        String requesterUserId = getUserIdFromToken(jwt);
        // Check if the JWT has the claim "roles" and if it contains "ADMIN"
        List<String> roles = jwt.getClaimAsStringList("roles"); // This might be inside "realm_access", for now, as it is, lol
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        boolean isAdmin = false;
        if (realmAccess != null) {
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            if (realmRoles != null && realmRoles.contains("ADMIN")) {
                isAdmin = true;
            }
        }


        if (!isAdmin && !requesterUserId.equals(userIdParam)) {
            log.warn("User {} attempted to access transactions for user {} without ADMIN role.", requesterUserId, userIdParam);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Fetching transactions for report: UserID={}, StartDate={}, EndDate={}", userIdParam, startDate, endDate);
        List<TransactionDTO> transactions = transactionService.getTransactionsByUserIdAndDateRange(userIdParam, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    /**
     * Gets transactions by date range (admin)
     */
    @GetMapping("/admin/all-by-date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsByDateRangeForAdmin(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Admin request: Fetching all transactions from {} to {}", startDate, endDate);
        List<TransactionDTO> transactions = transactionService.getAllTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
}