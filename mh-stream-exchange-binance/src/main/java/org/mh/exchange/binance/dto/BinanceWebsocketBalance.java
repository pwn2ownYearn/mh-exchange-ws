package org.mh.exchange.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;

import java.math.BigDecimal;

public final class BinanceWebsocketBalance {

  private final Currency currency;
  private final BigDecimal free;
  private final BigDecimal locked;

  public BinanceWebsocketBalance(
      @JsonProperty("a") String asset,
      @JsonProperty("f") BigDecimal free,
      @JsonProperty("l") BigDecimal locked) {
    this.currency = Currency.getInstance(asset);
    this.locked = locked;
    this.free = free;
  }

  public Currency getCurrency() {
    return currency;
  }

  public BigDecimal getTotal() {
    return free.add(locked);
  }

  public BigDecimal getAvailable() {
    return free;
  }

  public BigDecimal getLocked() {
    return locked;
  }

  public Balance toBalance() {
    return new Balance(currency, getTotal(), getAvailable(), getLocked());
  }

  @Override
  public String toString() {
    return "Balance[currency=" + currency + ", free=" + free + ", locked=" + locked + "]";
  }
}
