package com.currency.api.model.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CurrencyDto {
    private String name;
    private String code;
}
