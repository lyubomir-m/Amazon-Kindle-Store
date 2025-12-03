package org.example.ebookstore.services.implementations;

import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.repositories.CurrencyRepository;
import org.example.ebookstore.services.interfaces.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return this.currencyRepository.findAll();
    }

    @Override
    public Optional<Currency> findByCodeIgnoreCase(String code) {
        return this.currencyRepository.findByCodeIgnoreCase(code);
    }

    @Override
    public List<Currency> findAllExceptEuro() {
        return this.currencyRepository.findByIdNot(1L);
    }
}
