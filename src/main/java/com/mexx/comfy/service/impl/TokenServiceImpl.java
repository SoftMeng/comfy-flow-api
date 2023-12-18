package com.mexx.comfy.service.impl;

import com.mexx.comfy.constants.Roles;
import com.mexx.comfy.model.Jscode2sessionResp;
import com.mexx.comfy.service.TokenService;
import io.smallrye.jwt.build.Jwt;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Description: Token管理 .<br>
 * <p>Created Time: 2022/8/8 10:04 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@RequestScoped
public class TokenServiceImpl implements TokenService {
    @Inject JsonWebToken jwt;

    @Override
    public String generateToken(Jscode2sessionResp json) {
        return Jwt.issuer("https://www.comfyui.com/issuer")
            .upn(json.getOpenid())
            .preferredUserName("")
            .groups(new HashSet<>(Arrays.asList(Roles.User)))
            .claim("session_key", json.getSession_key())
            .claim("unionid", json.getUnionid())
            .claim("openid", json.getOpenid())
            .expiresIn(Duration.ofDays(365))
            .sign();
    }
}
