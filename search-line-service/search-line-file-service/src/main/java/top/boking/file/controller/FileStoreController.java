package top.boking.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.boking.base.vo.SlineResult;
import top.boking.file.domain.dto.SlineFileDTO;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.domain.respose.SlineFilePageRespose;
import top.boking.file.domain.respose.SlineFileRespose;
import top.boking.file.service.SLineFileService;

import java.io.IOException;
import java.util.List;

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
        //使用Rocketmq将文件信息投递到队列中
        SLineFile sLineFile = sLineFileService.uploadFile(file);
        return SlineResult.success(SlineFileRespose.buildWithSLineFile(sLineFile));
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


    @GetMapping("/list")
    public SlineResult<SlineFilePageRespose> list(@RequestParam("current") Long current, @RequestParam("pageSize") Long pageSize) {

        QueryWrapper<SLineFile> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<SLineFile> lambda = queryWrapper.lambda();
        lambda.orderBy(true, false, SLineFile::getGmtCreate);
        long count = sLineFileService.count();
        Page<SLineFile> page = sLineFileService.page(Page.of(current, pageSize), lambda);
        List<SLineFile> records = page.getRecords();
        List<SlineFileDTO> slineFileDTOS = records.stream().map(SlineFileDTO::buildWithSLineFile).toList();

        return SlineResult.success(new SlineFilePageRespose(slineFileDTOS, count));
    }

}