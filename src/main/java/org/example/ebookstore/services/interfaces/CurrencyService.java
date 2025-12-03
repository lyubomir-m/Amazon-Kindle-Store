package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {
    List<Currency> getAllCurrencies();
    Optional<Currency> findByCodeIgnoreCase(String code);
    List<Currency> findAllExceptEuro();
}
