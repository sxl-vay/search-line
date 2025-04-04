package top.boking.comment.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.jboss.marshalling.SimpleDataInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.boking.base.util.GlobalDataFormatTemplate;
import top.boking.comment.application.dto.CommentEntityDTO;
import top.boking.comment.domain.model.CommentContent;
import top.boking.comment.domain.model.CommentIndex;
import top.boking.comment.domain.model.CommentSubject;
import top.boking.comment.domain.model.UserLike;
import top.boking.comment.domain.service.CommentService;
import top.boking.comment.domain.repository.CommentContentMapper;
import top.boking.comment.domain.repository.CommentIndexMapper;
import top.boking.comment.domain.repository.CommentSubjectMapper;
import top.boking.comment.domain.repository.UserLikeMapper;
import top.boking.user.domain.entity.User;
import top.boking.user.utils.UserContext;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentSubjectMapper subjectMapper;
    private final CommentIndexMapper indexMapper;
    private final CommentContentMapper contentMapper;
    private final UserLikeMapper userLikeMapper;

    public CommentServiceImpl(CommentSubjectMapper subjectMapper, CommentIndexMapper indexMapper, CommentContentMapper contentMapper, UserLikeMapper userLikeMapper) {
        this.subjectMapper = subjectMapper;
        this.indexMapper = indexMapper;
        this.contentMapper = contentMapper;
        this.userLikeMapper = userLikeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishComment(CommentEntityDTO commentEntityDTO) {
        User user = UserContext.getCurrentUser();

        String objId = commentEntityDTO.getObjId();
        Long userId = user.getId();
        String content = commentEntityDTO.getContent();

        // 创建评论索引
        CommentIndex index = new CommentIndex();
        index.setId(IdWorker.getId());
        index.setObjId(objId);
        index.setUserId(userId);
        index.setRootId(0L);
        index.setParentId(0L);
        index.setLikeCount(0);
        indexMapper.insert(index);

        // 创建评论内容
        CommentContent commentContent = new CommentContent();
        commentContent.setId(IdWorker.getId());
        commentContent.setCommentIndexId(index.getId());
        commentContent.setContent(content);
        contentMapper.insert(commentContent);

        // 更新主题统计
        CommentSubject subject = subjectMapper.selectOne(new LambdaQueryWrapper<CommentSubject>().eq(CommentSubject::getObjId, objId));
        if (subject == null) {
            subject = new CommentSubject();
            subject.setId(IdWorker.getId());
            subject.setObjId(objId);
            subject.setUserId(userId);
            subject.setCount(1);
            subject.setRootCount(1);
            subjectMapper.insert(subject);
        } else {
            subject.setCount(subject.getCount() + 1);
            subject.setRootCount(subject.getRootCount() + 1);
            subjectMapper.updateById(subject);
        }

        return index.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long replyComment(CommentEntityDTO commentEntityDTO) {
        User user = UserContext.getCurrentUser();

        String objId = commentEntityDTO.getObjId();
        Long userId = user.getId();
        String content = commentEntityDTO.getContent();
        Long rootId = commentEntityDTO.getRootId();
        Long parentId = commentEntityDTO.getParentId();

        // 创建评论索引
        CommentIndex index = new CommentIndex();
        index.setId(IdWorker.getId());
        index.setObjId(objId);
        index.setUserId(userId);
        index.setRootId(rootId);
        index.setParentId(parentId);
        index.setLikeCount(0);
        indexMapper.insert(index);

        // 创建评论内容
        CommentContent commentContent = new CommentContent();
        commentContent.setId(IdWorker.getId());
        commentContent.setCommentIndexId(index.getId());
        commentContent.setContent(content);
        contentMapper.insert(commentContent);

        // 更新主题统计
        CommentSubject subject = subjectMapper.selectOne(new LambdaQueryWrapper<CommentSubject>().eq(CommentSubject::getObjId, objId));
        if (subject != null) {
            subject.setCount(subject.getCount() + 1);
            subjectMapper.updateById(subject);
        }

        return index.getId();
    }

    @Override
    public List<CommentEntityDTO> getCommentList(String objId, Long parentId, Long rootId, Integer page, Integer size) {
        LambdaQueryWrapper<CommentIndex> lqw = new LambdaQueryWrapper<CommentIndex>().eq(CommentIndex::getObjId, objId).orderByDesc(CommentIndex::getGmtCreate).last(String.format("LIMIT %d, %d", (page - 1) * size, size));
        if (rootId != null) {
            lqw.eq(CommentIndex::getRootId, rootId);
        }
        if (parentId != null) {
            lqw.eq(CommentIndex::getParentId, parentId);
        }
        List<CommentIndex> commentIndices = indexMapper.selectList(lqw);

        List<Long> indexIds = commentIndices.stream().map(CommentIndex::getId).toList();
        if (indexIds.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<CommentContent> inWrapper = new LambdaQueryWrapper<CommentContent>().in(CommentContent::getCommentIndexId, indexIds);
        List<CommentContent> commentContents = contentMapper.selectList(inWrapper);

        Map<Long, CommentContent> commentMap = commentContents.stream().collect(Collectors.toMap(CommentContent::getCommentIndexId, commentContent -> commentContent));

        List<CommentEntityDTO> list = commentIndices.stream().map(commentIndex -> getCommentEntityDTO(commentIndex, commentMap)).toList();

        if (Objects.equals(rootId, 0L)) {
            fullingSubCommentCount(objId, rootId, indexIds, list);
        }

        return list;
    }

    private void fullingSubCommentCount(String objId, Long rootId, List<Long> indexIds, List<CommentEntityDTO> list) {

        List<Map<String, Object>> subCommentIdWithObjAndRootIds = indexMapper.getSubCommentIdWithObjAndRootIds(objId, indexIds);
        Map<Long, Long> rootIdToCountMap = subCommentIdWithObjAndRootIds.stream().collect(Collectors.toMap(k -> (Long) k.get("root_id"), v -> (Long) v.get("sub_comment_count")));
        list.forEach(commentEntityDTO -> {
            Long count = rootIdToCountMap.get(commentEntityDTO.getRootId());
            commentEntityDTO.setSubCommentCount(count == null ? 0 : count.intValue());
        });

    }


    private static CommentEntityDTO getCommentEntityDTO(CommentIndex commentIndex, Map<Long, CommentContent> commentMap) {
        CommentEntityDTO commentEntityDTO = new CommentEntityDTO();
        commentEntityDTO.setId(commentIndex.getId());
        commentEntityDTO.setObjId(commentIndex.getObjId());
        commentEntityDTO.setUserId(commentIndex.getUserId());
        commentEntityDTO.setRootId(commentIndex.getRootId());
        commentEntityDTO.setParentId(commentIndex.getParentId());
        commentEntityDTO.setLikeCount(commentIndex.getLikeCount());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalDataFormatTemplate.DATE_FORMAT);
        commentEntityDTO.setGmtCreate(simpleDateFormat.format(commentIndex.getGmtCreate()));
        commentEntityDTO.setGmtModified(simpleDateFormat.format(commentIndex.getGmtModified()));
        commentEntityDTO.setAuthor(commentIndex.getId() + "");
        CommentContent commentContent = commentMap.get(commentIndex.getId());
        if (commentContent == null) {
            return commentEntityDTO;
        }
        String content = commentContent.getContent();
        commentEntityDTO.setContent(content);
        return commentEntityDTO;
    }

    @Override
    public CommentContent getCommentContent(Long commentId) {
        return contentMapper.selectOne(new LambdaQueryWrapper<CommentContent>().eq(CommentContent::getCommentIndexId, commentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeComment(Long commentId, Long userId) {
        // 检查是否已点赞
        UserLike userLike = userLikeMapper.selectOne(new LambdaQueryWrapper<UserLike>().eq(UserLike::getUserId, userId).eq(UserLike::getCommentId, commentId));
        if (userLike == null) {
            // 创建点赞记录
            userLike = new UserLike();
            userLike.setId(IdWorker.getId());
            userLike.setUserId(userId);
            userLike.setCommentId(commentId);
            userLikeMapper.insert(userLike);

            // 更新评论点赞数
            CommentIndex index = indexMapper.selectById(commentId);
            if (index != null) {
                index.setLikeCount(index.getLikeCount() + 1);
                indexMapper.updateById(index);
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeComment(Long commentId, Long userId) {
        // 检查是否已点赞
        UserLike userLike = userLikeMapper.selectOne(new LambdaQueryWrapper<UserLike>().eq(UserLike::getUserId, userId).eq(UserLike::getCommentId, commentId));
        if (userLike != null) {
            // 删除点赞记录
            userLikeMapper.deleteById(userLike.getId());

            // 更新评论点赞数
            CommentIndex index = indexMapper.selectById(commentId);
            if (index != null) {
                index.setLikeCount(index.getLikeCount() - 1);
                indexMapper.updateById(index);
            }
            return true;
        }
        return false;
    }

    @Override
    public Long countCommentWithObj(String objId) {
        if (objId != null) {
            return indexMapper.selectCount(new LambdaQueryWrapper<CommentIndex>().eq(CommentIndex::getObjId, objId));
        }
        return null;
    }

    @Override
    public Long countCommentWithRoot(String objId, Long rootId) {
        if (objId != null && rootId != null) {
            return indexMapper.selectCount(new LambdaQueryWrapper<CommentIndex>().eq(CommentIndex::getObjId, objId).eq(CommentIndex::getRootId, rootId));
        }
        return null;
    }
}