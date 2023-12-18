package com.mexx.comfy.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.mexx.comfy.properties.FileProperties;
import com.mexx.comfy.service.FileStorage;
import com.mexx.comfy.utils.ClientUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 文件存储 .<br>
 * <p>Created Time: 2023/12/18 13:45 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@ApplicationScoped
public class FileStorageDiskImpl implements FileStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageDiskImpl.class);
    @Inject FileProperties fileProperties;

    @Override
    public String upload(String comfyImageUrl, String fileName) {
        try (Client client = ClientUtils.buildClient()) {
            Invocation.Builder builder = client.target(comfyImageUrl)
                .request();
            Response response = builder.get();
            ClientUtils.check200(response);
            byte[] imageBytes = response.readEntity(byte[].class);
            String targetFileName = StrUtil.format("{}_{}", IdUtil.objectId(), fileName);
            File targetFile = FileUtil.touch(fileProperties.disk() + targetFileName);
            LOGGER.info("存储文件位置:{}", targetFile.getAbsolutePath());
            FileUtil.writeBytes(imageBytes, targetFile);
            return fileProperties.view() + targetFileName;
        } catch (Exception e) {
            LOGGER.error("文件处理失败", e);
        }
        return null;
    }
}
