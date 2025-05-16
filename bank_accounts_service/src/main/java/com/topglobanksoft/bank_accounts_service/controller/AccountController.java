package com.topglobanksoft.bank_accounts_service.controller;

import com.topglobanksoft.bank_accounts_service.dto.AccountCreateDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountUpdateDTO;
import com.topglobanksoft.bank_accounts_service.service.AccountService;
// import com.topglobanksoft.bank_accounts_service.util.JwtUtil; // Utility class for extracting claims
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.Authentication; // To get the token
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt; // Represents the decoded token
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.Map; // Not used

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Helper method to get user ID from JWT token
    // The claim containing the ID may vary ('sub', 'userId', 'id', etc.)
    // We need to ensure the Auth Server includes this claim
    private Long getUserIdFromToken(Jwt jwt) {
        // Example: Assuming ID is in a claim called "userId"
        Object userIdClaim = jwt.getClaim("userId"); // Standard claim often used for user ID
        if (userIdClaim == null) {
            // Fallback to 'sub' (subject) claim if 'userId' is not present
            userIdClaim = jwt.getSubject();
        }

        if (userIdClaim instanceof Integer) {
            return ((Integer) userIdClaim).longValue();
        } else if (userIdClaim instanceof Long) {
            return (Long) userIdClaim;
        } else if (userIdClaim instanceof String) {
            try {
                return Long.parseLong((String) userIdClaim);
            } catch (NumberFormatException e) {
                // If parsing 'sub' as Long fails, it might be a non-numeric subject.
                // Log this or handle as appropriate.
            }
        }
        throw new IllegalArgumentException("Could not find or parse user ID (claim 'userId' or 'sub') in JWT token");
    }


    // --- Endpoints for Users (RF-006, RF-014) ---

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountDTO> agregarAccount(@Valid @RequestBody AccountCreateDTO cuentaCreateDTO,
                                                     @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = getUserIdFromToken(jwt);
        AccountDTO nuevaAccount = accountService.addAccount(cuentaCreateDTO, usuarioId);
        return new ResponseEntity<>(nuevaAccount, HttpStatus.CREATED);
    }

    @GetMapping("/mis-cuentas")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AccountDTO>> obtenerMisAccounts(@AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = getUserIdFromToken(jwt);
        List<AccountDTO> cuentas = accountService.getAccountByUser(usuarioId);
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{idAccount}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountDTO> obtenerMiAccountPorId(@PathVariable Long idAccount,
                                                            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = getUserIdFromToken(jwt);
        AccountDTO cuenta = accountService.getAccountByIdAndUser(idAccount, usuarioId);
        return ResponseEntity.ok(cuenta);
    }

    @PutMapping("/{idAccount}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountDTO> actualizarMiAccount(@PathVariable Long idAccount,
                                                          @Valid @RequestBody AccountUpdateDTO accountUpdateDTO,
                                                          @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = getUserIdFromToken(jwt);
        AccountDTO cuentaActualizada = accountService.updateAccount(idAccount, usuarioId, accountUpdateDTO);
        return ResponseEntity.ok(cuentaActualizada);
    }

    @DeleteMapping("/{idAccount}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> eliminarMiAccount(@PathVariable Long idAccount,
                                                  @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = getUserIdFromToken(jwt);
        accountService.deleteAccount(idAccount, usuarioId);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints for Administrators (RF-010) ---

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountDTO>> listarTodasLasAccountsAdmin() {
        List<AccountDTO> cuentas = accountService.listAllAccountsAdmin();
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/admin/{idAccount}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDTO> obtenerAccountPorIdAdmin(@PathVariable Long idAccount) {
        AccountDTO cuenta = accountService.getAccountByIdAdmin(idAccount);
        return ResponseEntity.ok(cuenta);
    }

    @DeleteMapping("/admin/{idAccount}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccountAdmin(@PathVariable Long idAccount) {
        accountService.deleteAccountAdmin(idAccount);
        return ResponseEntity.noContent().build();
    }
}