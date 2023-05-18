package ohih.town.constants;

public interface ErrorsConst {


    String DATABASE_UPDATE_ERROR = "database.update.error";
    String DATABASE_DELETE_ERROR = "database.delete.error";

    String INVALID_ACCESS_ERROR = "invalid.access.error";


    // Mail
    String MAIL_VERIFICATION_SENT_FAILURE = "mail.verification.sent.failure";
    String MAIL_VERIFICATION_EMAIL_MISMATCH = "mail.verification.email.mismatch";
    String MAIL_VERIFICATION_CODE_MISMATCH = "mail.verification.code.mismatch";
    String MAIL_VERIFICATION_REQUEST_NOTFOUND = "mail.verification.request.notfound";
    String MAIL_VERIFICATION_EMAIL_NOT_SENT = "mail.verification.email.not.sent";
    String MAIL_VERIFICATION_CODE_NULL = "mail.verification.code.null";
    String MAIL_VERIFICATION_EMAIL_FAILURE = "mail.verification.email.failure";

    // Email
    String USER_EMAIL_DUPLICATED = "user.email.duplicated";
    String USER_EMAIL_NULL = "user.email.null";
    String USER_EMAIL_INVALID = "user.email.invalid";
    String USER_EMAIL_EMAIL_MISMATCH = "user.email.email_mismatch";
    String USER_EMAIL_VALIDATED_NULL = "user.email.validated.null";

    // Username
    String USER_USERNAME_NULL = "user.username.null";
    String USER_USERNAME_INVALID = "user.username.invalid";
    String USER_USERNAME_DUPLICATED = "user.username.duplicated";
    String USER_USERNAME_USERNAME_MISMATCH = "user.username.username_mismatch";
    String USER_USERNAME_VALIDATED_NULL = "user.username.validated.null";

    // Password
    String USER_PASSWORD_NULL = "user.password.null";
    String USER_PASSWORD_INVALID = "user.password.invalid";
    String USER_PASSWORD_CONFIRMATION_INVALID = "user.password_confirmation.invalid";
    String USER_PASSWORD_CONFIRMATION_NULL = "user.password_confirmation.null";

    // Register
    String USER_REGISTER_FAILURE = "user.register.failure";
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
    String POST_AUTHOR_INVALID = "post.author.invalid";
    String POST_PASSWORD_INVALID = "post.password.invalid";
    String POST_SUBJECT_INVALID = "post.subject.invalid";
    String POST_BODY_INVALID = "post.body.invalid";

    String POST_ACCESS_DENIED = "post.access.denied";

    String POST_UPLOAD_FAILURE = "post.upload.failure";
    String POST_UPDATE_FAILURE = "post.update.failure";
    String POST_DELETE_FAILURE = "post.delete.failure";

    // Comment
    String COMMENT_POST_ID_INVALID = "comment.post_id.invalid";
    String COMMENT_AUTHOR_INVALID = "comment.author.invalid";
    String COMMENT_PASSWORD_INVALID = "comment.password.invalid";
    String COMMENT_COMMENT_INVALID = "comment.comment.invalid";

    String COMMENT_ACCESS_DENIED = "comment.access.denied";

    String COMMENT_UPLOAD_FAILURE = "comment.upload.failure";
    String COMMENT_DELETE_FAILURE = "comment.delete.failure";

}
