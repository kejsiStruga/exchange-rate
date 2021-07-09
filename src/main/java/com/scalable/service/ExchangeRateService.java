package com.scalable.service;

import com.scalable.controller.ExchangeRateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Service
public class ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateController.class);

    private String wsEntryPoint = "https://sdw-wsrest.ecb.europa.eu/service/";
    private String resource = "data/";
    private String flowRef = "EXR/";
    private String frequency = "D.";

    @Autowired
    private RestTemplate restTemplate;
    public String getECBReferenceRate(String currency) {
        String seriesKey = frequency
                + currency.toUpperCase(Locale.ROOT)
                + ".EUR.SP00.A?startPeriod=";

        String uri = wsEntryPoint
                + resource
                + flowRef
                + seriesKey
                + getYesterdayDate() +"&endPeriod="
                + getToday();

        logger.info("URI: " + uri);

        return restTemplate.getForObject(uri, String.class);
    }

    public double getECBReferenceRatePair(double first_currency_euro_rate,
                                              double second_currency_euro_rate ) {
        logger.info("First: " + first_currency_euro_rate);
        logger.info("Second: " + second_currency_euro_rate);

        logger.info("Divide: " + first_currency_euro_rate/second_currency_euro_rate);
        return second_currency_euro_rate/first_currency_euro_rate;
    }

    public String getListOfSupportedCurrencies() {
        // D..EUR.SP00.A
        String seriesKey = "D..EUR.SP00.A";

        String uri = wsEntryPoint
                + resource
                + flowRef
                + seriesKey
                + "?startPeriod="
                +getYesterdayDate() +"&endPeriod="
                + getToday();

        logger.info(uri);

        return restTemplate.getForObject(uri, String.class);
    }

    private String getYesterdayDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        return simpleDateFormat.format(yesterday);
    }

    private String getToday() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }

}
