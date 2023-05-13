package ohih.town.domain.post.mapper;

import ohih.town.domain.AccessInfo;
import ohih.town.domain.post.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    Long getUserIdByPostId(Long postId);

    AccessInfo getAccessInfo(Long postId);

    boolean uploadPost(PostUploadRequest postUploadRequest);

    boolean uploadAttachment(Attachment attachment);

    boolean uploadThumbnail(Attachment attachment);


    List<Attachment> getAttachmentByPostId(Long postId);


    boolean deleteAttachmentsByFileName(String fileName);


    void deleteThumbnailByPostId(Long postId);


    PostUpdateRequest getPostUpdateInfoByPostId(Long postId);


    PostDetails getPostDetailsByPostId(Long postId);


    Integer updatePost(PostUploadRequest postUploadRequest);

    Integer deletePost(Long postId);


}
