package top.boking.escore.service;


import java.util.List;

public interface KnowledgeBaseService {


    /**
     * 根据id删除知识库
     *
     * @param id
     */
    void deleteById(String id);

    //批量根据id删除知识库
    void deleteByIds(List<String> ids);
}
