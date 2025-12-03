package org.example.ebookstore.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "exchange_rates")
public class ExchangeRate extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "currency_from_id", nullable = false)
    private Currency currencyFrom;
    @Column(nullable = false)
    private LocalDate validityDate;
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal rate;

    public ExchangeRate() {
    }

    public ExchangeRate(Currency currencyFrom, LocalDate validityDate, BigDecimal rate) {
        this.currencyFrom = currencyFrom;
        this.validityDate = validityDate;
        this.rate = rate;
    }

    public Currency getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(Currency currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public LocalDate getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(LocalDate validityDate) {
        this.validityDate = validityDate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
