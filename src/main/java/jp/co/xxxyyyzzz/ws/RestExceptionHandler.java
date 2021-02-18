package jp.co.xxxyyyzzz.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collections;
import java.util.Map;

@SuppressWarnings({"unused"})
@RestControllerAdvice({"jp.co.xxxyyyzzz.ws.search.api"})
public class RestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Map<String, String> handleError404(NoHandlerFoundException e)   {
        return Collections.singletonMap("message", "Resource cannot be found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public Map<String, String> handleError(Exception e) {
        logger.error("予期しない例外発生", e);
        return Collections.singletonMap("message", "Application error has occurred");
    }

}
