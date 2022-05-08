package com.currency.api.model.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class CurrencyDto {
    private String name;
    private String code;
}
