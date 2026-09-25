package de.cosmohdx.griefergames.feature.payment.hud;

import de.cosmohdx.griefergames.GrieferGames;
import java.math.BigDecimal;

public class BalanceHudWidget extends AccountBalanceHudWidget {

  public BalanceHudWidget(GrieferGames griefergames) {
    super(
        griefergames,
        "gg_balance",
        "textures/hud/economy_cash.png",
        new BigDecimal("12345.67"),
        () -> griefergames.balances().cashKnown(),
        () -> griefergames.balances().cash()
    );
  }
}
