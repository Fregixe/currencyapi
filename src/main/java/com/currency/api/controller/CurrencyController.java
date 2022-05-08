package com.currency.api.controller;

import com.currency.api.exception.CurrencyNotFoundException;
import com.currency.api.model.dto.response.ConvertationAmountDto;
import com.currency.api.model.dto.response.CurrencyDto;
import com.currency.api.model.dto.response.RateDto;
import com.currency.api.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currency")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/table")
    ResponseEntity<List<CurrencyDto>> getAllCurrency() {
        return ResponseEntity.ok().body(currencyService.getAllCurrency());
    }

    @GetMapping("/convert")
    ResponseEntity<ConvertationAmountDto> getConvertation(@RequestParam String codeFromCurrency,
                                                          @RequestParam String codeToCurrency,
                                                          @RequestParam double amount)
            throws CurrencyNotFoundException {
        return ResponseEntity.ok().body(currencyService.convert(codeFromCurrency, codeToCurrency, amount));
    }

    @PostMapping("/rates")
    ResponseEntity<List<RateDto>> getRates(@RequestBody List<String> codes) throws CurrencyNotFoundException {
        return ResponseEntity.ok().body(currencyService.getRates(codes));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ArithmeticException.class, CurrencyNotFoundException.class})
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
