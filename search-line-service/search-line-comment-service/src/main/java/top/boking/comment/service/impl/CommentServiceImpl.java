package top.boking.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.boking.comment.entity.CommentContent;
import top.boking.comment.entity.CommentIndex;
import top.boking.comment.entity.CommentSubject;
import top.boking.comment.entity.UserLike;
import top.boking.comment.mapper.CommentContentMapper;
import top.boking.comment.mapper.CommentIndexMapper;
import top.boking.comment.mapper.CommentSubjectMapper;
import top.boking.comment.mapper.UserLikeMapper;
import top.boking.comment.service.CommentService;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentSubjectMapper subjectMapper;
    private final CommentIndexMapper indexMapper;
    private final CommentContentMapper contentMapper;
    private final UserLikeMapper userLikeMapper;

    public CommentServiceImpl(CommentSubjectMapper subjectMapper,
                              CommentIndexMapper indexMapper,
                              CommentContentMapper contentMapper,
                              UserLikeMapper userLikeMapper) {
        this.subjectMapper = subjectMapper;
        this.indexMapper = indexMapper;
        this.contentMapper = contentMapper;
        this.userLikeMapper = userLikeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishComment(String objId, Long userId, String content) {
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
        CommentSubject subject = subjectMapper.selectOne(
                new LambdaQueryWrapper<CommentSubject>().eq(CommentSubject::getObjId, objId));
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
    public Long replyComment(String objId, Long userId, String content, Long rootId, Long parentId) {
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
        CommentSubject subject = subjectMapper.selectOne(
                new LambdaQueryWrapper<CommentSubject>().eq(CommentSubject::getObjId, objId));
        if (subject != null) {
            subject.setCount(subject.getCount() + 1);
            subjectMapper.updateById(subject);
        }

        return index.getId();
    }

    @Override
    public List<CommentIndex> getCommentList(String objId, Integer page, Integer size) {
        return indexMapper.selectList(
                new LambdaQueryWrapper<CommentIndex>()
                        .eq(CommentIndex::getObjId, objId)
                        .orderByDesc(CommentIndex::getGmtCreate)
                        .last(String.format("LIMIT %d, %d", (page - 1) * size, size)));
    }

    @Override
    public CommentContent getCommentContent(Long commentId) {
        return contentMapper.selectOne(
                new LambdaQueryWrapper<CommentContent>()
                        .eq(CommentContent::getCommentIndexId, commentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeComment(Long commentId, Long userId) {
        // 检查是否已点赞
        UserLike userLike = userLikeMapper.selectOne(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId)
                        .eq(UserLike::getCommentId, commentId));
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
        UserLike userLike = userLikeMapper.selectOne(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId)
                        .eq(UserLike::getCommentId, commentId));
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
}