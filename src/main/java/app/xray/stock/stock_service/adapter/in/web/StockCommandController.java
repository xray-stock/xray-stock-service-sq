package app.xray.stock.stock_service.adapter.in.web;

import app.xray.stock.stock_service.adapter.in.web.dto.SaveStockRequest;
import app.xray.stock.stock_service.adapter.in.web.dto.SavedStockResponse;
import app.xray.stock.stock_service.application.port.in.SaveStockUseCase;
import app.xray.stock.stock_service.application.port.vo.SaveStockCommand;
import app.xray.stock.stock_service.domain.Stock;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockCommandController {

    private final SaveStockUseCase saveStockUseCase;

    @PostMapping("/save")
    public ResponseEntity<SavedStockResponse> save(@RequestBody @Valid SaveStockRequest request) {
        SaveStockCommand command = SaveStockCommand.from(request);
        Stock registered = saveStockUseCase.save(command);
        return ResponseEntity.ok(SavedStockResponse.from(registered));
    }
}
