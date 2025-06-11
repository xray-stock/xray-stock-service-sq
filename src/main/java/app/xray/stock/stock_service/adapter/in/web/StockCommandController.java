package app.xray.stock.stock_service.adapter.in.web;

import app.xray.stock.stock_service.adapter.in.web.dto.SaveStockRequest;
import app.xray.stock.stock_service.adapter.in.web.dto.SavedStockResponse;
import app.xray.stock.stock_service.application.port.in.SaveStockUseCase;
import app.xray.stock.stock_service.application.port.in.StartCollectingStockUseCase;
import app.xray.stock.stock_service.application.port.in.StopCollectingStockUseCase;
import app.xray.stock.stock_service.application.port.vo.SaveStockCommand;
import app.xray.stock.stock_service.application.port.vo.StartCollectingStockCommand;
import app.xray.stock.stock_service.application.port.vo.StopCollectingStockCommand;
import app.xray.stock.stock_service.domain.Stock;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockCommandController {

    private final SaveStockUseCase saveStockUseCase;
    private final StartCollectingStockUseCase startCollectingStockUseCase;
    private final StopCollectingStockUseCase stopCollectingStockUseCase;

    @PostMapping("/save")
    public ResponseEntity<SavedStockResponse> save(@RequestBody @Valid SaveStockRequest request) {
        SaveStockCommand command = SaveStockCommand.from(request);
        Stock registered = saveStockUseCase.save(command);
        return ResponseEntity.ok(SavedStockResponse.from(registered));
    }

    @PatchMapping("/{stockId}/start")
    public ResponseEntity<HttpStatus> start(@PathVariable("stockId") String stockId) {
        StartCollectingStockCommand command = StartCollectingStockCommand.from(stockId);
        startCollectingStockUseCase.start(command);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{stockId}/end")
    public ResponseEntity<HttpStatus> end(@PathVariable("stockId") String stockId) {
        StopCollectingStockCommand command = StopCollectingStockCommand.from(stockId);
        stopCollectingStockUseCase.stop(command);
        return ResponseEntity.ok().build();
    }

}
