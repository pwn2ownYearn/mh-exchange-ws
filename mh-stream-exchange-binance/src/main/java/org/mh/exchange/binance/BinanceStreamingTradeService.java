package org.mh.exchange.binance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mh.exchange.binance.dto.BaseBinanceWebSocketTransaction;
import org.mh.exchange.binance.dto.ExecutionReportBinanceUserTransaction;
import org.mh.exchange.binance.dto.ExecutionReportBinanceUserTransaction.ExecutionType;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.ExchangeSecurityException;
import org.mh.service.netty.StreamingObjectMapperHelper;
import org.market.hedge.core.ParsingCurrencyPair;
import org.mh.stream.exchange.core.StreamingTradeService;

import java.io.IOException;

public class BinanceStreamingTradeService implements StreamingTradeService {

  private final Subject<ExecutionReportBinanceUserTransaction> executionReportsPublisher =
      PublishSubject.<ExecutionReportBinanceUserTransaction>create().toSerialized();

  private volatile Disposable executionReports;
  private volatile org.mh.exchange.binance.BinanceUserDataStreamingService binanceUserDataStreamingService;

  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  public BinanceStreamingTradeService(
      org.mh.exchange.binance.BinanceUserDataStreamingService binanceUserDataStreamingService) {
    this.binanceUserDataStreamingService = binanceUserDataStreamingService;
  }

  public Observable<ExecutionReportBinanceUserTransaction> getRawExecutionReports() {
    if (binanceUserDataStreamingService == null || !binanceUserDataStreamingService.isSocketOpen())
      throw new ExchangeSecurityException("Not authenticated");
    return executionReportsPublisher;
  }

  public Observable<Order> getOrderChanges() {
    return getRawExecutionReports()
        .filter(r -> !r.getExecutionType().equals(ExecutionType.REJECTED))
        .map(ExecutionReportBinanceUserTransaction::toOrder);
  }

  @Override
  public Observable<Order> getOrderChanges(ParsingCurrencyPair currencyPair, Object... args) {
    return getOrderChanges().filter(oc -> currencyPair.getCurrencyPair().equals(oc.getCurrencyPair()));
  }

  public Observable<UserTrade> getUserTrades() {
    return getRawExecutionReports()
        .filter(r -> r.getExecutionType().equals(ExecutionType.TRADE))
        .map(ExecutionReportBinanceUserTransaction::toUserTrade);
  }

  @Override
  public Observable<UserTrade> getUserTrades(ParsingCurrencyPair currencyPair, Object... args) {
    return getUserTrades().filter(t -> t.getCurrencyPair().equals(currencyPair.getCurrencyPair()));
  }

  /** Registers subsriptions with the streaming service for the given products. */
  public void openSubscriptions() {
    if (binanceUserDataStreamingService != null) {
      executionReports =
          binanceUserDataStreamingService
              .subscribeChannel(
                  BaseBinanceWebSocketTransaction.BinanceWebSocketTypes.EXECUTION_REPORT)
              .map(this::executionReport)
              .subscribe(executionReportsPublisher::onNext);
    }
  }

  /**
   * User data subscriptions may have to persist across multiple socket connections to different
   * URLs and therefore must act in a publisher fashion so that subscribers get an uninterrupted
   * stream.
   */
  void setUserDataStreamingService(
      org.mh.exchange.binance.BinanceUserDataStreamingService binanceUserDataStreamingService) {
    if (executionReports != null && !executionReports.isDisposed()) executionReports.dispose();
    this.binanceUserDataStreamingService = binanceUserDataStreamingService;
    openSubscriptions();
  }

  private ExecutionReportBinanceUserTransaction executionReport(JsonNode json) {
    try {
      return mapper.treeToValue(json, ExecutionReportBinanceUserTransaction.class);
    } catch (IOException e) {
      throw new ExchangeException("Unable to parse execution report", e);
    }
  }
}
