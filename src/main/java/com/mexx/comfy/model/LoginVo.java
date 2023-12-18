package com.mexx.comfy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:  .<br>
 * <p>Created Time: 2023/12/14 10:09 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Setter
@Getter
@AllArgsConstructor
public class LoginVo {
    private String openid;
    private String jwt;
}
