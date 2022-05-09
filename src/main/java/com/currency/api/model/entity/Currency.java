package com.currency.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return Double.compare(currency.rate, rate) == 0 &&
                Objects.equals(name, currency.name) &&
                code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code, rate);
    }

    @Override
    public String toString() {
        return code;
    }
}
