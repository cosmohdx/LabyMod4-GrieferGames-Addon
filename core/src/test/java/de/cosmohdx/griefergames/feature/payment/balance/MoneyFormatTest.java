package de.cosmohdx.griefergames.feature.payment.balance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyFormatTest {

  private final MoneyFormat german = new MoneyFormat(',', '.', "Tsd.", "Mio.", "Mrd.", "Bio.");
  private final MoneyFormat english = new MoneyFormat('.', ',', "K", "M", "B", "T");

  @Test
  void fullAmountUsesServerStyle() {
    assertEquals("$1,234.56", this.german.format(new BigDecimal("1234.56"), true, false));
    assertEquals("$1,234.50", this.german.format(new BigDecimal("1234.5"), true, false));
    assertEquals("$1,235", this.german.format(new BigDecimal("1234.56"), false, false));
    assertEquals("-$12.00", this.english.format(new BigDecimal("-12"), true, false));
  }

  @Test
  void roundsHalfUpWhenReadingADouble() {
    assertEquals(new BigDecimal("1.01"), MoneyFormat.money(1.005));
    assertEquals(new BigDecimal("1.00"), MoneyFormat.money(1.004));
    assertEquals(new BigDecimal("-1.01"), MoneyFormat.money(-1.005));
    assertEquals(new BigDecimal("1500.00"), MoneyFormat.money(new BigDecimal("1500")));
  }

  @Test
  void compactUsesSuffixesFromOneThousandUp() {
    assertEquals("$999.99", this.german.format(new BigDecimal("999.99"), true, true));
    assertEquals("$1,00 Tsd.", this.german.format(new BigDecimal("1000"), true, true));
    assertEquals("$1,25 Mio.", this.german.format(new BigDecimal("1250000"), true, true));
    assertEquals("$1.25 M", this.english.format(new BigDecimal("1250000"), true, true));
    assertEquals("-$1,25 Mio.", this.german.format(new BigDecimal("-1250000"), true, true));
    assertEquals("$2,00 Mrd.", this.german.format(new BigDecimal("2000000000"), false, true));
  }
}
