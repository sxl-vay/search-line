package top.boking.fileservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.boking.fileservice.domain.entity.SLineFile;
import top.boking.fileservice.mapper.SLineFileMapper;

@Service
public class SLineFileService extends ServiceImpl<SLineFileMapper, SLineFile> {
    public void uploadFile(MultipartFile file) {

    }
}
