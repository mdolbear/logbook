package com.mjdsoftware.logbook.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

@Component
@NoArgsConstructor
public class LocalizationUtils {

    @Getter(value = AccessLevel.PRIVATE)
    @Autowired
    private MessageSource messageSource;


    /**
     * Answer a message for aCode
     * @param aCode String
     * @return String
     */
    public String getLocalizedMessage(String aCode) {

        Locale  tempLocale;

        tempLocale = LocaleContextHolder.getLocale();
        return this.getMessageSource().getMessage(aCode, null, tempLocale);

    }

    /**
     * Answer a message for aCode
     * @param aCode String
     * @param anArgs Object[]
     * @return String
     * @see MessageSource for a description of the values utilized in anArgs
     */
    public String getLocalizedMessage(String aCode,
                                      Object[] anArgs) {

        Locale  tempLocale;

        tempLocale = LocaleContextHolder.getLocale();
        return this.getMessageSource().getMessage(aCode, anArgs, tempLocale);

    }

    /**
     * Answer a message for aCode
     * @param aCode String
     * @param aLocale Locale
     * @return String
     */
    public String getLocalizedMessage(String aCode,
                                      Locale aLocale) {

        return this.getMessageSource().getMessage(aCode, null, aLocale);

    }

    /**
     * Answer a message for aCode
     * @param aCode String
     * @param anArgs Object[]
     * @param aLocale Locale
     * @return String
     * @see MessageSource for a description of the values utilized in anArgs
     */
    public String getLocalizedMessage(String aCode,
                                      Object[] anArgs,
                                      Locale aLocale) {

        return this.getMessageSource().getMessage(aCode, anArgs, aLocale);

    }


    /**
     * Answer a string representation of a number localized for aLocale
     * @param aValue double
     * @param aLocale Locale
     * @return String
     */
    public String asLocalizedNumber(double aValue,
                                    Locale aLocale) {

        NumberFormat tempFormat;

        tempFormat = NumberFormat.getNumberInstance(aLocale);

        return tempFormat.format(aValue);
    }

    /**
     * Answer a string representation of a currency localized for aLocale
     * @param aValue BigDecimal
     * @param aLocale Locale
     * @return String
     */
    public String asLocalizedCurrency(BigDecimal aValue,
                                      Locale aLocale) {

        NumberFormat tempFormat;

        tempFormat = NumberFormat.getCurrencyInstance(aLocale);

        return tempFormat.format(aValue);
    }


    /**
     * Answer a string representation of aDateTime localized for aLocale and following
     * aPattern String. Note TemporalAccessor will include all new Date types such as
     * LocalDateTime, LocalDate, LocalTime, and ZonedDateTime.
     * @param aDateTime TemporalAccessor
     * @param aPattern String
     * @param aLocale Locale
     * @return String
     */
    public String asLocalizedDateTime(TemporalAccessor aDateTime,
                                      String aPattern,
                                      Locale aLocale) {

        DateTimeFormatter   tempFormatter;

        tempFormatter = DateTimeFormatter.ofPattern(aPattern, aLocale);

        return tempFormatter.format(aDateTime);
    }

    /**
     * Format a traditional aDateTime based on aPattern and
     * aLocale. Answer the formatted String
     * @param aDateTime Date
     * @param aPattern String
     * @param aLocale Locale
     * @return String
     */
    public String asLocalizedDateTime(Date aDateTime,
                                      String aPattern,
                                      Locale aLocale) {

        SimpleDateFormat    tempFormat;

        tempFormat = new SimpleDateFormat(aPattern, aLocale);
        return tempFormat.format(aDateTime);

    }


}
