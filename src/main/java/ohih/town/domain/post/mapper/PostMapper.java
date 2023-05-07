package ohih.town.domain.post.mapper;

import ohih.town.domain.post.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    Long getUserIdByPostId(Long postId);


    List<Attachment> getAttachmentByPostId(Long postId);

    boolean uploadAttachment(Attachment attachment);

    boolean deleteAttachmentsByFileName(String fileName);


    boolean setThumbnail(Thumbnail thumbnail);

    void deleteThumbnailByPostId(Long postId);


    PostAccessInfo getPostAccessInfoByPostId(Long postId);

    PostUpdateRequest getPostUpdateInfoByPostId(Long postId);


    PostDetails getPostDetailsByPostId(Long postId);

    boolean uploadPost(PostUploadRequest postUploadRequest);

    Integer updatePost(PostUploadRequest postUploadRequest);

    Integer deletePost(Long postId);


}
