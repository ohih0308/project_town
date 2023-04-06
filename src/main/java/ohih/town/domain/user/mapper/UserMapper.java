package ohih.town.domain.user.mapper;

import ohih.town.domain.user.dto.GuestbookPermission;
import ohih.town.domain.user.dto.ProfileImage;
import ohih.town.domain.user.dto.RegisterUser;
import ohih.town.domain.user.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {
    // register
    boolean isFiledDuplicated(Map<String, String> map);

    boolean registerUser(RegisterUser registerUser);

    boolean initGuestbookConfig(Long userId);


    // login
    UserInfo getUserByEmailAndPassword(Map<String, String> map);


    // update user info
    ProfileImage findProfileImageByUserId(Long userId);

    void uploadProfileImage(ProfileImage profileImage);

    String findProfileImageDirectoryByUserId(Long userId);

    void updateProfileImage(ProfileImage profileImage);

    Integer deleteProfileImage(Long userId);

    boolean updateUsername(Map map);

    boolean updatePassword(Map map);

    boolean deactivate(Long userId);

    boolean updateGuestbookPermission(GuestbookPermission guestbookPermission);

    boolean updateGuestbookActivation(Map map);
}
