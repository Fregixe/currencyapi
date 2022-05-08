package com.currency.api.configuration;

import com.currency.api.model.entity.Currency;
import com.currency.api.model.dto.response.CurrencyDto;
import com.currency.api.model.dto.response.RateDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapCurrencyToCurrencyDTO(modelMapper);
        mapCurrencyToRateDto(modelMapper);
        return modelMapper;
    }

    private void mapCurrencyToCurrencyDTO(ModelMapper modelMapper){
        TypeMap<Currency, CurrencyDto> config =
                modelMapper.createTypeMap(Currency.class, CurrencyDto.class);
        config.addMapping(
                Currency::getName,
                CurrencyDto::setName
        );
        config.addMapping(
                Currency::getCode,
                CurrencyDto::setCode
        );
    }
    private void mapCurrencyToRateDto(ModelMapper modelMapper){
        TypeMap<Currency, RateDto> config =
                modelMapper.createTypeMap(Currency.class, RateDto.class);

        config.addMapping(
                Currency::getName,
                RateDto::setName
        );
        config.addMapping(
                Currency::getRate,
                RateDto::setRate
        );
    }
}
