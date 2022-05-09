package com.currency.api.service;

import com.currency.api.exception.CurrencyNotFoundException;
import com.currency.api.model.dto.response.CurrencyDto;
import com.currency.api.model.dto.response.RateDto;
import com.currency.api.model.entity.Currency;
import com.currency.api.repository.ActionRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceUnitTest {

    private static final List<Currency> testCurrencies = new ArrayList<>() {{
        add(new Currency("polskie złoty", "PLN", 1.0));
        add(new Currency("dolar amerykański", "USD", 4.0));
        add(new Currency("euro", "EUR", 5.0));
    }};

    private static final String INVALID_CODE = "CZK";

    @InjectMocks
    private CurrencyService currencyService;
    @InjectMocks
    private ModelMapper modelMapper;
    @InjectMocks
    private LoggerService loggerService;
    @Mock
    private ActionRepository actionRepository;

    private MockedStatic<CurrencyService> mockedStatic;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyService = new CurrencyService(modelMapper, loggerService);
    }

    @Test
    void loadCurrenciesTest_ShouldReturnListOfCurrencies() {
        List<Currency> testCurrencies = CurrencyService.loadCurrencies();
        assertTrue(testCurrencies.size() > 4);
        for (Currency currency : testCurrencies) {
            assertNotNull(currency.getCode());
            assertNotNull(currency.getName());
            assertTrue(currency.getRate() > 0);
        }
    }

    @Test
    void getAllCurrenciesTest_ShouldReturnListOfCurrencyDtos_AndLogThat() {
        List<CurrencyDto> testCurrencies = currencyService.getAllCurrencies();
        assertTrue(testCurrencies.size() > 4);
        assertEquals("Got currencies table", loggerService.getAction().getName());
        assertNotNull(loggerService.getAction().getTime());
        for (CurrencyDto currencyDto : testCurrencies) {
            assertNotNull(currencyDto.getCode());
            assertNotNull(currencyDto.getName());
        }
    }

    @SneakyThrows
    @Test
    void convertTest_ShouldReturnConvertationResultOrThrowException_WhenCurrencyInvalid() {
        mockedStatic = Mockito.mockStatic(CurrencyService.class);
        double testAmount = 10;
        Currency currencyFrom = testCurrencies.get(0);
        Currency currencyTo = testCurrencies.get(1);

        double expectedAmount = currencyFrom.getRate() * testAmount / currencyTo.getRate();

        mockedStatic.when(CurrencyService::loadCurrencies).thenReturn(testCurrencies);

        assertEquals(expectedAmount,
                currencyService.convert(currencyFrom.getCode(), currencyTo.getCode(), testAmount)
                        .getAmount());
        assertEquals("Converted " + testAmount  + " " + currencyFrom + " to " + currencyTo,
                loggerService.getAction().getName());
        assertThrows(CurrencyNotFoundException.class,
                () -> {
                    currencyService.convert(INVALID_CODE, currencyTo.getCode(), testAmount);
                    currencyService.convert(currencyFrom.getCode(), INVALID_CODE, testAmount);
                });


        mockedStatic.close();

    }

    @Test
    void getAllCurrenciesTest_ShouldReturnRateDtoListOrThrowException_WhenCodeInvalid()
            throws CurrencyNotFoundException {
        mockedStatic = Mockito.mockStatic(CurrencyService.class);
        Currency firstTestCurrency = testCurrencies.get(0);
        Currency secondTestCurrency = testCurrencies.get(1);

        mockedStatic.when(CurrencyService::loadCurrencies).thenReturn(testCurrencies);

        List<RateDto> resultRates =
                currencyService.getRates(Arrays.asList(firstTestCurrency.toString(),
                        secondTestCurrency.toString()));
        assertEquals(2, resultRates.size());
        assertEquals(resultRates.get(0).getRate(), firstTestCurrency.getRate());
        assertEquals(resultRates.get(1).getRate(), secondTestCurrency.getRate());
        assertEquals("Got currencies for " +
                        Arrays.asList(firstTestCurrency.toString(), secondTestCurrency.toString()),
                loggerService.getAction().getName());
        assertThrows(CurrencyNotFoundException.class,
                () -> currencyService.getRates(Arrays.asList(INVALID_CODE)));
        mockedStatic.close();
    }

    @Test
    void validateAmountTest_ShouldReturnAmountOrThrowException_WhenAmountInvalid() {
        int testAmount = 10;
        assertThrows(ArithmeticException.class,
                () -> currencyService.validateAmount(0));
        assertThrows(ArithmeticException.class,
                () -> currencyService.validateAmount(Double.MAX_VALUE * Double.MAX_VALUE));
        assertThrows(ArithmeticException.class,
                () -> currencyService.validateAmount(Double.MAX_VALUE + 1));
        assertThrows(ArithmeticException.class,
                () -> currencyService.validateAmount(1 / (Double.MAX_VALUE * Double.MAX_VALUE)));
        assertEquals(testAmount, currencyService.validateAmount(testAmount));
    }

    @Test
    void getCurrencyByCodeTest_ShouldReturnCurrencyOrThrowException_WhenCodeInvalid()
            throws CurrencyNotFoundException {
        mockedStatic = Mockito.mockStatic(CurrencyService.class);
        Currency testCurrency = testCurrencies.get(2);

        mockedStatic.when(CurrencyService::loadCurrencies).thenReturn(testCurrencies);

        Currency returnCurrency =
                currencyService.getCurrencyByCode(testCurrency.getCode(), testCurrencies);

        assertEquals(testCurrency, returnCurrency);

        assertThrows(CurrencyNotFoundException.class,
                () -> currencyService.getCurrencyByCode(INVALID_CODE, testCurrencies));
        mockedStatic.close();
    }
}