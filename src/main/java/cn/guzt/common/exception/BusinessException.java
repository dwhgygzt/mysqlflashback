package cn.guzt.common.exception;

/**
 * 业务类异常
 *
 * @author guzt
 */
@SuppressWarnings("unused")
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final String FAIL = "-1";

    public static final String FAIL_MSG = "FAIL";

    private String errorCode;

    private String errorMsg;

    private Object errorBody;

    /**
     * 创建BusinessException异常，只设置errorCode.
     *
     * @param errorCode 业务码
     */
    public static void createByErrorCode(String errorCode) {
        throw new BusinessException(errorCode, FAIL_MSG);
    }

    /**
     * 创建BusinessException异常，只设置errorCode 和 errorBody.
     *
     * @param errorCode 业务码
     * @param errorBody 错误进一步的明细
     */
    public static void createByErrorCode(String errorCode, Object errorBody) {
        throw new BusinessException(errorCode, FAIL_MSG, errorBody);
    }

    /**
     * 创建BusinessException异常，只设置errorMsg.
     * errorCode 默认为 -1
     *
     * @param errorMsg 业务码
     */
    public static void createByErrorMsg(String errorMsg) {
        throw new BusinessException(FAIL, errorMsg);
    }

    /**
     * 创建BusinessException异常，只设置errorMsg 和 errorBody.
     * errorCode 默认为 -1
     *
     * @param errorMsg  业务码
     * @param errorBody 错误进一步的明细
     */
    public static void createByErrorMsg(String errorMsg, Object errorBody) {
        throw new BusinessException(FAIL, errorMsg, errorBody);
    }

    /**
     * 创建异常
     *
     * @param errorCode 业务码
     * @param errorMsg  业务验证信息
     */
    public static void create(String errorCode, String errorMsg) {
        throw new BusinessException(errorCode, errorMsg);
    }

    /**
     * 创建异常
     *
     * @param errorCode 业务码
     * @param errorMsg  业务验证信息
     * @param errorBody 错误进一步的明细
     */
    public static void create(String errorCode, String errorMsg, Object errorBody) {
        throw new BusinessException(errorCode, errorMsg, errorBody);
    }

    /**
     * 默认构造，一般用于获取动态方法堆栈.
     */
    public BusinessException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param errorCode 业务码
     * @param errorMsg  业务验证信息
     */
    public BusinessException(String errorCode, String errorMsg) {
        super("errorCode=" + errorCode + ", errorMsg=" + errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 构造函数
     *
     * @param errorCode 业务码
     * @param errorMsg  业务验证信息
     * @param errorBody 错误进一步的明细
     */
    public BusinessException(String errorCode, String errorMsg, Object errorBody) {
        super("errorCode=" + errorCode + ", errorMsg=" + errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.errorBody = errorBody;
    }

    /**
     * 屏蔽异常堆栈,业务类异常暂不需要爬栈.
     *
     * @return Throwable
     */
    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(Object errorBody) {
        this.errorBody = errorBody;
    }
}
