package ohih.town.domain.guestbook.mapper;

import ohih.town.domain.AccessInfo;
import ohih.town.domain.guestbook.dto.PostUploadRequest;
import ohih.town.domain.guestbook.dto.GuestbookWriteConfig;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GuestbookMapper {

    AccessInfo getAccessInfo(Long postId);

    GuestbookWriteConfig getGuestbookWriteConfig(Long ownerId);

    boolean uploadPost(PostUploadRequest postUploadRequest);

    boolean deletePost(Long postId);
}
