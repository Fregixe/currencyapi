package com.currency.api.model.dto.response;

import lombok.*;

@Data
@Getter
@Setter
public class ConvertationAmountDto {
    private String name;
    private double amount;
}
