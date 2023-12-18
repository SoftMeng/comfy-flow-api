package com.mexx.comfy.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Description:  .<br>
 * <p>Created Time: 2023/12/14 10:26 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Setter
@Getter
public class Jscode2sessionResp {
    private String session_key;
    private String unionid;
    private String errmsg;
    private String openid;
    private String errcode;
}
