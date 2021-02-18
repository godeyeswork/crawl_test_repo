package jp.co.xxxyyyzzz.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@SuppressWarnings({"SameReturnValue", "unused"})
@ControllerAdvice
public class AdminExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(AdminExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class})
    public String handleError404() {
        return "admin/error/404";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public String handleError(Exception e) {
        logger.error("予期しない例外発生", e);
        return "admin/error/500";
    }

}
