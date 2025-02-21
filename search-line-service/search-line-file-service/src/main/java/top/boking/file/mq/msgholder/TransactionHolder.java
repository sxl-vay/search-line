package top.boking.file.mq.msgholder;

import org.springframework.web.multipart.MultipartFile;

public class TransactionHolder {
    private static ThreadLocal<MultipartFile> multipartFileHolder = new ThreadLocal<>();

    public static MultipartFile getMultipartFileHolder() {
        return multipartFileHolder.get();
    }

    public static void setMultipartFile(MultipartFile multipartFile) {
        multipartFileHolder.set(multipartFile);
    }

    public static void clear() {
        multipartFileHolder.remove();
    }
}
