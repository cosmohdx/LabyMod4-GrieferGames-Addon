package de.cosmohdx.griefergames.feature.payment.balance;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Renders a balance. The full amount always uses the server style ({@code $1,234.56}).
 * Compact form uses the given separators and suffixes, for example {@code $1,25 Mio.}.
 */
public final class MoneyFormat {

  public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);

  private static final BigDecimal THOUSAND = new BigDecimal("1000");
  private static final BigDecimal MILLION = new BigDecimal("1000000");
  private static final BigDecimal BILLION = new BigDecimal("1000000000");
  private static final BigDecimal TRILLION = new BigDecimal("1000000000000");

  private final char decimal;
  private final char grouping;
  private final String thousand;
  private final String million;
  private final String billion;
  private final String trillion;

  public MoneyFormat(
      char decimal,
      char grouping,
      String thousand,
      String million,
      String billion,
      String trillion
  ) {
    this.decimal = decimal;
    this.grouping = grouping;
    this.thousand = thousand;
    this.million = million;
    this.billion = billion;
    this.trillion = trillion;
  }

  public static BigDecimal money(double value) {
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }

  public static BigDecimal money(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_UP);
  }

  public String format(BigDecimal amount, boolean showCents, boolean compact) {
    if (compact && amount.abs().compareTo(THOUSAND) >= 0) {
      return this.compact(amount);
    }
    return plain(amount, showCents ? 2 : 0);
  }

  private String compact(BigDecimal amount) {
    BigDecimal abs = amount.abs();
    BigDecimal unit;
    String suffix;
    if (abs.compareTo(TRILLION) >= 0) {
      unit = TRILLION;
      suffix = this.trillion;
    } else if (abs.compareTo(BILLION) >= 0) {
      unit = BILLION;
      suffix = this.billion;
    } else if (abs.compareTo(MILLION) >= 0) {
      unit = MILLION;
      suffix = this.million;
    } else {
      unit = THOUSAND;
      suffix = this.thousand;
    }
    BigDecimal scaled = amount.divide(unit, 2, RoundingMode.HALF_UP);
    return number(scaled, 2, this.decimal, this.grouping) + " " + suffix;
  }

  private static String plain(BigDecimal amount, int fractionDigits) {
    return number(amount, fractionDigits, '.', ',');
  }

  private static String number(BigDecimal amount, int fractionDigits, char decimal, char grouping) {
    BigDecimal scaled = amount.setScale(fractionDigits, RoundingMode.HALF_UP);
    boolean negative = scaled.signum() < 0;
    String plain = scaled.abs().toPlainString();
    String whole = plain;
    String fraction = "";
    int dot = plain.indexOf('.');
    if (dot >= 0) {
      whole = plain.substring(0, dot);
      fraction = plain.substring(dot + 1);
    }
    StringBuilder text = new StringBuilder();
    if (negative) {
      text.append('-');
    }
    text.append('$');
    text.append(group(whole, grouping));
    if (fractionDigits > 0) {
      text.append(decimal);
      text.append(fraction);
    }
    return text.toString();
  }

  private static String group(String digits, char separator) {
    StringBuilder grouped = new StringBuilder();
    int length = digits.length();
    for (int index = 0; index < length; index++) {
      if (index > 0 && (length - index) % 3 == 0) {
        grouped.append(separator);
      }
      grouped.append(digits.charAt(index));
    }
    return grouped.toString();
  }
}
