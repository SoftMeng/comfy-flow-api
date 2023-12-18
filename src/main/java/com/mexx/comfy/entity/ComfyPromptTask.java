package com.mexx.comfy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class ComfyPromptTask extends PanacheEntity {
    @Column(columnDefinition = "varchar(20)")
    public String comfyIp;
    @Column(columnDefinition = "varchar(50)")
    public String openid;
    @Column(columnDefinition = "varchar(50)")
    public String flowName;
    @Column(columnDefinition = "text")
    public String prompt;
    @Column(columnDefinition = "varchar(500)")
    public String image;
    @Column(columnDefinition = "varchar(100)")
    public String promptId;
    @Column(columnDefinition = "varchar(20)")
    public String number;
    @Column(columnDefinition = "text")
    public String result;
}