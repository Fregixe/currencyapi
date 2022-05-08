package com.currency.api.model.dto.response;

import lombok.*;

@Data
@Getter
@Setter
public class RateDto {
    private String name;
    private double rate;
}
