package com.mexx.comfy.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Description:  .<br>
 * <p>Created Time: 2024/3/30 11:36 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Setter
@Getter
public class ImageVo {
    private Long id;
    private String flowName;
    private String prompt;
    private String name;
    private String viewUrl;
}
