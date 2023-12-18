package com.mexx.comfy.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Description:  .<br>
 * <p>Created Time: 2023/12/14 11:50 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Setter
@Getter
public class ComfyPromptResp {
    private String prompt_id;
    private String number;
    private Object node_errors;
}
