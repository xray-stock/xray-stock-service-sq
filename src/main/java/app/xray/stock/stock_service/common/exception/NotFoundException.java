package app.xray.stock.stock_service.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 주어진 리소스를 찾을 수 없을 때 사용하는 예외.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String message) {
        return new NotFoundException(message);
    }

    public static NotFoundException of(String resource, Object identifier) {
        return new NotFoundException(resource + " not found. (identifier = " + identifier + ")");
    }

    public static NotFoundException of(Class<?> clazz, Object identifier) {
        return new NotFoundException(clazz.getSimpleName() + " not found. (identifier = " + identifier + ")");
    }
}
