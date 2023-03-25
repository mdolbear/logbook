package com.mjdsoftware.logbook.utils;

import com.mjdsoftware.logbook.LogbookApplication;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author michaeldolbear
 */
@SpringBootTest(classes = LogbookApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LocalizationTest {


    @LocalServerPort
    private int port;

    @Autowired
    @Getter(value = AccessLevel.PRIVATE)
    private LocalizationUtils localizationUtils;

    //Constants
    private static final String SPANISH_LOCAL_CODE = "es";
    private static final String ENGLISH_LOCAL_CODE = "en";
    private static final String VALUE_KEY = "error.UNKNOWN_ERROR";
    private static final Locale SPANISH_LOCALE = new Locale(SPANISH_LOCAL_CODE);
    private static final Locale ENGLISH_LOCALE = new Locale(ENGLISH_LOCAL_CODE);

    /**
     * Example of how to retrieve a piece of text based using the resource bundle key.
     */
    @Test
    public void simpleTextRetrievalTest() {

        String tempSpanishResult;
        String tempEnglishResult;

        tempSpanishResult =
                this.getLocalizationUtils().getLocalizedMessage(VALUE_KEY, SPANISH_LOCALE);
        tempEnglishResult =
                this.getLocalizationUtils().getLocalizedMessage(VALUE_KEY, ENGLISH_LOCALE);

        assertTrue("LocalizationTest: Did not properly translate",
                   tempSpanishResult.equals("Error desconocido ha ocurrido") &&
                                                tempEnglishResult.equals("Unknown error occurred"));

    }

    /**
     * Example of how to localize numbers
     */
    @Test
    public void numberFormatTest() {

        String  tempEnglishFormattedNumber;
        String  tempSpanishFormattedNumber;

        tempEnglishFormattedNumber =
                this.getLocalizationUtils().asLocalizedNumber(12345.67d, ENGLISH_LOCALE);

        tempSpanishFormattedNumber =
                this.getLocalizationUtils().asLocalizedNumber(12345.67d, SPANISH_LOCALE);

        assertTrue("Numbers did not translate properly",
                tempEnglishFormattedNumber.equals("12,345.67") &&
                            tempSpanishFormattedNumber.equals("12.345,67"));
    }

    /**
     * Example of how to localize currency
     */
    @Test
    public void currencyFormatTest() {

        String  tempUSFormattedCurrency;
        String  tempFrenchFormattedCurrency;

        tempUSFormattedCurrency =
                this.getLocalizationUtils().asLocalizedCurrency(new BigDecimal(12345.67d), Locale.US);

        tempFrenchFormattedCurrency =
                this.getLocalizationUtils().asLocalizedCurrency(new BigDecimal(12345.67d), Locale.FRANCE);

        assertTrue("Currencies dates did not translate properly",
                tempUSFormattedCurrency.contains("$") &&
                                tempFrenchFormattedCurrency.contains("€"));
    }


    /**
     * Example of how to localize java 8 dates.
     */
    @Test
    public void java8DateTimeTest() {

        String          tempUSDateFormat;
        String          tempSpainDateFormat;
        String          tempPattern = "dd-MMMM-yyyy HH:mm:ss.SSS";
        LocalDateTime   tempDateTime = LocalDateTime.of(2022,
                                                        5, //1-based
                                                    1,
                                                          10,
                                                         10,
                                                         10,
                                                    10);

        tempUSDateFormat =
                this.getLocalizationUtils().asLocalizedDateTime(tempDateTime,
                                                                tempPattern,
                                                                Locale.US);

        tempSpainDateFormat =
                this.getLocalizationUtils().asLocalizedDateTime(tempDateTime,
                                                                tempPattern,
                                                                new Locale(SPANISH_LOCAL_CODE, "ES"));
        assertTrue("Java8 dates did not translate properly",
                tempUSDateFormat.contains("May") &&
                                tempSpainDateFormat.contains("mayo"));
    }


    /**
     * Example of how to localize pre-java8 dates
     */
    @Test
    public void traditionalDateTimeTest() {

        Date                     tempDateTime;
        String                   tempUSDateFormat;
        String                   tempSpainDateFormat;
        String                   tempPattern = "dd-MMMM-yyyy HH:mm:ss:SSS";
        GregorianCalendar        tempCal = new GregorianCalendar(2022,
                                                                 5, //Zero-based, so its really June
                                                             1,
                                                             10,
                                                                10,
                                                                10);

        tempDateTime = tempCal.getTime();
        tempUSDateFormat =
                this.getLocalizationUtils().asLocalizedDateTime(tempDateTime,
                                                                tempPattern,
                                                                Locale.US);

        tempSpainDateFormat =
                this.getLocalizationUtils().asLocalizedDateTime(tempDateTime,
                                                                tempPattern,
                                                                new Locale(SPANISH_LOCAL_CODE, "ES"));
        assertTrue("Traditional dates did not translate properly",
                            tempUSDateFormat.contains("June") &&
                                        tempSpainDateFormat.contains("junio"));



    }


}
