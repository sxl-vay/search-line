package top.boking.fileservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.boking.base.vo.SlineResult;
import top.boking.fileservice.domain.respose.SlineFileRespose;
import top.boking.fileservice.service.SLineFileService;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class FileStoreController {
    @Autowired
    private SLineFileService sLineFileService;

    @PostMapping("/upload")
    public SlineResult<SlineFileRespose> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return SlineResult.error("501", "文件为空");
        }
        return SlineResult.success(SlineFileRespose.buildWithSLineFile(sLineFileService.uploadFile(file)));
    }

    //根据id删除附件的接口
    @DeleteMapping("/delete/{id}")
    public SlineResult<Boolean> deleteById(@PathVariable("id") Long id) {
        boolean b = sLineFileService.removeById(id);
        return SlineResult.success(b);
    }

    @GetMapping("/getById")
    public SlineResult<SlineFileRespose> getById(@RequestParam("id") Long id) {
        return SlineResult.success(SlineFileRespose.buildWithSLineFile(sLineFileService.getById(id)));
    }
}