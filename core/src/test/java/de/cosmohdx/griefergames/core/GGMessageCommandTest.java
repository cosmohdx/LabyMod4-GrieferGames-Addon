package de.cosmohdx.griefergames.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class GGMessageCommandTest {

  @Test
  void parsesServerAndLocalAmountStyles() {
    assertEquals(0, new BigDecimal("1234.56").compareTo(GGMessageCommand.parseAmount("1,234.56")));
    assertEquals(0, new BigDecimal("1234.56").compareTo(GGMessageCommand.parseAmount("1.234,56")));
    assertEquals(0, new BigDecimal("1000").compareTo(GGMessageCommand.parseAmount("1,000")));
    assertEquals(0, new BigDecimal("1.50").compareTo(GGMessageCommand.parseAmount("1,50")));
    assertEquals(0, new BigDecimal("-2.5").compareTo(GGMessageCommand.parseAmount("-$2.5")));
    assertEquals(0, new BigDecimal("12345678")
        .compareTo(GGMessageCommand.parseAmount("12,345,678")));
  }

  @Test
  void rejectsBlankAndText() {
    assertNull(GGMessageCommand.parseAmount(""));
    assertNull(GGMessageCommand.parseAmount("abc"));
    assertNull(GGMessageCommand.parseAmount(null));
  }
}
