package ohih.town.domain.user.service;

import lombok.RequiredArgsConstructor;
import ohih.town.domain.user.mapper.UserMapper;
import org.h2.engine.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor
class UserServiceImplTest {

    private UserMapper userMapperMock = Mockito.mock(UserMapper.class);

    private UserService userService = new UserServiceImpl(userMapperMock);


    @Test
    void isDuplicated_Duplicated() {
        Mockito.when(userMapperMock.isDuplicated(Mockito.anyMap())).thenReturn(true);

        Assertions.assertTrue(userService.isDuplicated("username", "username"));

    }
}