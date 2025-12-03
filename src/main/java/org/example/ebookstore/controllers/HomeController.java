package org.example.ebookstore.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.BookDto;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class HomeController {
    private final UserService userService;
    private final CurrencyService currencyService;
    private final BookService bookService;
    private final ScheduledTasksService scheduledTasksService;
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public HomeController(UserService userService, CurrencyService currencyService, BookService bookService, ScheduledTasksService scheduledTasksService, ExchangeRateService exchangeRateService) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.bookService = bookService;
        this.scheduledTasksService = scheduledTasksService;
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping("/change-currency")
    public String changeCurrency(@RequestParam String currencyCode, HttpServletResponse response,
                                 @ModelAttribute("userDto") UserDto userDto) {
        if (userDto != null) {
            Optional<Currency> optional = this.currencyService.findByCodeIgnoreCase(currencyCode);
            if (optional.isPresent()) {
                Currency currency = optional.get();
                userDto.setSelectedCurrency(currency);
                this.userService.updateUserCurrency(userDto.getId(), currency);
            }
        }

        Cookie cookie = new Cookie("selectedCurrency", currencyCode);
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping({"/", "/home"})
    public String viewHomepage(Model model, HttpServletRequest request) {
        Currency currency = this.userService.getSelectedCurrency(request);
        List<BookDto> books = this.bookService.findFirst54BestSellers(currency);
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/about")
    public String viewAboutPage() {
        return "about";
    }

    @GetMapping("/customer-service")
    public String viewCustomerServicePage() {
        return "customer-service";
    }

    @GetMapping("/fx-rates")
    public String viewFxRatesPage(Model model) {
        LocalDate lastUpdated = this.scheduledTasksService.getLastFxRatesUpdateDate();
        List<Currency> currencies = this.currencyService.findAllExceptEuro();
        Map<Currency, BigDecimal> latestRates = new LinkedHashMap<>();

        for (Currency currency : currencies) {
            BigDecimal rate = this.exchangeRateService.getLatestRate(currency.getCode()).get();
            latestRates.put(currency, rate);
        }

        model.addAttribute("lastUpdated", lastUpdated);
        model.addAttribute("latestRates", latestRates);

        return "fx-rates";
    }

}


