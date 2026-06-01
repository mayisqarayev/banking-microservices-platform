package com.mayis.account_service.controller;

import com.mayis.account_service.dto.AccountResponseDto;
import com.mayis.account_service.dto.AccountAmountRequestDto;
import com.mayis.account_service.dto.ChangeAccountStatusRequestDto;
import com.mayis.account_service.dto.CreateAccountRequestDto;
import com.mayis.account_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDto create(@Valid @RequestBody CreateAccountRequestDto request) {
        return accountService.create(request);
    }

    @GetMapping("/{id}")
    public AccountResponseDto getById(@PathVariable UUID id) {
        return accountService.getById(id);
    }

    @GetMapping("/by-customer/{customerId}")
    public List<AccountResponseDto> getByCustomerId(@PathVariable UUID customerId) {
        return accountService.getByCustomerId(customerId);
    }

    @PatchMapping("/{id}/block")
    public AccountResponseDto block(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) ChangeAccountStatusRequestDto request
    ) {
        return accountService.block(id, request);
    }

    @PatchMapping("/{id}/unblock")
    public AccountResponseDto unblock(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) ChangeAccountStatusRequestDto request
    ) {
        return accountService.unblock(id, request);
    }

    @PatchMapping("/{id}/close")
    public AccountResponseDto close(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) ChangeAccountStatusRequestDto request
    ) {
        return accountService.close(id, request);
    }

    @PostMapping("/{id}/debit")
    public AccountResponseDto debit(
            @PathVariable UUID id,
            @Valid @RequestBody AccountAmountRequestDto request
    ) {
        return accountService.debit(id, request);
    }

    @PostMapping("/{id}/credit")
    public AccountResponseDto credit(
            @PathVariable UUID id,
            @Valid @RequestBody AccountAmountRequestDto request
    ) {
        return accountService.credit(id, request);
    }
}
