package ohih.town.constants;

public interface URLConst {
    String RETURN_MESSAGE = "return-message";


    // TownController
    String HOME = "/";
    String REGISTER = "/register";
    String BOARD_SELECTION = "/board-selection";
    String BOARD = "/board/";
    String POST_DETAILS = "/post-details";
    String UPLOAD_POST_FORM = "/upload/post";
    String UPDATE_POST_FORM = "/update/post";


    String UPLOAD_POST = "/upload/post";
    String UPDATE_POST = "/update/post";
    String DELETE_POST = "/delete/post";


    String SEND_VERIFICATION_CODE = "/send/verification-code";
    String VERIFY_EMAIL = "/verify/email";
    String VERIFY_USERNAME = "/verify/username";
    String VERIFY_PASSWORD = "/verify/password";
    String VERIFY_PASSWORD_CONFIRMATION = "/verify/password-confirmation";

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


    String ACCESS_PERMISSION_POST = "/access-permission/post/";


    String UPLOAD_COMMENT = "/upload/comment";
    String DELETE_COMMENT = "/delete/comment";
    String ACCESS_PERMISSION_COMMENT = "/access-permission/comment/";
}
