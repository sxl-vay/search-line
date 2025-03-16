package top.boking.escore.syn.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.boking.escore.syn.entity.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDao implements PushDao<Comment> {

    private static final Logger log = LoggerFactory.getLogger(CommentDao.class);

    private Connection connection;

    public CommentDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Comment> idCursorQuery(long offset, int limit) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comment where id > ? LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, offset);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getLong("id"));
                    comment.setModule(rs.getString("MODULE"));
                    comment.setTargetId(rs.getLong("TARGET_ID"));
                    comment.setCommentor(rs.getLong("COMMENTOR"));
                    comment.setAddTime(rs.getTimestamp("ADD_TIME"));
                    comment.setBackupContent(rs.getString("BACKUP_CONTENT"));
                    comment.setBlog(rs.getLong("BLOG"));
                    comment.setDayWeibo(rs.getTimestamp("DAY_WEIBO"));
                    comment.setParent(rs.getLong("PARENT"));
                    comment.setTransmitCount(rs.getLong("TRANSMIT_COUNT"));
                    comment.setCommentCount(rs.getLong("COMMENT_COUNT"));
                    comment.setTenantKey(rs.getString("TENANT_KEY"));
                    comment.setPrivy(rs.getString("PRIVY"));
                    comment.setLongitude(rs.getString("LONGITUDE"));
                    comment.setLatitude(rs.getString("LATITUDE"));
                    comment.setAddress(rs.getString("ADDRESS"));
                    comment.setContent(rs.getString("content"));
                    comment.setClient(rs.getString("CLIENT"));
                    comment.setVisittype(rs.getString("VISITTYPE"));
                    comment.setTargetname(rs.getString("targetname"));
                    comment.setSubtargetId(rs.getString("SUBTARGET_ID"));
                    comment.setCommentType(rs.getString("COMMENT_TYPE"));
                    comment.setStepName(rs.getString("STEP_NAME"));
                    comment.setExternal(rs.getString("EXTERNAL"));
                    comment.setRootId(rs.getLong("root_id"));
                    comment.setIsInclude(rs.getInt("is_include"));
                    comment.setFlowType(rs.getInt("flow_type"));
                    comment.setSecondid(rs.getLong("secondid"));
                    comment.setDeleteType(rs.getInt("delete_type"));
                    comment.setUpdateTime(rs.getTimestamp("update_time"));
                    comment.setCreator(rs.getLong("creator"));
                    comment.setCreateTime(rs.getTimestamp("create_time"));
                    comment.setCustomFlag(rs.getString("custom_flag"));

                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            log.error("批量查询评论失败", e);
        }

        return comments;
    }
}