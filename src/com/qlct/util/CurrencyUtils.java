package com.qlct.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyUtils {
    private static final Locale VIETNAMESE_LOCALE = new Locale("vi", "VN");
    private static final String SUFFIX = " đ";

    private CurrencyUtils() {
    }

    public static String format(BigDecimal amount) {
        BigDecimal safeAmount = amount == null ? BigDecimal.ZERO : amount;
        NumberFormat formatter = NumberFormat.getNumberInstance(VIETNAMESE_LOCALE);
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        return formatter.format(safeAmount) + SUFFIX;
    }
}
