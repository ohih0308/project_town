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


    @Test
    void testCheckValidationAndDuplication_ValidInputNotDuplicated() {
        // Arrange
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9]+)$");
        String field = "username";
        String input = "john123";
        String validMessage = "Valid username";
        String invalidMessage = "Invalid username";
        String duplicatedMessage = "Username already exists";

        // Act
        CheckResult result = userService.checkValidationAndDuplication(pattern, field, input, validMessage, invalidMessage, duplicatedMessage);

        // Assert
        assertTrue(result.isValid());
        assertEquals(1, result.getMessages().size());
        assertEquals(validMessage, result.getMessages().get(field));
    }



}