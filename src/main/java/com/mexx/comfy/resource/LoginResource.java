package com.mexx.comfy.resource;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mexx.comfy.properties.WechatProperties;
import com.mexx.comfy.entity.LoginLog;
import com.mexx.comfy.exception.BadException;
import com.mexx.comfy.model.Jscode2sessionResp;
import com.mexx.comfy.model.LoginVo;
import com.mexx.comfy.model.Result;
import com.mexx.comfy.service.TokenService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 登录 .<br>
 * <p>Created Time: 2023/12/14 10:06 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Path("/login")
public class LoginResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginResource.class);
    private static final String jscode2session = """
        https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code
        """;
    @Inject TokenService tokenService;
    @Inject WechatProperties wechatProperties;

    @POST
    @PermitAll
    @Operation(summary = "微信小程序登录")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Result<LoginVo> login(@QueryParam("js_code") String js_code) {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder()
            .connectTimeout(5000, TimeUnit.SECONDS);
        Response response = null;
        try (Client client = clientBuilder.build()) {
            String url = StrUtil.format(jscode2session, wechatProperties.appid(), wechatProperties.secret(), js_code);
            Invocation.Builder builder = client.target(url)
                .request(MediaType.APPLICATION_JSON);
            response = builder.get();
            String str = response.readEntity(String.class);
            LOGGER.info("Wechat-返回:{}", str);
            Jscode2sessionResp resp = JSONUtil.toBean(str, Jscode2sessionResp.class);
            String jwt = tokenService.generateToken(resp);
            LoginLog loginLog = new LoginLog();
            loginLog.loginTime = new Date();
            loginLog.openid = resp.getOpenid();
            loginLog.unionid = resp.getUnionid();
            loginLog.session_key = resp.getSession_key();
            loginLog.persistAndFlush();
            return Result.ok(new LoginVo(resp.getOpenid(), jwt));
        } catch (Exception e) {
            LOGGER.error("微信登录失败", e);
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
        throw new BadException("微信登录失败");
    }
}
