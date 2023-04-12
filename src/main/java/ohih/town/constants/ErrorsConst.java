package ohih.town.constants;

public interface ErrorsConst {
    String DATABASE_UPDATE_ERROR = "database.update.error";
    String DATABASE_DELETE_ERROR = "database.delete.error";

    String INVALID_ACCESS_ERROR = "invalid.access.error";


    // Email
    String MAIL_SEND_ERROR = "mail.send.error";
    String USER_EMAIL_DUPLICATED = "user.email.duplicated";
    String EMAIL_VERIFICATION_FAILURE = "email.verification.failure";
    String USER_EMAIL_NULL = "user.email.null";
    String USER_EMAIL_INVALID = "user.email.invalid";
    String USER_EMAIL_EMAIL_MISMATCH = "user.email.email_mismatch";
    String USER_EMAIL_VALIDATED_NULL = "user.email.validated.null";
    String USER_EMAIL_AUTHENTICATED_NULL = "user.email.authenticated.null";

    // Username
    String USER_USERNAME_NULL = "user.username.null";
    String USER_USERNAME_INVALID = "user.username.invalid";
    String USER_USERNAME_DUPLICATED = "user.username.duplicated";
    String USER_USERNAME_USERNAME_MISMATCH = "user.username.username_mismatch";
    String USER_USERNAME_VALIDATED_NULL = "user.username.validated.null";

    // Password
    String USER_PASSWORD_NULL = "user.password.null";
    String USER_PASSWORD_INVALID = "user.password.invalid";
    String USER_CONFIRM_PASSWORD_INVALID = "user.confirm_password.invalid";
    String USER_CONFIRM_PASSWORD_NULL = "user.confirm_password.null";

    // Register
    String USER_REGISTER_FAILED = "user.register.failed";
    String USER_AGREEMENT_MISSING = "user.agreement.missing";
    String USER_REGISTER_REQUEST_NULL = "user.register.request.null";
    String USER_REGISTER_SQL_EXCEPTION = "user.register.sql.exception";
    String USER_LOGIN_FAILURE_INVALID_CREDENTIALS = "user.login.failure.invalid_credentials";

    // Profile image
    String USER_PROFILE_IMAGE_UPLOAD_FAILURE = "user.profile.image.upload.failure";
    String USER_PROFILE_IMAGE_UPDATE_FAILURE = "user.profile.image.update.failure";
    String USER_PROFILE_IMAGE_DELETE_FAILURE = "user.profile.image.delete.failure";

    // Etc
    String USER_UPDATE_USERNAME_FAILURE = "user.update.username.failure";
    String USER_UPDATE_PASSWORD_FAILURE = "user.update.password.failure";
    String USER_DEACTIVATE_ACCOUNT_FAILURE = "user.deactivate.account.failure";
    String USER_GUESTBOOK_PERMISSION_UPDATE_FAILURE = "user.guestbook.permission.update.failure";
    String USER_GUESTBOOK_ACTIVATION_UPDATE_FAILURE = "user.guestbook.activation.update.failure";


    // Post
    String POST_SUBJECT_INVALID = "post.subject.invalid";
    String POST_BODY_INVALID = "post.body.invalid";


    String POST_UPLOAD_IO_EXCEPTION = "post.upload.io.exception";
    String POST_UPLOAD_SQL_EXCEPTION = "post.update.sql.exception";
    String POST_UPDATE_IO_EXCEPTION = "post.update.io.exception";
    String POST_UPDATE_SQL_EXCEPTION = "post.upload.sql.exception";
    String POST_DELETE_PARTIAL_EXCEPTION = "post.delete.partial.exception";
    String UPLOAD_ATTACHMENT_EXTENSION_ERROR = "upload.attachment.extension.error";
    String UPLOAD_ATTACHMENT_SIZE_ERROR = "upload.attachment.size.error";
    String POST_PERMISSION_ERROR = "post.permission.error";
    String POST_EXISTENCE_ERROR = "post.existence.error";
    String POST_DELETE_ERROR = "post.delete.error";

    String COMMENT_INVALID = "comment.invalid";
    String COMMENT_UPLOAD_SQL_EXCEPTION = "comment.upload.sql.exception";
    String COMMENT_PERMISSION_ERROR = "comment.permission.error";
    String COMMENT_EXISTENCE_ERROR = "comment.existence.error";
    String COMMENT_DELETE_SQL_EXCEPTION = "comment.delete.sql.exception";
}
