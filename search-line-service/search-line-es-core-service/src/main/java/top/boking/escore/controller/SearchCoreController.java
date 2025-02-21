package top.boking.escore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.boking.base.vo.SlineResult;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchCoreController {
    @Value("${server.port}")
    private String port;


    @GetMapping("/typetest")
    public SlineResult<List<String>> getType() {
        //获取当前服务的ip地址
        String ip = System.getenv("MY_POD_IP");
        return SlineResult.success(Arrays.asList(ip+":"+port,"file","text"));
    }
    @PostMapping("/all")
    public SlineResult<String> getAll() {
        //todo 后续实现all接口
        return SlineResult.success("test");
    }

}
