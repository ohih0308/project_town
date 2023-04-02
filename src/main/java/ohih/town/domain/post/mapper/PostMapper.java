package ohih.town.domain.post.mapper;

import ohih.town.domain.post.dto.Attachment;
import ohih.town.domain.post.dto.PostUpload;
import ohih.town.domain.post.dto.Thumbnail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

    boolean uploadPost(PostUpload postUpload);

    boolean uploadAttachment(Attachment attachment);

    boolean setThumbnail(Thumbnail thumbnail);
}
