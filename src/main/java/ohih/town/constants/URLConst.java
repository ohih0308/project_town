package ohih.town.constants;

public interface URLConst {
    String RETURN_MESSAGE = "return-message";

    String HOME = "/";
    String REGISTER_URL = "/register";

    String SEND_VERIFICATION_CODE = "/send/verification-code";
    String VERIFY_EMAIL_CODE = "/verify/email-code";

    String CHECK_USERNAME = "/check/username";

    String CHECK_PASSWORD = "/check/password";

    String CHECK_CONFIRM_PASSWORD = "/check/confirm-password";

    String LOGIN = "/login";
    String LOGOUT = "/logout";

    String UPLOAD_PROFILE_IMAGE = "/upload/profile-image";
    String UPDATE_PROFILE_IMAGE = "/update/profile-image";
    String DELETE_PROFILE_IMAGE = "/delete/profile-image";
    String UPDATE_USERNAME = "/update/username";
    String UPDATE_PASSWORD = "/update/password";
    String DEACTIVATE = "/deactivate";

    String UPDATE_GUESTBOOK_PERMISSION = "/update/guestbook-permission";
    String UPDATE_GUESTBOOK_ACTIVATION = "/update/guestbook-activation";

    String FORUM_SELECTION = "/forum-selection";
    String GET_BOARD_PAGE = "/board/{boardName}";

    String UPLOAD_POST = "/upload/post";
    String POST_DETAILS = "/post/{postId}";
    String UPDATE_POST_FORM = "/update/post-form";
    String UPDATE_POST = "/update/post";
    String DELETE_POST_FORM = "/delete/post-form";
    String DELETE_POST = "/delete/post";
    String CHECK_POST_PERMISSION = "/check/post/permission";


    String UPLOAD_COMMENT = "/upload/comment";
    String DELETE_COMMENT = "/delete/comment";
    String CHECK_COMMENT_PERMISSION = "/check/comment/permission";
}
