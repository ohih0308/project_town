package ohih.town.domain.guestbook.mapper;

import ohih.town.domain.AccessInfo;
import ohih.town.domain.guestbook.dto.Guestbook;
import ohih.town.domain.guestbook.dto.GuestbookUploadRequest;
import ohih.town.domain.guestbook.dto.GuestbookWriteConfig;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GuestbookMapper {

    Long getUserId(Long postId);

    AccessInfo getPostAccessInfo(Long postId);

    AccessInfo getCommentAccessInfo(Long commentId);

    GuestbookWriteConfig getGuestbookWriteConfig(Long ownerId);

    Long countPosts(Map<String, Object> map);

    List<Guestbook> getAllPosts(Map<String, Object> map);

    List<Guestbook> getPublicPosts(Map<String, Object> map);

    Long countComments(Long postId);

    boolean isPrivatePost(Long postId);

    Long getGuestbookOwner(Long postId);

    List<Guestbook> getComments(Map<String, Object> map);


    boolean uploadPost(GuestbookUploadRequest guestbookUploadRequest);

    boolean uploadComment(GuestbookUploadRequest guestbookUploadRequest);

    boolean deletePost(Long postId);

    boolean deleteComment(Long commentId);
}
