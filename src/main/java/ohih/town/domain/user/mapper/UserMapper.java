package ohih.town.domain.user.mapper;

import ohih.town.domain.user.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {
    // register
    boolean checkDuplication(Map<String, String> map);

    boolean registerUser(RegisterRequest registerRequest);


    boolean initializeGuestBookConfig(Long userId);

    // login
    UserInfo getUserByEmailAndPassword(Map<String, String> map);


    // update user info
    boolean uploadProfileImage(ProfileImage profileImage);

    boolean updateProfileImage(ProfileImage profileImage);

    boolean deleteProfileImage(Long userId);


    boolean updateUsername(Map map);

    boolean updatePassword(Map map);

    boolean deactivate(Long userId);

    boolean updateGuestbookPermission(GuestbookPermission guestbookPermission);

    boolean updateGuestbookActivation(Map map);
}
