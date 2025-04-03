package top.boking.comment.interfaces.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import top.boking.base.vo.SlineResult;
import top.boking.comment.application.dto.CommentEntityDTO;
import top.boking.comment.application.service.CommentApplicationService;
import top.boking.comment.domain.model.CommentContent;
import top.boking.comment.domain.model.CommentIndex;
import top.boking.comment.domain.service.CommentService;
import top.boking.comment.interfaces.dto.request.PushCommentRequest;

import java.util.List;

@Tag(name = "评论接口")
@RestController
@RequestMapping
public class CommentController {
    private final CommentApplicationService commentService;

    public CommentController(CommentApplicationService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "发表评论")
    @PostMapping("/publish")
    public SlineResult<Long> publishComment(@RequestBody PushCommentRequest request) {

        return SlineResult.success(commentService.publishComment(request.toCommentEntityDTO()));
    }

    @Operation(summary = "回复评论")
    @PostMapping("/reply")
    public SlineResult<Long> replyComment(@RequestBody PushCommentRequest request) {
        return SlineResult.success(commentService.replyComment(request.toCommentEntityDTO()));
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/list")
    public SlineResult<List<CommentEntityDTO>> getCommentList(
            @Parameter(description = "评论对象ID") @RequestParam String objId,
            @Parameter(description = "页码") @RequestParam Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam Integer pageSize) {
        return SlineResult.success(commentService.getCommentList(objId, pageNum, pageSize));
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