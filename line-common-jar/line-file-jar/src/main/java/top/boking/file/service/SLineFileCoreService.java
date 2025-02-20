package top.boking.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.mapper.SLineFileMapper;

/**
 * 文件核心服务
 */
@Service
public class SLineFileCoreService extends ServiceImpl<SLineFileMapper, SLineFile> {
    /**
     * 检查指定文件ID的文件是否存在
     * 该方法通过调用getById方法来获取文件，如果getById返回null，则表示文件不存在，反之则存在
     *
     * @param fileId 文件ID，用于唯一标识一个文件
     * @return boolean 表示文件是否存在的布尔值，存在返回true，否则返回false
     */
    public boolean checkFileExist(Long fileId) {
        return getById(fileId) != null;
    }
}
