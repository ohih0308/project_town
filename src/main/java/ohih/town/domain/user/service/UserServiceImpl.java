package ohih.town.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.UserConst;
import ohih.town.constants.UtilityConst;
import ohih.town.domain.user.dto.*;
import ohih.town.domain.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;


    @Override
    public boolean isDuplicated(String field, String value) {
        Map map = new HashMap();
        map.put(UtilityConst.FIELD, field);
        map.put(UtilityConst.VALUE, value);

        return userMapper.isDuplicated(map);
    }

    @Override
    public boolean confirmPassword(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        } else return password.equals(confirmPassword);
    }

    @Override
    public boolean hasNull(RegisterRequest registerRequest) {
        if (registerRequest.getEmail() == null ||
                registerRequest.getUsername() == null ||
                registerRequest.getPassword() == null ||
                registerRequest.getConfirmPassword() == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public RegisterResult checkRegisterRequest() {
        return null;
    }

    @Override
    public RegisterResult registerUser(RegisterRequest registerRequest) {
        return null;
    }

    @Override
    public LoginResult login(String email, String password) {
        return null;
    }

    @Override
    public ProfileImage extractProfileImageFromRequest(MultipartFile multipartFile, Long userId) {
        return null;
    }

    @Override
    public ProfileImageActionResult uploadProfileImage(MultipartFile multipartFile, Long userId) {
        return null;
    }

    @Override
    public ProfileImageActionResult updateProfileImage(MultipartFile multipartFile, Long userId) {
        return null;
    }

    @Override
    public ProfileImageActionResult deleteProfileImage(String directory) {
        return null;
    }

    @Override
    public UserInfoUpdateResult updateUsername(Long userId, String username) {
        return null;
    }

    @Override
    public UserInfoUpdateResult updatePassword(Long userId, String password) {
        return null;
    }

    @Override
    public UserInfoUpdateResult updateGuestbookPermission(Long userId, GuestbookPermission guestbookPermission) {
        return null;
    }

    @Override
    public UserInfoUpdateResult updateGuestbookActivation(Long userId, boolean activation) {
        return null;
    }
}
