package com.example.demo.controller;

import com.example.demo.model.dto.response.ConvertationAmountDto;
import com.example.demo.model.dto.response.CurrencyDto;
import com.example.demo.model.dto.response.RateDto;
import com.example.demo.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/table")
    List<CurrencyDto> getAllCurrency(){
        return currencyService.getAllCurrency();
    }
    @GetMapping("/convert")
    ConvertationAmountDto getConvertation(@RequestParam String codeFromCurrency,
                                          @RequestParam String codeToCurrency,
                                          @RequestParam double amount){
        return currencyService.convert(codeFromCurrency, codeToCurrency, amount);
    }
    @PostMapping("/rates")
    List<RateDto> getRates(@RequestBody List<String> codes){
        return currencyService.getRates(codes);
    }
}
