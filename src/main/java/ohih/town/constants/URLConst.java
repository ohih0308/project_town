package ohih.town.constants;

public interface URLConst {
    String RETURN_MESSAGE = "return-message";


    // BoardController
    String VERIFY_CATEGORY = "/verify-category";
    String VERIFY_BOARD = "/verify-board";
    String CREATE_CATEGORY = "/create-category";
    String CREATE_BOARD = "/create-board";
    String RENAME_CATEGORY = "/rename-category";
    String RENAME_BOARD = "/rename-board";


    // TownController
    String HOME = "/";
    String REGISTER = "/register";
    String BOARD_SELECTION = "/board-selection";
    String BOARD = "/board/";
    String POST_DETAILS = "/post-details/{postId}";
    String UPLOAD_POST_FORM = "/upload/post";
    String UPDATE_POST_FORM = "/update/post";

    String MY_PAGE = "/my-page";
    String MY_POSTS = "/my-page/posts";
    String MY_COMMENTS = "/my-page/comments";


    // UserRestController
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

    String DEACTIVATE_ACCOUNT = "/deactivate-account";

    String UPDATE_GUESTBOOK_PERMISSION = "/update/guestbook-permission";
    String UPDATE_GUESTBOOK_ACTIVATION = "/update/guestbook-activation";


    // PostRestController
    String UPLOAD_POST = "/upload/post";
    String ACCESS_PERMISSION_POST = "/access-permission/post/";
    String UPDATE_POST = "/update/post";
    String DELETE_POST = "/delete/post";
    String APPRAISE_POST = "/appraise/post";


    // CommentRestController
    String UPLOAD_COMMENT = "/upload/comment";
    String ACCESS_PERMISSION_COMMENT = "/access-permission/comment/";
    String DELETE_COMMENT = "/delete/comment";
    String GET_COMMENTS = "/{postId}/comments";


    // NotificationRestController
    String READ_NOTIFICATIONS = "/read-notifications";
    String DELETE_NOTIFICATION = "/delete-notification";
    String DELETE_NOTIFICATIONS = "/delete-notifications";


    // GuestbookRestController
    String GUESTBOOK_POSTS = "/guestbook/posts";
    String GUESTBOOK_COMMENTS = "/guestbook/comments";
    String GUESTBOOK_UPLOAD_POST = "/guestbook/upload/post";
    String GUESTBOOK_ACCESS_PERMISSION_POST = "/guestbook/access-permission/post/";
    String GUESTBOOK_ACCESS_PERMISSION_COMMENT = "/guestbook/access-permission/comment/";
    String GUESTBOOK_DELETE_POST = "/guestbook/delete/post";

    String GUESTBOOK_UPLOAD_COMMENT = "/guestbook/upload/comment";
    String GUESTBOOK_DELETE_COMMENT = "/guestbook/delete/comment";

}
