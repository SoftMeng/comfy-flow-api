package com.mexx.comfy.model;

import com.mexx.comfy.constants.RespCodeMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * Description: 通用的接口返回值 .<br>
 * <p>Created Time: 2022/7/28 15:36 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Setter
@Getter
public class Result<T> {
    protected String code;
    protected String message;
    protected T result;

    private Result() {
    }

    public Result(String code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    /**
     * 失败返回.
     *
     * @return .
     */
    public static <T> Result<T> fail(Throwable e) {
        return new Result<T>(RespCodeMsg.FAIL_CODE, e.getMessage(), null);
    }


    /**
     * 默认成功返回.
     *
     * @return .
     */
    public static <T> Result<T> ok() {
        return new Result<T>(RespCodeMsg.SUCCESS_CODE, "", null);
    }

    /**
     * 创建返回对象.
     *
     * @param o
     * @return
     */
    public static <T> Result<T> ok(T o) {
        return new Result<T>(RespCodeMsg.SUCCESS_CODE, "", o);
    }
}
