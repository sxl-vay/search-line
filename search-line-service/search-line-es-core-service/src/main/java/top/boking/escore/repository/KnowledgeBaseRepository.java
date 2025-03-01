package top.boking.escore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import top.boking.escore.domain.entity.KnowledgeBase;

import java.util.Date;
import java.util.List;

@Repository
public interface KnowledgeBaseRepository extends ElasticsearchRepository<KnowledgeBase, String> {
    // 基于文件名进行模糊查询
    Page<KnowledgeBase> findByFileNameContaining(String fileName, Pageable pageable);

    // 根据 fileContent 进行全文检索
    @Query("""
            {
                "multi_match": {
                  "query": "?0",
                  "fields": ["fileContent", "fileName"]
                }
              }
            """)
    List<KnowledgeBase> constomSearch(String content);

    // 根据文件类型查询
    List<KnowledgeBase> findByFileType(String fileType);

    // 根据作者查询
    List<KnowledgeBase> findByAuthor(String author);

    // 根据上传时间范围查询
    Page<KnowledgeBase> findByUploadTimeBetween(Date startTime, Date endTime, Pageable pageable);

    // 组合查询：根据文件类型和作者
    Page<KnowledgeBase> findByFileTypeAndAuthor(String fileType, String author, Pageable pageable);

    // 组合查询：根据文件名或内容进行模糊搜索
    Page<KnowledgeBase> findByFileNameContainingOrFileContentContaining(
            String fileName, String content, Pageable pageable);
}