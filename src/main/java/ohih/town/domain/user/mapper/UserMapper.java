package ohih.town.domain.user.mapper;

import ohih.town.domain.user.dto.ProfileImage;
import ohih.town.domain.user.dto.Register;
import ohih.town.domain.user.dto.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Mapper
public interface UserMapper {

    boolean isFiledDuplicated(Map<String, String> map);

    void registerUser(Register register);

    void setLetterConfig(Long userId);


    UserInfo getUserByEmailAndPassword(Map<String, String> map);


    ProfileImage findProfileImageByUserId(Long userId);

    void createProfileImage(ProfileImage profileImage);

    String findProfileImageDirectoryByUserId(Long userId);

    void updateProfileImage(ProfileImage profileImage);

    Integer deleteProfileImage(Long userId);

}
