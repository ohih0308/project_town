package ohih.town.domain.user.service;

import lombok.RequiredArgsConstructor;
import ohih.town.domain.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor
class UserServiceImplTest {

    private UserMapper userMapperMock = Mockito.mock(UserMapper.class);

    @Test
    void name() {

    }
}