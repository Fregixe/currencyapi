package com.currency.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    @JsonProperty("currency")
    private String name;
    private String code;
    @JsonProperty("mid")
    private double rate;
}
