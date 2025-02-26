package top.boking.comment.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.boking.comment.domain.CommentContext;
import top.boking.comment.domain.entity.Comment;
import top.boking.comment.domain.request.PageParam;
import top.boking.comment.domain.vo.CommentTreeVO;
import top.boking.comment.domain.vo.CommentVO;
import top.boking.comment.handle.ReplyCommentHandler;
import top.boking.comment.handle.RootCommentHandler;
import top.boking.comment.handle.SubCommentHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private RootCommentHandler rootHandler;
    @Autowired
    private SubCommentHandler subHandler;
    @Autowired
    private ReplyCommentHandler replyHandler;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 初始化责任链
    @PostConstruct
    public void init() {
        rootHandler.setNext(subHandler);
        subHandler.setNext(replyHandler);
    }

    public CommentTreeVO loadComments(Long postId, PageParam pageParam) {
        // 1. 构建上下文
        CommentContext context = new CommentContext();
        context.setPostId(postId);
        context.setPageParam(pageParam);

        // 2. 触发责任链
        rootHandler.handle(context);

        // 3. 构建评论树
        return buildCommentTree(context);
    }

    private CommentTreeVO buildCommentTree(CommentContext context) {
        List<CommentVO> rootVOs = new ArrayList<>();
        
        // 1. 构建主评论节点
        for (Comment root : context.getRootComments()) {
            CommentVO rootVO = convert(root);
            
            // 2. 添加子评论
            List<Comment> subs = context.getSubComments().get(root.getId());
            if (subs != null) {
                List<CommentVO> subVOs = new ArrayList<>();
                for (Comment sub : subs) {
                    CommentVO subVO = convert(sub);
                    // 3. 添加回复
                    List<Comment> replies = context.getReplyComments().get(sub.getId());
                    if (replies != null) {
                        subVO.setReplies(convertList(replies));
                    }
                    subVOs.add(subVO);
                }
                rootVO.setSubComments(subVOs);
            }
            rootVOs.add(rootVO);
        }

        return new CommentTreeVO(rootVOs, context.getPageParam());
    }

    private CommentVO convert(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setUserId(comment.getUserId());
        vo.setCreatedAt(comment.getCreatedAt());
        vo.setParentId(comment.getParentId());
        return vo;
    }

    private List<CommentVO> convertList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        return comments.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}