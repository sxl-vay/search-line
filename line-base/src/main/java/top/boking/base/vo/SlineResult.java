package top.boking.base.vo;

import lombok.Getter;
import lombok.Setter;
import top.boking.base.response.SingleResponse;

import static top.boking.base.response.ResponseCode.SUCCESS;

/**
 * @author shxl
 */
@Getter
@Setter
public class SlineResult<T> {
    /**
     * 状态码
     */
    private String code;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 消息描述
     */
    private String message;

    /**
     * 服务器节点套接字
     */
    private String server;

    {
        //获取ip
        String ip = System.getenv("MY_POD_IP");
        //获取端口号
        String port = System.getenv("MY_POD_PORT");
        this.server = ip + ":" + port;
    }
    /**
     * 数据，可以是任何类型的VO
     */
    private T data;

    public SlineResult() {
    }

    public SlineResult(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public SlineResult(Boolean success, String code, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public SlineResult(SingleResponse<T> singleResponse) {
        this.success = singleResponse.getSuccess();
        this.data = singleResponse.getData();
        this.code = singleResponse.getResponseCode();
        this.message = singleResponse.getResponseMessage();
    }

    public static <T> SlineResult<T> success(T data) {
        return new SlineResult<>(true, SUCCESS.name(), SUCCESS.name(), data);
    }

    public static <T> SlineResult<T> error(String errorCode, String errorMsg) {
        return new SlineResult<>(false, errorCode, errorMsg, null);
    }
}
