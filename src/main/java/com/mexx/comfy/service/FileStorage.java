package com.mexx.comfy.service;

import java.io.File;

/**
 * Description: 文件存储 .<br>
 * <p>Created Time: 2023/12/18 13:45 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
public interface FileStorage {
    /**
     * 上传文件.
     *
     * @param comfyImageUrl ComfyUI中的图片路径.
     * @param fileName      文件名称.
     * @return 可访问的文件路径.
     */
    String upload(String comfyImageUrl, String fileName);
}
