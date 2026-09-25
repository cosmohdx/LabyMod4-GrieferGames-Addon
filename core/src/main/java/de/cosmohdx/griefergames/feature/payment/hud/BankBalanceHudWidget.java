package de.cosmohdx.griefergames.feature.payment.hud;

import de.cosmohdx.griefergames.GrieferGames;
import java.math.BigDecimal;

public class BankBalanceHudWidget extends AccountBalanceHudWidget {

  public BankBalanceHudWidget(GrieferGames griefergames) {
    super(
        griefergames,
        "gg_bank",
        "textures/bank.png",
        new BigDecimal("98765.43"),
        () -> griefergames.balances().bankKnown(),
        () -> griefergames.balances().bank()
    );
  }
}
