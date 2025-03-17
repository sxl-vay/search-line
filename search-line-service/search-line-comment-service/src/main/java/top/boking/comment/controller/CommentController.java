package top.boking.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import top.boking.base.vo.SlineResult;
import top.boking.comment.entity.CommentContent;
import top.boking.comment.entity.CommentIndex;
import top.boking.comment.service.CommentService;

import java.util.List;

@Tag(name = "评论接口")
@RestController
@RequestMapping
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "发表评论")
    @PostMapping("/publish")
    public SlineResult<Long> publishComment(
            @Parameter(description = "评论对象ID") @RequestParam String objId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "评论内容") @RequestParam String content) {
        return SlineResult.success(commentService.publishComment(objId, userId, content));
    }

    @Operation(summary = "回复评论")
    @PostMapping("/reply")
    public SlineResult<Long> replyComment(
            @Parameter(description = "评论对象ID") @RequestParam String objId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "评论内容") @RequestParam String content,
            @Parameter(description = "根评论ID") @RequestParam Long rootId,
            @Parameter(description = "父评论ID") @RequestParam Long parentId) {
        return SlineResult.success(commentService.replyComment(objId, userId, content, rootId, parentId));
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/list")
    public SlineResult<List<CommentIndex>> getCommentList(
            @Parameter(description = "评论对象ID") @RequestParam String objId,
            @Parameter(description = "页码") @RequestParam Integer page,
            @Parameter(description = "每页大小") @RequestParam Integer size) {
        return SlineResult.success(commentService.getCommentList(objId, page, size));
    }

    @Operation(summary = "获取评论内容")
    @GetMapping("/content/{commentId}")
    public SlineResult<CommentContent> getCommentContent(
            @Parameter(description = "评论ID") @PathVariable Long commentId) {
        return SlineResult.success(commentService.getCommentContent(commentId));
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/like")
    public SlineResult<Boolean> likeComment(
            @Parameter(description = "评论ID") @RequestParam Long commentId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return SlineResult.success(commentService.likeComment(commentId, userId));
    }

    @Operation(summary = "取消点赞")
    @PostMapping("/unlike")
    public SlineResult<Boolean> unlikeComment(
            @Parameter(description = "评论ID") @RequestParam Long commentId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return SlineResult.success(commentService.unlikeComment(commentId, userId));
    }
}