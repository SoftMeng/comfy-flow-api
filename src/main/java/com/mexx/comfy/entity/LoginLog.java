package com.mexx.comfy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Date;

/**
 * Description: 登录记录 .<br>
 * <p>Created Time: 2023/12/14 18:18 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Entity
public class LoginLog extends PanacheEntity {
    @Column(columnDefinition = "varchar(50)")
    public String openid;
    @Column(columnDefinition = "varchar(50)")
    public String unionid;
    @Column(columnDefinition = "varchar(500)")
    public String session_key;
    public Date loginTime;
}
