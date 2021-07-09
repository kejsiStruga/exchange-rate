package com.scalable.controller;

import com.scalable.service.ExchangeRateService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;

@RestController
public class ExchangeRateController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateController.class);
    private ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService rateService){
        exchangeRateService = rateService;
    }

    @GetMapping(value = "/getECBReferenceRate/{currency}")
    public BigDecimal getECBReferenceRate(@PathVariable String currency) {
        String jsonResults = exchangeRateService.getECBReferenceRate(currency);

        JSONObject jsonObject = new JSONObject(jsonResults);
        JSONArray dataSetsArray = jsonObject.getJSONArray("dataSets");

        return ((BigDecimal) dataSetsArray
                .getJSONObject(0)
                .getJSONObject("series")
                .getJSONObject("0:0:0:0:0")
                .getJSONObject("observations")
                .getJSONArray("0")
                .get(0));

    }

    @GetMapping(value = "/getECBReferenceRatePair/{first_currency}/{second_currency}")
    public BigDecimal getECBReferenceRatePair(@PathVariable String first_currency,
                                          @PathVariable String second_currency) {

        BigDecimal first_currency_euro_rate = getECBReferenceRate(first_currency.toUpperCase(Locale.ROOT));
        BigDecimal second_currency_euro_rate = getECBReferenceRate(second_currency.toUpperCase(Locale.ROOT));

        return exchangeRateService
                .getECBReferenceRatePair(first_currency_euro_rate, second_currency_euro_rate);
    }

    @GetMapping(value = "/getListOfSupportedCurrencies")
    public HashMap<String, String> getListOfSupportedCurrencies() {
        HashMap<String, String> currenciesSupported = new HashMap<>();

        String jsonResults = exchangeRateService.getListOfSupportedCurrencies();

        JSONObject jsonObject = new JSONObject(jsonResults);
        JSONArray currenciesArray = jsonObject.getJSONObject("structure")
                .getJSONObject("dimensions")
                .getJSONArray("series")
                .getJSONObject(1)
                .getJSONArray("values");

        for(int i = 0; i< currenciesArray.length(); i++) {
            JSONObject obj = (JSONObject) currenciesArray.get(i);
            currenciesSupported.put(obj.get("name").toString(), obj.get("id").toString());
        }

        return currenciesSupported;
    }

    @GetMapping(value = "/getConvertedAmount/{amount}/{from_currency}/{to_currency}")
    public BigDecimal getConvertedAmount(@PathVariable String amount,
                                   @PathVariable String from_currency,
                                   @PathVariable String to_currency){
        double doubleConvertedAmount = Double.parseDouble(amount);
        BigDecimal rate;

        if (from_currency.equals("EUR")) {
            rate = getECBReferenceRate(to_currency);
        } else if (to_currency.equals("EUR")) {
            rate = getECBReferenceRate(from_currency)
                    .pow(-1, MathContext.DECIMAL64)
                    .round(new MathContext(4, RoundingMode.HALF_UP));
        } else {
            rate = getECBReferenceRatePair(from_currency, to_currency);
        }

        return new BigDecimal(doubleConvertedAmount).multiply(rate);

    }

}
