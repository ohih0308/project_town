package ohih.town.constants;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ErrorMessageResourceBundle {
    ResourceBundle MAIL_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.mail-error-messages", Locale.getDefault());
    ResourceBundle USER_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.user-error-messages", Locale.getDefault());
    ResourceBundle COMMON_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.common-error-messages", Locale.getDefault());
    ResourceBundle POST_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.post-error-messages", Locale.getDefault());
    ResourceBundle COMMENT_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.comment-error-messages", Locale.getDefault());
}
