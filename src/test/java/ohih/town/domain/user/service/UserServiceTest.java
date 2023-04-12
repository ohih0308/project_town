package ohih.town.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import ohih.town.domain.user.dto.CheckResult;
import ohih.town.domain.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static ohih.town.constants.ErrorMessageResourceBundle.USER_ERROR_MESSAGES;
import static ohih.town.constants.SuccessMessagesResourceBundle.SUCCESS_MESSAGES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Slf4j
@SpringBootTest
class UserServiceTest {
    @Autowired
    DataSource dataSource;

    @Autowired
    UserService userService;

    UserMapper userMapperMock = Mockito.mock(UserMapper.class);
    UserService userServiceWithMock = new UserService(userMapperMock);

    ResourceBundle userErrorMessageSource = USER_ERROR_MESSAGES;
    ResourceBundle successMessageSource = SUCCESS_MESSAGES;

//    @Test
//        // 입력값이 유효하고 중복되지 않은 경우
//    void testCheckValidationAndDuplication_ValidAndNotDuplicated() {
//        String input = "example";
//        CheckResult result = userService.checkValidationAndDuplication(Pattern.compile("^[a-z]+$"),
//                "field", input,
//                "valid message",
//                "invalid message",
//                "duplicated message");
//        assertTrue(result.isValid());
//        assertFalse(result.isDuplicated());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("valid message", result.getMessages().get(0));
//    }
//
//    @Test
//        // 입력값이 유효하지 않은 경우
//    void testCheckValidationAndDuplication_Invalid() {
//        String input = "123";
//        CheckResult result = userService.checkValidationAndDuplication(Pattern.compile("^[a-z]+$"),
//                "field", input,
//                "valid message",
//                "invalid message",
//                "duplicated message");
//        assertFalse(result.isValid());
//        assertTrue(result.getMessages().contains("invalid message"));
//    }
//
//    @Test
//        // 입력값이 중복된 경우
//    void testCheckValidationAndDuplication_Duplicated() {
//        Mockito.when(userMapperMock.checkDuplication(any())).thenReturn(true);
//        String input = "example";
//        // 가정: "field"에 이미 "example"라는 값이 존재함
//        CheckResult result = userServiceWithMock.checkValidationAndDuplication(Pattern.compile("^[a-z]+$"),
//                "field", input,
//                "valid message",
//                "invalid message",
//                "duplicated message");
//        assertFalse(result.isDuplicated());
//        assertTrue(result.getMessages().contains("duplicated message"));
//    }
//
//    @Test
//        // 입력값이 유효하지 않고 중복된 경우
//    void testCheckValidationAndDuplication_InvalidAndDuplicated() {
//        Mockito.when(userMapperMock.checkDuplication(any())).thenReturn(true);
//        String input = "123";
//        // 가정: "field"에 이미 "example"라는 값이 존재함
//        CheckResult result = userServiceWithMock.checkValidationAndDuplication(Pattern.compile("^[a-z]+$"),
//                "field", input,
//                "valid message",
//                "invalid message",
//                "duplicated message");
//        assertFalse(result.isValid());
//        assertFalse(result.isDuplicated());
//        assertTrue(result.getMessages().contains("invalid message"));
//    }
//
//
//    @Test
//        // 입력값이 유효한 경우
//    void testCheckValidation_Valid() {
//        String input = "example";
//        CheckResult result = userService.checkValidation(Pattern.compile("^[a-z]+$"), input,
//                "valid message", "invalid message");
//        assertTrue(result.isValid());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("valid message", result.getMessages().get(0));
//    }
//
//    @Test
//        // 입력값이 유효하지 않은 경우
//    void testCheckValidation_Invalid() {
//        String input = "123";
//        CheckResult result = userService.checkValidation(Pattern.compile("^[a-z]+$"), input,
//                "valid message", "invalid message");
//        assertFalse(result.isValid());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("invalid message", result.getMessages().get(0));
//    }
//
//
//    @Test
//        // 두 문자열이 같은 경우
//    void testCheckStringEquality_Equal() {
//        String string1 = "example";
//        String string2 = "example";
//        CheckResult result = userService.checkStringEquality(string1, string2,
//                "valid message", "invalid message");
//        assertTrue(result.isValid());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("valid message", result.getMessages().get(0));
//    }
//
//    @Test
//        // 두 문자열이 다른 경우
//    void testCheckStringEquality_NotEqual() {
//        String string1 = "example";
//        String string2 = "not example";
//        CheckResult result = userService.checkStringEquality(string1, string2,
//                "valid message", "invalid message");
//        assertFalse(result.isValid());
//        assertEquals(1, result.getMessages().size());
//        assertEquals("invalid message", result.getMessages().get(0));
//    }


}