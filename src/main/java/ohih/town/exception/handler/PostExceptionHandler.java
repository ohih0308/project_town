package ohih.town.exception.handler;

import ohih.town.constants.ErrorMessageResourceBundle;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.post.controller.PostRestController;
import ohih.town.exception.InvalidAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static ohih.town.constants.ErrorsConst.INVALID_ACCESS_ERROR;

@RestControllerAdvice(assignableTypes = {PostRestController.class})
public class PostExceptionHandler {

    @ExceptionHandler(InvalidAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleResponse handleInvalidAccessException() {
        return new SimpleResponse(false, ErrorMessageResourceBundle.COMMON_ERROR_MESSAGES.getString(INVALID_ACCESS_ERROR));
    }
}
