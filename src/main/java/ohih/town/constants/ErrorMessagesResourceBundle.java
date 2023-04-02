package ohih.town.constants;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ErrorMessagesResourceBundle {
    ResourceBundle MAIL_ERROR_MESSAGES = ResourceBundle.getBundle("error-messages.mail-error-messages", Locale.getDefault());
    ResourceBundle USER_ERROR_MESSAGES = ResourceBundle.getBundle("error-messages.user-error-messages", Locale.getDefault());
    ResourceBundle DATABASE_ERROR_MESSAGES = ResourceBundle.getBundle("error-messages.database-error-messages", Locale.getDefault());
    ResourceBundle POST_ERROR_MESSAGES = ResourceBundle.getBundle("error-messages.post-error-messages", Locale.getDefault());
}
