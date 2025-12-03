package org.example.ebookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "currencies")
public class Currency extends BaseEntity {
    @Column(unique = true, nullable = false, length = 30)
    @Size(min = 3, max = 30)
    private String name;
    @Column(unique = true, nullable = false, length = 3)
    @Size(min = 3, max = 3)
    private String code;
    @Column(unique = true, nullable = false, length = 128)
    @Size(min = 1, max = 128)
    private String symbol;
    @OneToMany(mappedBy = "currencyFrom")
    private Set<ExchangeRate> exchangeRates = new HashSet<>();

    public Currency() {
    }

    public Currency(String name, String code, String symbol) {
        this.name = name;
        this.code = code;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Set<ExchangeRate> getExchangeRates() {
        return Collections.unmodifiableSet(this.exchangeRates);
    }
    public void addExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRates.add(exchangeRate);
    }
    public void removeExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRates.remove(exchangeRate);
    }
}
