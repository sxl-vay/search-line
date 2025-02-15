package top.boking.fileservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.boking.base.vo.SlineResult;
import top.boking.fileservice.domain.entity.SLineFile;
import top.boking.fileservice.service.SLineFileService;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileUploadController {
    @Autowired
    private SLineFileService service;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return "文件为空";
        }
        service.uploadFile(file);
        return "文件上传成功";
    }

    @GetMapping("/getById")
    public SlineResult<SLineFile> getById(@RequestParam("id") Long id) {
        return SlineResult.success(service.getById(id));
    }

}