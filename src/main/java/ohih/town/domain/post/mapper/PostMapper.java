package ohih.town.domain.post.mapper;

import ohih.town.domain.post.dto.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

    boolean uploadPost(PostUploadRequest postUploadRequest);

    boolean uploadAttachment(Attachment attachment);

    boolean setThumbnail(Thumbnail thumbnail);

    PostAccessInfo getPostAccessInfoByPostId(Long postId);

    PostUpdateInfo getPostUpdateInfoByPostId(Long postId);
}
