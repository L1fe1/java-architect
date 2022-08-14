package com.l1fe1.elasticsearch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "ims_autoparts_car_brand")
public class CarSerialBrand {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer uniacid;
    private Integer parentId;
    private String initials;
    private String name;
    private String pic_url;
    private Integer status;
    private Integer sort;
    private Integer isHot;
    private Long createTime;
}
