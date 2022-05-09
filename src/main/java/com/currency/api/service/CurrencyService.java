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

    private static final RestTemplate restTemplate = new RestTemplate();
    private final ModelMapper modelMapper;
    private final LoggerService loggerService;

    public static List<Currency> loadCurrencies() {
        ResponseEntity<Info[]> responseEntity = restTemplate
                .getForEntity(TABLE_URL, Info[].class);
        List<Currency> availableCurrencies = new ArrayList<>();
        availableCurrencies.add(new Currency("polski złoty", "PLN", 1.0));
        for (Info info : Objects.requireNonNull(responseEntity.getBody())) {
            availableCurrencies.addAll(info.getRates());
        }
        return availableCurrencies;
    }

    public List<CurrencyDto> getAllCurrencies() {
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
        convertationAmountDto.setAmount(validateAmount(amount * from.getRate() / to.getRate()));
        convertationAmountDto.setName(to.getName());
        loggerService.log("Converted " + amount + " " + currencyFrom + " to " + currencyTo);
        return convertationAmountDto;
    }


    public List<RateDto> getRates(List<String> requiredCurrencies) throws CurrencyNotFoundException {
        List<RateDto> currencyRateDtos = new ArrayList<>();
        List<Currency> availableCurrencies = loadCurrencies();
        for (String code : requiredCurrencies) {
            currencyRateDtos.add(modelMapper.map(getCurrencyByCode(code, availableCurrencies), RateDto.class));
        }
        loggerService.log("Got currencies for " + requiredCurrencies);
        return currencyRateDtos;
    }

    public double validateAmount(double amount) {
        if (amount <= 0 ||
                amount == Double.POSITIVE_INFINITY ||
                amount == Double.MAX_VALUE ||
                Double.compare(+0.0f, amount) == 0) {
            throw new ArithmeticException("Bad input amount");
        }
        return amount;
    }

    public Currency getCurrencyByCode(String currencyCode, List<Currency> availableCurrencies)
            throws CurrencyNotFoundException {
        Currency currency = availableCurrencies.stream()
                .filter(availableCurrency -> availableCurrency.toString().equalsIgnoreCase(currencyCode))
                .findFirst()
                .orElse(null);
        if (currency == null) {
            throw new CurrencyNotFoundException("Currency: " + currencyCode + " not found");
        }
        return currency;
    }
}
