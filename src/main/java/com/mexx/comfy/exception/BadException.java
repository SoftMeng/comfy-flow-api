package com.mexx.comfy.exception;

/**
 * Description: 错误&异常 .<br>
 * <p>Created Time: 2022/7/22 15:51 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
public class BadException extends RuntimeException {
    public BadException(String message) {
        super(message);
    }
}
