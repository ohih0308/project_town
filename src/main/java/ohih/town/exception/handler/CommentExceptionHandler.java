package ohih.town.exception.handler;

import ohih.town.constants.ResourceBundleConst;
import ohih.town.domain.SimpleResponse;
import ohih.town.domain.comment.controller.CommentRestController;
import ohih.town.exception.InvalidAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static ohih.town.constants.ErrorConst.INVALID_ACCESS_ERROR;

@RestControllerAdvice(assignableTypes = {CommentRestController.class})
public class CommentExceptionHandler {

    @ExceptionHandler(InvalidAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleResponse handleInvalidAccessException() {
        return new SimpleResponse(false, ResourceBundleConst.COMMON_ERROR_MESSAGES.getString(INVALID_ACCESS_ERROR));
    }
}
