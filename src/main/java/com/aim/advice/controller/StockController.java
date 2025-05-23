package com.aim.advice.controller;

import com.aim.advice.api.ApiResponse;
import com.aim.advice.dto.stock.StockRequest;
import com.aim.advice.dto.stock.UpdatePriceRequest;
import com.aim.advice.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ApiResponse<Void> register(@Valid @RequestBody StockRequest request) {
        stockService.register(request);
        return ApiResponse.ok(null);
    }

    @PutMapping("/price")
    public ApiResponse<Void> updatePrice(@Valid @RequestBody UpdatePriceRequest request) {
        stockService.updatePrice(request.getCode(), request.getPrice());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{stockCode}")
    public ApiResponse<Void> delete(@PathVariable String stockCode) {
        stockService.delete(stockCode);
        return ApiResponse.ok(null);
    }
}