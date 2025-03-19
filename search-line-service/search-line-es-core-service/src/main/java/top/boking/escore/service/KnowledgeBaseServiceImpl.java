package top.boking.escore.service;

import org.springframework.stereotype.Service;
import top.boking.escore.repository.KnowledgeBaseRepository;

import java.util.List;

@Service("knowledgeBaseService")
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public KnowledgeBaseServiceImpl(KnowledgeBaseRepository knowledgeBaseRepository) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
    }

    /**
     * 根据id删除知识库
     *
     * @param id
     */
    public void deleteById(String id) {
        knowledgeBaseRepository.deleteById(id);
    }

    //批量根据id删除知识库
    public void deleteByIds(List<String> ids) {
        knowledgeBaseRepository.deleteAllById(ids);
    }
}
