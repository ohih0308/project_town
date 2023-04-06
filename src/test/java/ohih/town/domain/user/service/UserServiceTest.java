package ohih.town.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.*;
import ohih.town.domain.user.dto.CheckResult;
import ohih.town.domain.user.dto.RegisterRequest;
import ohih.town.domain.user.dto.RegisterRequestResult;
import ohih.town.domain.user.dto.RegisterUser;
import ohih.town.domain.user.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static ohih.town.constants.ErrorMessageResourceBundle.COMMON_ERROR_MESSAGES;
import static ohih.town.constants.ErrorMessageResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.ErrorsConst.*;
import static ohih.town.constants.SuccessConst.USER_CONFIRM_PASSWORD_VALID;
import static ohih.town.constants.SuccessConst.USER_USERNAME_VALID;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
@SpringBootTest
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    private UserService userService;


    @Autowired
    PlatformTransactionManager transactionManager;
    TransactionStatus status;

    @BeforeEach
    void beforeEach() {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        this.userService = new UserService(userMapper);
    }


    @AfterEach
    void afterEach() {
        // 트랜잭션 롤백
        transactionManager.rollback(status);
    }


    @Test
    public void testCheckValidation() {
        // Test case 1: valid input
        Pattern pattern1 = ValidationPatterns.USERNAME;
        CheckResult result1 = userService.checkValidation(pattern1,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                ErrorsConst.USER_USERNAME_INVALID, SuccessConst.USER_USERNAME_VALID,
                "username1");
        assertTrue(result1.getIsValid());
        assertEquals(SUCCESS_MESSAGES.getString(USER_USERNAME_VALID), result1.getMessage());

        // Test case 2: invalid input
        Pattern pattern2 = ValidationPatterns.PASSWORD;
        CheckResult result2 = userService.checkValidation(pattern1,
                USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                ErrorsConst.USER_USERNAME_INVALID, SuccessConst.USER_USERNAME_VALID,
                "invalid username");
        assertFalse(result2.getIsValid());
        assertEquals(USER_ERROR_MESSAGES.getString(USER_USERNAME_INVALID), result2.getMessage());
    }

    @Test
    public void testCheckValidationAndDuplication() {
        // Test case 1: username valid and not duplicated
        {
            when(userMapper.isFiledDuplicated(anyMap())).thenReturn(false);

            String input = "username";

            CheckResult expectedResult = new CheckResult();
            expectedResult.setIsValid(true);
            expectedResult.setIsDuplicated(false);
            expectedResult.setMessage(SUCCESS_MESSAGES.getString(USER_USERNAME_VALID));

            CheckResult actualResult = userService.checkValidationAndDuplication(ValidationPatterns.USERNAME,
                    USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                    USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED,
                    USER_USERNAME_VALID,
                    UserConst.USERNAME, input);

            Assertions.assertEquals(expectedResult.getIsValid(), actualResult.getIsValid());
            Assertions.assertEquals(expectedResult.getIsDuplicated(), actualResult.getIsDuplicated());
            Assertions.assertEquals(expectedResult.getMessage(), actualResult.getMessage());
        }


        // Test case 2: username invalid and not duplicated
        {
            when(userMapper.isFiledDuplicated(anyMap())).thenReturn(false);

            String input = "invalid username";

            CheckResult expectedResult = new CheckResult();
            expectedResult.setIsValid(false);
            expectedResult.setIsDuplicated(false);
            expectedResult.setMessage(USER_ERROR_MESSAGES.getString(USER_USERNAME_INVALID));

            CheckResult actualResult = userService.checkValidationAndDuplication(ValidationPatterns.USERNAME,
                    USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                    USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED,
                    USER_USERNAME_VALID,
                    UserConst.USERNAME, input);

            Assertions.assertEquals(expectedResult.getIsValid(), actualResult.getIsValid());
            Assertions.assertEquals(expectedResult.getIsDuplicated(), actualResult.getIsDuplicated());
            Assertions.assertEquals(expectedResult.getMessage(), actualResult.getMessage());
        }

        // Test case 3: username valid and duplicated
        {
            when(userMapper.isFiledDuplicated(anyMap())).thenReturn(true);

            String input = "username";

            CheckResult expectedResult = new CheckResult();
            expectedResult.setIsValid(true);
            expectedResult.setIsDuplicated(true);
            expectedResult.setMessage(USER_ERROR_MESSAGES.getString(USER_USERNAME_DUPLICATED));

            CheckResult actualResult = userService.checkValidationAndDuplication(ValidationPatterns.USERNAME,
                    USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                    USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED,
                    USER_USERNAME_VALID,
                    UserConst.USERNAME, input);

            Assertions.assertEquals(expectedResult.getIsValid(), actualResult.getIsValid());
            Assertions.assertEquals(expectedResult.getIsDuplicated(), actualResult.getIsDuplicated());
            Assertions.assertEquals(expectedResult.getMessage(), actualResult.getMessage());
        }

        // Test case 4: username invalid and duplicated
        {
            when(userMapper.isFiledDuplicated(anyMap())).thenReturn(true);

            String input = "invalid username";

            CheckResult expectedResult = new CheckResult();
            expectedResult.setIsValid(false);
            expectedResult.setIsDuplicated(true);
            expectedResult.setMessage(USER_ERROR_MESSAGES.getString(USER_USERNAME_INVALID) +
                    "\n" +
                    USER_ERROR_MESSAGES.getString(USER_USERNAME_DUPLICATED));

            CheckResult actualResult = userService.checkValidationAndDuplication(ValidationPatterns.USERNAME,
                    USER_ERROR_MESSAGES, SUCCESS_MESSAGES,
                    USER_USERNAME_INVALID, USER_USERNAME_DUPLICATED,
                    USER_USERNAME_VALID,
                    UserConst.USERNAME, input);

            Assertions.assertEquals(expectedResult.getIsValid(), actualResult.getIsValid());
            Assertions.assertEquals(expectedResult.getIsDuplicated(), actualResult.getIsDuplicated());
            Assertions.assertEquals(expectedResult.getMessage(), actualResult.getMessage());
        }
    }

    @Test
    public void testCheckConfirmPassword() {
        String password = "password1234";
        String confirmPassword = "password1234";

        // Test case 1: password and confirmPassword are equal
        {
            CheckResult result = userService.checkConfirmPassword(password, confirmPassword);
            assertTrue(result.getIsValid());
            assertEquals(SUCCESS_MESSAGES.getString(USER_CONFIRM_PASSWORD_VALID), result.getMessage());
        }

        // Test case 2: password and confirmPassword are not equal
        {
            confirmPassword = "password4321";
            CheckResult result = userService.checkConfirmPassword(password, confirmPassword);
            assertFalse(result.getIsValid());
            assertEquals(USER_ERROR_MESSAGES.getString(USER_CONFIRM_PASSWORD_INVALID), result.getMessage());
        }

        // Test case 3: password is null
        {
            password = null;
            confirmPassword = "password1234";
            CheckResult result = userService.checkConfirmPassword(password, confirmPassword);
            assertFalse(result.getIsValid());
            assertEquals(USER_ERROR_MESSAGES.getString(USER_CONFIRM_PASSWORD_NULL), result.getMessage());
        }

        // Test case 4: confirmPassword is null
        {
            password = "password1234";
            confirmPassword = null;
            CheckResult result = userService.checkConfirmPassword(password, confirmPassword);
            assertFalse(result.getIsValid());
            assertEquals(USER_ERROR_MESSAGES.getString(USER_CONFIRM_PASSWORD_NULL), result.getMessage());
        }
    }

    @Test
    public void testValidateRegisterRequest() {
        String validatedEmail = "test@example.com";
        String authenticatedEmail = "test@example.com";
        String validatedUsername = "username";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setUsername("username");
        registerRequest.setPassword("validPassword1");
        registerRequest.setConfirmPassword("validPassword1");
        registerRequest.setAgreement(true);


        // Test case 1: validatedEmail is null
        {
            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(null,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);

            assertEquals(0, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(COMMON_ERROR_MESSAGES.getString(INVALID_ACCESS_ERROR)));
        }

        // Test case 2: authenticatedEmail is null
        {
            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    null,
                    validatedUsername,
                    registerRequest);

            assertEquals(0, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(COMMON_ERROR_MESSAGES.getString(INVALID_ACCESS_ERROR)));
        }

        // Test case 3: validatedUsername is null
        {
            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    null,
                    registerRequest);

            assertEquals(0, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.USERNAME));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(COMMON_ERROR_MESSAGES.getString(INVALID_ACCESS_ERROR)));
        }

        // Test case 4: validatedEmail != registerRequest.getEmail()
        {
            validatedEmail = "test1@example.com";

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);

            assertEquals(1, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(0).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorFields().get(0).containsValue(registerRequest.getEmail()));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(USER_ERROR_MESSAGES.getString(USER_EMAIL_EMAIL_MISMATCH)));

            validatedEmail = "test@example.com";
        }

        // Test case 5: authenticatedEmail != registerRequest.getEmail()
        {
            authenticatedEmail = "test1@example.com";

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);


            assertEquals(1, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(0).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorFields().get(0).containsValue(registerRequest.getEmail()));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(USER_ERROR_MESSAGES.getString(USER_EMAIL_EMAIL_MISMATCH)));

            authenticatedEmail = "test@example.com";
        }

        // Test case 6: validatedUsername != registerRequest.getUsername()
        {
            validatedUsername = "1username";

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);

            assertEquals(1, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(0).containsKey(UserConst.USERNAME));
            assertTrue(registerRequestResult.getErrorFields().get(0).containsValue(registerRequest.getUsername()));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.USERNAME));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(USER_ERROR_MESSAGES.getString(USER_USERNAME_USERNAME_MISMATCH)));

            validatedUsername = "username";
        }


        // Test case 7: registerRequest.getEmail() is invalid
        {
            when(userMapper.isFiledDuplicated(anyMap())).thenReturn(false);

            registerRequest.setEmail("invalid email");

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);


            assertEquals(2, registerRequestResult.getErrorFields().size());
            assertEquals(2, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(1).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorFields().get(1).containsValue(registerRequest.getEmail()));
            assertTrue(registerRequestResult.getErrorMessages().get(1).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorMessages().get(1).containsValue(USER_ERROR_MESSAGES.getString(USER_EMAIL_INVALID)));

            registerRequest.setEmail("test@example.com");
        }


        // Test case 8: registerRequest.getEmail() is duplicated
        {
            Map<String, String> map = new HashMap<>();
            map.put(UtilityConst.FIELD, UserConst.EMAIL);
            map.put(UtilityConst.VALUE, registerRequest.getEmail());

            when(userMapper.isFiledDuplicated(eq(map))).thenReturn(true);

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);


            assertEquals(1, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(0).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorFields().get(0).containsValue(registerRequest.getEmail()));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.EMAIL));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(USER_ERROR_MESSAGES.getString(USER_EMAIL_DUPLICATED)));
        }

        // Test case 9: registerRequest.getUsername() is invalid
        {
            validatedUsername = "invalid username";
            registerRequest.setUsername("invalid username");
            when(userMapper.isFiledDuplicated(anyMap())).thenReturn(false);

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);


            assertEquals(1, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(0).containsKey(UserConst.USERNAME));
            assertTrue(registerRequestResult.getErrorFields().get(0).containsValue(registerRequest.getUsername()));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.USERNAME));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(USER_ERROR_MESSAGES.getString(USER_USERNAME_INVALID)));

            validatedUsername = "username";
            registerRequest.setUsername("username");
        }

        // Test case 10: registerRequest.getUsername() is duplicated
        {
            Map<String, String> map = new HashMap<>();
            map.put(UtilityConst.FIELD, UserConst.USERNAME);
            map.put(UtilityConst.VALUE, registerRequest.getUsername());

            when(userMapper.isFiledDuplicated(eq(map))).thenReturn(true);

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);


            assertEquals(1, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(0).containsKey(UserConst.USERNAME));
            assertTrue(registerRequestResult.getErrorFields().get(0).containsValue(registerRequest.getUsername()));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.USERNAME));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(USER_ERROR_MESSAGES.getString(USER_USERNAME_DUPLICATED)));
        }

        // Test case 11: registerRequest.getPassword(), registerRequest.getConfirmPassword() is invalid
        {
            registerRequest.setPassword("invalid password");
            registerRequest.setConfirmPassword("invalid password");

            when(userMapper.isFiledDuplicated(anyMap())).thenReturn(false);

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);


            assertEquals(1, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(0).containsKey(UserConst.PASSWORD));
            assertTrue(registerRequestResult.getErrorFields().get(0).containsValue(registerRequest.getPassword()));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.PASSWORD));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(USER_ERROR_MESSAGES.getString(USER_PASSWORD_INVALID)));

            registerRequest.setPassword("validPassword1");
            registerRequest.setConfirmPassword("validPassword1");
        }

        // Test case 12: registerRequest.getAgreement() == false
        {
            registerRequest.setAgreement(false);

            when(userMapper.isFiledDuplicated(anyMap())).thenReturn(false);

            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);


            assertEquals(1, registerRequestResult.getErrorFields().size());
            assertEquals(1, registerRequestResult.getErrorMessages().size());
            assertTrue(registerRequestResult.getErrorFields().get(0).containsKey(UserConst.AGREEMENT));
            assertTrue(registerRequestResult.getErrorFields().get(0).containsValue(null));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsKey(UserConst.AGREEMENT));
            assertTrue(registerRequestResult.getErrorMessages().get(0).containsValue(USER_ERROR_MESSAGES.getString(USER_AGREEMENT_MISSING)));

            registerRequest.setAgreement(true);
        }

        // Test case 13: valid, authenticated, not duplicated
        {
            RegisterRequestResult registerRequestResult = userService.validateRegisterRequest(validatedEmail,
                    authenticatedEmail,
                    validatedUsername,
                    registerRequest);

            assertEquals(0, registerRequestResult.getErrorFields().size());
            assertEquals(0, registerRequestResult.getErrorMessages().size());
        }
    }

    @Test
    public void testRegisterUser() {
        String email = "email";
        String username = "username";
        String password = "password";

        // Test case 1: userMapper.registerUser(registerUser) == false
        {
            Mockito.when(userMapper.registerUser(any())).thenReturn(false);

            Assertions.assertThrows(SQLException.class, () -> {
                userService.registerUser(email, username, password);
            });

            Mockito.when(userMapper.registerUser(any())).thenReturn(true);
        }


        // Test case 1: userMapper.initGuestbookConfig(registerUser.getUserId() == false
        {
            Mockito.when(userMapper.initGuestbookConfig(any())).thenReturn(false);

            Assertions.assertThrows(SQLException.class, () -> {
                userService.registerUser(email, username, password);
            });

//            Mockito.when(userMapper.initGuestbookConfig(any())).thenReturn(true);
        }
    }

}