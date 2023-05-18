package ohih.town.domain.post.mapper;

import ohih.town.domain.AccessInfo;
import ohih.town.domain.post.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    Long getUserIdByPostId(Long postId);

    AccessInfo getAccessInfo(Long postId);

    List<Attachment> getAttachments(Long postId);

    Integer getAttachmentCount(Long postId);

    Integer getCommentCount(Long postId);

    PostContent getPostContent(Long postId);


    boolean uploadPost(PostUploadRequest postUploadRequest);

    boolean uploadAttachment(Attachment attachment);

    boolean uploadThumbnail(Attachment attachment);


    boolean updateAttachment(Attachment attachment);

    boolean updateThumbnail(Attachment attachment);

    boolean updatePost(PostUploadRequest postUploadRequest);


    Integer deleteAttachments(Long postId);

    boolean deletePost(Long postId);

    Integer deleteComments(Long postId);

    boolean deleteThumbnail(Long postId);
}
