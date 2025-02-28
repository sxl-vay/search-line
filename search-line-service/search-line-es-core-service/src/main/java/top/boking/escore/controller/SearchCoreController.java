package top.boking.escore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import top.boking.base.vo.SlineResult;
import top.boking.escore.domain.entity.KnowledgeBase;
import top.boking.escore.domain.request.SearchRequest;
import top.boking.escore.repository.KnowledgeBaseRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchCoreController {
    @Value("${server.port}")
    private String port;

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public SearchCoreController(KnowledgeBaseRepository knowledgeBaseRepository) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
    }

    @GetMapping("/typetest")
    public SlineResult<List<String>> getType() {
        //获取当前服务的ip地址
        String ip = System.getenv("MY_POD_IP");
        return SlineResult.success(Arrays.asList(ip+":"+port,"file","text"));
    }
    @PostMapping("/all")
    public SlineResult<Map<String, Object>> getAll(@RequestBody SearchRequest request) {
        Pageable pageable = PageRequest.of(0, request.getPageSize());
        List<KnowledgeBase> knowledgeBases = knowledgeBaseRepository.findByFileContentContaining(request.getSearchText());
        List<Map<String, Object>> list = knowledgeBases.stream()
                .map(e -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("title", e.getFileName());
                    result.put("content", e.getFileContent());
                    return result;
                })
                .toList();

        //todo 后续实现all接口
        return SlineResult.success(Map.of("postList",list));

    }

}
