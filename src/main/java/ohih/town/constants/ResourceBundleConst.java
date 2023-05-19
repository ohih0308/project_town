package ohih.town.constants;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleConst {
    // Success
    ResourceBundle SUCCESS_MESSAGES = ResourceBundle.getBundle("messages.success-messages.success-messages", Locale.getDefault());

    // Error
    ResourceBundle USER_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.user-error-messages", Locale.getDefault());
    ResourceBundle COMMON_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.common-error-messages", Locale.getDefault());
    ResourceBundle POST_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.post-error-messages", Locale.getDefault());
    ResourceBundle COMMENT_ERROR_MESSAGES = ResourceBundle.getBundle("messages.error-messages.comment-error-messages", Locale.getDefault());
    ResourceBundle NOTIFICATION_ERROR_MESSAGES = ResourceBundle.getBundle("message.error-messages.notification-error-messages", Locale.getDefault());

    // Notification
    ResourceBundle NOTIFICATION_MESSAGES = ResourceBundle.getBundle("messages.notification-messages.notification-messages", Locale.getDefault());
}
