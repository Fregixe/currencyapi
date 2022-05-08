package com.currency.api.service;

import com.currency.api.model.dto.response.ConvertationAmountDto;
import com.currency.api.model.entity.Currency;
import com.currency.api.model.entity.Info;
import com.currency.api.model.dto.response.CurrencyDto;
import com.currency.api.model.dto.response.RateDto;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CurrencyService {
    private static final String TABLE_URL = "https://api.nbp.pl/api/exchangerates/tables/A?format=json";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ModelMapper modelMapper;
    private final LoggerService loggerService;

    public List<Currency> loadCurrencies(){
        ResponseEntity<Info[]> responseEntity = restTemplate
                .getForEntity(TABLE_URL, Info[].class);
        List<Currency> rates = new ArrayList<>();
        for (Info info : Objects.requireNonNull(responseEntity.getBody())) {
            rates.addAll(info.getRates().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        return rates;
    }
    public List<CurrencyDto> getAllCurrency() {
        loggerService.log("Get currencies table");
        return loadCurrencies().stream()
                .map(currency -> modelMapper.map(currency, CurrencyDto.class))
                .collect(Collectors.toList());
    }

    public ConvertationAmountDto convert(String currencyFrom, String currencyTo, double amount) {
        loggerService.log("Converted " + amount + " from " + currencyFrom + " to " + currencyTo);
        Currency from = loadCurrencies().stream()
                .filter(currencyResDto -> currencyResDto.getCode().equals(currencyFrom))
                .findFirst()
                .orElse(null);
        Currency to = loadCurrencies().stream()
                .filter(currencyResDto -> currencyResDto.getCode().equals(currencyTo))
                .findFirst()
                .orElse(null);
        if (currencyFrom.equals("PLN")) {
            if (to == null) {
                return null;
            }
            return ConvertationAmountDto.builder()
                    .amount(amount / to.getRate())
                    .name(to.getName()).build();
        } else if (currencyTo.equals("PLN")) {
            if (from == null) {
                return null;
            }
            return ConvertationAmountDto.builder()
                    .amount(amount * from.getRate())
                    .name("polski złoty").build();
        } else {
            if (to == null || from == null) {
                return null;
            }
            return ConvertationAmountDto.builder()
                    .amount(amount * from.getRate() / to.getRate())
                    .name(to.getName()).build();
        }
    }

    public List<RateDto> getRates(List<String> requiredCurrency) {
        loggerService.log("Got currencies for " + requiredCurrency);
        List<RateDto> currencies = loadCurrencies().stream()
                .filter(currency ->
                        requiredCurrency.contains(currency.getCode()) &&
                                requiredCurrency.remove(currency.getCode()))
                .map(currency -> modelMapper.map(currency, RateDto.class))
                .collect(Collectors.toList());
        if (!requiredCurrency.isEmpty()) {
            throw new NumberFormatException("Currency not found: " + requiredCurrency);
        }
        return currencies;
    }

}
