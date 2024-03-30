package com.mexx.comfy.service;

import com.mexx.comfy.model.Jscode2sessionResp;

/**
 * Description: TokenService.java .<br>
 * <p>Created Time: 2023/12/14 10:12 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
public interface TokenService {

    /**
     * 生成Token.
     *
     * @param json json.
     * @return 生成Token.
     */
    String generateToken(Jscode2sessionResp json);
}
