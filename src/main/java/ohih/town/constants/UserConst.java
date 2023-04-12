package ohih.town.constants;

public interface UserConst {
    Integer USER_TYPE_ADMIN = 0;
    Integer USER_TYPE_MEMBER = 1;
    Integer USER_TYPE_GUEST = 2;

    Integer VERIFICATION_CODE_LENGTH = 5;

    String EMAIL = "email";
    String USERNAME = "username";
    String PASSWORD = "password";
    String CONFIRM_PASSWORD = "confirm_password";
    String AGREEMENT = "agreement";
    String USER_ID = "user_id";

    String ACTIVATION = "activation";

    String REGISTER_REQUEST = "register_request";

    String VALIDATED_EMAIL = "validated_email";
    String VALIDATED_USERNAME = "validated_username";

    String GUESTBOOK_PERMISSION = "guestbook_permission";
    String GUESTBOOK_ACTIVATION = "activation";
}
