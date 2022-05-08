package com.currency.api.service;

import com.currency.api.exception.CurrencyNotFoundException;
import com.currency.api.model.dto.response.ConvertationAmountDto;
import com.currency.api.model.dto.response.CurrencyDto;
import com.currency.api.model.dto.response.RateDto;
import com.currency.api.model.entity.Currency;
import com.currency.api.model.entity.Info;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CurrencyService {
    private static final String TABLE_URL = "https://api.nbp.pl/api/exchangerates/tables/A?format=json";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ModelMapper modelMapper;
    private final LoggerService loggerService;

    public List<Currency> loadCurrencies() {
        ResponseEntity<Info[]> responseEntity = restTemplate
                .getForEntity(TABLE_URL, Info[].class);
        List<Currency> rates = new ArrayList<>();
        for (Info info : Objects.requireNonNull(responseEntity.getBody())) {
            rates.addAll(info.getRates());
        }
        return rates;
    }

    public List<CurrencyDto> getAllCurrency() {
        List<CurrencyDto> currenciesTable = loadCurrencies().stream()
                .map(currency -> modelMapper.map(currency, CurrencyDto.class))
                .collect(Collectors.toList());
        loggerService.log("Got currencies table");
        return currenciesTable;
    }

    public ConvertationAmountDto convert(String currencyFrom, String currencyTo, double amount)
            throws CurrencyNotFoundException {
        ConvertationAmountDto convertationAmountDto = new ConvertationAmountDto();
        List<Currency> availableCurrencies = loadCurrencies();
        Currency from = getCurrencyByCode(currencyFrom, availableCurrencies);
        Currency to = getCurrencyByCode(currencyTo, availableCurrencies);
        if (currencyFrom.equals("PLN")) {
            convertationAmountDto.setAmount(validateAmount(amount / to.getRate()));
            convertationAmountDto.setName(to.getName());
        } else if (currencyTo.equals("PLN")) {
            convertationAmountDto.setAmount(validateAmount(amount * from.getRate()));
            convertationAmountDto.setName("polski złoty");
        } else {
            convertationAmountDto.setAmount(validateAmount(amount * from.getRate() / to.getRate()));
            convertationAmountDto.setName(to.getName());
        }
        loggerService.log("Converted " + amount + " " + currencyFrom + " to " + currencyTo);
        return convertationAmountDto;
    }


    public List<RateDto> getRates(List<String> requiredCurrency) throws CurrencyNotFoundException {
        List<RateDto> currencies = loadCurrencies().stream()
                .filter(currency ->
                        requiredCurrency.contains(currency.getCode()) &&
                                requiredCurrency.remove(currency.getCode()))
                .map(currency -> modelMapper.map(currency, RateDto.class))
                .collect(Collectors.toList());
        if (!requiredCurrency.isEmpty()) {
            throw new CurrencyNotFoundException("Currency not found: " + requiredCurrency);
        }
        loggerService.log("Got currencies for " + requiredCurrency);
        return currencies;
    }

    private double validateAmount(double amount) {
        if (amount <= 0 ||
                amount == Double.POSITIVE_INFINITY ||
                amount == Double.MAX_VALUE ||
                Double.compare(-0.0f, amount) == 0 ||
                Double.compare(+0.0f, amount) == 0) {
            throw new ArithmeticException("Bad input amount");
        }
        return amount;
    }

    private Currency getCurrencyByCode(String currencyCode, List<Currency> availableCurrencies)
            throws CurrencyNotFoundException {
        Currency currency = availableCurrencies.stream()
                .filter(currencyResDto -> currencyResDto.getCode().equals(currencyCode))
                .findFirst()
                .orElse(null);
        if (currency == null && !currencyCode.equals("PLN")) {
            throw new CurrencyNotFoundException("Currency: " + currencyCode + " not found");
        }
        return currency;
    }
}
