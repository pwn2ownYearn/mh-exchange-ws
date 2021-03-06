package org.mh.stream.exchange.core;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.market.hedge.core.TradingArea;
import org.market.hedge.service.StreamingParsingCurrencyPair;
import org.mh.service.core.ConnectableService;
import io.netty.channel.ChannelHandlerContext;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.ExchangeSpecification;
import org.mh.service.netty.NettyStreamingService;

public interface StreamingExchange extends Exchange {
  String USE_SANDBOX = "Use_Sandbox";
  String ACCEPT_ALL_CERITICATES = "Accept_All_Ceriticates";
  String ENABLE_LOGGING_HANDLER = "Enable_Logging_Handler";
  String SOCKS_PROXY_HOST = "SOCKS_Proxy_Host";
  String SOCKS_PROXY_PORT = "SOCKS_Proxy_Port";


  default void instance(TradingArea area){
    throw new NotYetImplementedForExchangeException();
  }


  /**
   * 连接到交换机的WebSocket API。
   *
   * @param args Product subscription is used only in certain exchanges where you need to specify
   *     subscriptions during the connect phase.
   * @return {@link Completable} that completes upon successful connection.
   */
  Completable connect(ProductSubscription... args);

  /**
   *
   * 断开与WebSocket API的连接。
   *
   * @return {@link Completable} that completes upon successful disconnect.
   */
  Completable disconnect();

  /**
   * 检查与交换机的连接是否有效。
   *
   * @return true if connection is open, otherwise false.
   */
  boolean isAlive();

  /**
   * 可观察到重新连接失败事件。 发生这种情况时，通常表明服务器或网络已关闭。
   *
   * @return Observable with the exception during reconnection.
   */
  default Observable<Throwable> reconnectFailure() {
    throw new NotYetImplementedForExchangeException();
  }

  /**
   * Observable for connection success event. When this happens, it usually indicates that the
   * server or the network is down.
   *
   * @return Observable with the exception during reconnection.
   */
  default Observable<Object> connectionSuccess() {
    throw new NotYetImplementedForExchangeException();
  }

  /**
   * Observable for disconnection event.
   *
   * @return Observable with the exception during reconnection.
   */
  default Observable<ChannelHandlerContext> disconnectObservable() {
    throw new NotYetImplementedForExchangeException();
  }

  /**
   * Observable for message delay measure. Every time when the client received a message with a
   * timestamp, the delay time is calculated and pushed to subscribers.
   *
   * @return Observable with the message delay measure.
   */
  default Observable<Long> messageDelay() {
    throw new NotYetImplementedForExchangeException();
  }

  /**
   * 重新订阅频道
   * */
  default void resubscribeChannels() {
    throw new NotYetImplementedForExchangeException();
  }

  default Observable<Object> connectionIdle() {
    throw new NotYetImplementedForExchangeException();
  };

  /** Returns service that can be used to access streaming market data. */
  StreamingMarketDataService getStreamingMarketDataService();

  /**获取货币对解析*/
  StreamingParsingCurrencyPair getStreamingParsingCurrencyPair();

  /** Returns service that can be used to access streaming account data. */
  default StreamingAccountService getStreamingAccountService() {
    throw new NotYetImplementedForExchangeException();
  }

  /** Returns service that can be used to access streaming trade data. */
  default StreamingTradeService getStreamingTradeService() {
    throw new NotYetImplementedForExchangeException();
  }

  /**
   * Set whether or not to enable compression handler.
   *
   * @param compressedMessages Defaults to false
   */
  void useCompressedMessages(boolean compressedMessages);

  default void applyStreamingSpecification(
      ExchangeSpecification exchangeSpec, NettyStreamingService<?> streamingService) {
    streamingService.setSocksProxyHost(
        (String) exchangeSpec.getExchangeSpecificParametersItem(SOCKS_PROXY_HOST));
    streamingService.setSocksProxyPort(
        (Integer) exchangeSpec.getExchangeSpecificParametersItem(SOCKS_PROXY_PORT));
    streamingService.setBeforeConnectionHandler(
        (Runnable)
            exchangeSpec.getExchangeSpecificParametersItem(
                ConnectableService.BEFORE_CONNECTION_HANDLER));

    Boolean accept_all_ceriticates =
        (Boolean) exchangeSpec.getExchangeSpecificParametersItem(ACCEPT_ALL_CERITICATES);
    if (accept_all_ceriticates != null && accept_all_ceriticates) {
      streamingService.setAcceptAllCertificates(true);
    }

    Boolean enable_logging_handler =
        (Boolean) exchangeSpec.getExchangeSpecificParametersItem(ENABLE_LOGGING_HANDLER);
    if (enable_logging_handler != null && enable_logging_handler) {
      streamingService.setEnableLoggingHandler(true);
    }
  }
}
