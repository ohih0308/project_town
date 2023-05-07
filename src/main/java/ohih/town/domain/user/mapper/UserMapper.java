package ohih.town.domain.user.mapper;

import ohih.town.domain.user.dto.GuestbookPermission;
import ohih.town.domain.user.dto.ProfileImage;
import ohih.town.domain.user.dto.RegisterRequest;
import ohih.town.domain.user.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {

    Integer isDuplicated(Map<String, String> map);

    boolean registerUser(RegisterRequest registerRequest);

    boolean initGuestbookConfigs(Long userId);

    UserInfo login(Map<String, String> map);

    boolean uploadProfileImage(ProfileImage profileImage);

    boolean updateProfileImage(ProfileImage profileImage);

    boolean deleteProfileImage(String directory);

    boolean updateUserInfo(Map<String, Object> map);

    boolean updateGuestbookPermission(GuestbookPermission guestbookPermission);

//    boolean updateGuestbookActivation(Map<String, Object> map);

}
