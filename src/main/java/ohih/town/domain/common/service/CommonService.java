package ohih.town.domain.common.service;

import ohih.town.ValidationResult;
import ohih.town.constants.PostConst;
import ohih.town.constants.UserConst;
import ohih.town.constants.ValidationPatterns;
import ohih.town.domain.common.dto.ActionResult;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.PostContentInfo;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.utilities.Utilities;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

import static ohih.town.constants.ErrorMessagesResourceBundle.POST_ERROR_MESSAGES;
import static ohih.town.constants.ErrorMessagesResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.ErrorsConst.POST_BODY_INVALID;
import static ohih.town.constants.SuccessConst.*;
import static ohih.town.constants.SuccessConst.POST_BODY_VALID;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;

@Service
public class CommonService {

    public void setAuthor(AuthorInfo authorInfo, UserInfo userInfo, String ip) {
        authorInfo.setIp(ip);
        if (userInfo == null) {
            authorInfo.setUserType(UserConst.USER_TYPE_GUEST);
        } else {
            authorInfo.setUserId(userInfo.getUserId());
            authorInfo.setUserType(userInfo.getUserType());
            authorInfo.setAuthor(userInfo.getUsername());
            authorInfo.setPassword("");
        }
    }

    public ValidationResult checkValidation(Pattern pattern,
                                            ResourceBundle errorMessageSource, ResourceBundle successMessageSource,
                                            String invalidMessage,
                                            String validMessage,
                                            String field,
                                            String input) {
        ValidationResult validationResult = new ValidationResult();
        boolean isValid = Utilities.isValidPattern(pattern, input);

        Map<String, String> message = new HashMap<>();

        Map<String, Boolean> fieldValidation = new HashMap<>();

        if (isValid) {
            message.put(field, successMessageSource.getString(validMessage));
            validationResult.setIsValid(true);
            fieldValidation.put(field, true);
        } else {
            message.put(field, errorMessageSource.getString(invalidMessage));
            validationResult.setIsValid(false);
            fieldValidation.put(field, false);
        }

        validationResult.setFieldValidation(fieldValidation);
        validationResult.setMessage(message);

        return validationResult;
    }
}
