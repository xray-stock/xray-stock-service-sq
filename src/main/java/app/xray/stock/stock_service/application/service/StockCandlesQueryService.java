package app.xray.stock.stock_service.application.service;

import app.xray.stock.stock_service.adapter.in.web.dto.StockCandlesResponse;
import app.xray.stock.stock_service.application.port.in.QueryStockCandlesUseCase;
import app.xray.stock.stock_service.application.port.out.LoadStockDataPort;
import app.xray.stock.stock_service.application.port.out.LoadTradeTickDataPort;
import app.xray.stock.stock_service.application.port.vo.StockCandleSearchConditionQuery;
import app.xray.stock.stock_service.common.exception.NotFoundException;
import app.xray.stock.stock_service.common.type.CandleIntervalType;
import app.xray.stock.stock_service.domain.Stock;
import app.xray.stock.stock_service.domain.TradeTick;
import app.xray.stock.stock_service.domain.TradeTicksCandleConverter;
import app.xray.stock.stock_service.domain.vo.Candle;
import app.xray.stock.stock_service.domain.vo.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockCandlesQueryService implements QueryStockCandlesUseCase {

    private final LoadStockDataPort loadStockDataPort;
    private final LoadTradeTickDataPort loadTradeTickDataPort;

    @Override
    public StockCandlesResponse queryCandles(StockCandleSearchConditionQuery query) {

        // 주식 기본 정보 조회
        String stockId = query.getStockId();
        CandleIntervalType interval = query.getInterval();
        loadStockDataPort.findOneById(stockId).orElseThrow(() -> NotFoundException.of(Stock.class, stockId))
                .assertEnabled();

        // 주식 차트 정보 조회
        Instant start = query.getStart();
        Instant end = query.getEnd();

        List<TradeTick> tradeTicks = loadTradeTickDataPort.loadTradeTicksDataByRange(stockId, start, end);

        TradeTicksCandleConverter converter = TradeTicksCandleConverter.forConverting(
                TimeRange.of(start, end), interval, tradeTicks);
        converter.aggregate(false);
        List<Candle> candles = converter.getCandles();

        return StockCandlesResponse.of(stockId, interval, candles);
    }
}
