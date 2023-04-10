package ohih.town.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import ohih.town.domain.user.mapper.UserMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

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

}