package org.mh.stream.exchange.core;

import io.reactivex.Observable;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.exceptions.ExchangeSecurityException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.market.hedge.core.ParsingCurrencyPair;

public interface StreamingTradeService {

  /**
   * Get the changes of order state for the logged-in user.
   *
   * <p><strong>Warning:</strong> there are currently no guarantees that messages will arrive in
   * order, that messages will not be skipped, or that any initial state message will be sent on
   * connection. Most exchanges have a recommended approach for managing this, involving timestamps,
   * sequence numbers and a separate REST API for re-sync when inconsistencies appear. You should
   * implement these approaches, if required, by combining calls to this method with {@link
   * org.knowm.xchange.service.trade.TradeService#getOpenOrders()}.
   *
   * <p><strong>Emits</strong> {@link
   * org.mh.service.core.exception.NotConnectedException} When not connected to the
   * WebSocket API.
   *
   * <p><strong>Immediately throws</strong> {@link ExchangeSecurityException} if called without
   * authentication details
   *
   * @param currencyPair Currency pair of the order changes.
   * @return {@link Observable} that emits {@link org.knowm.xchange.dto.Order} when exchange sends the update.
   */
  default Observable<Order> getOrderChanges(ParsingCurrencyPair currencyPair, Object... args) {
    throw new NotYetImplementedForExchangeException();
  }

  /**
   * Gets authenticated trades for the logged-in user.
   *
   * <p><strong>Warning:</strong> there are currently no guarantees that messages will arrive in
   * order, that messages will not be skipped, or that any initial state message will be sent on
   * connection. Most exchanges have a recommended approach for managing this, involving timestamps,
   * sequence numbers and a separate REST API for re-sync when inconsistencies appear. You should
   * implement these approaches, if required, by combining calls to this method with {@link
   * org.knowm.xchange.service.trade.TradeService#getTradeHistory(TradeHistoryParams)} (org.knowm.xchange.service.trade.params.TradeHistoryParams)}
   *
   * <p><strong>Emits</strong> {@link
   * org.mh.service.core.exception.NotConnectedException} When not connected to the
   * WebSocket API.
   *
   * <p><strong>Immediately throws</strong> {@link ExchangeSecurityException} if called without
   * authentication details
   *
   * @param currencyPair Currency pair for which to get trades.
   * @return {@link Observable} that emits {@link UserTrade} when exchange sends the update.
   */
  default Observable<UserTrade> getUserTrades(ParsingCurrencyPair currencyPair, Object... args) {
    throw new NotYetImplementedForExchangeException();
  }
}
