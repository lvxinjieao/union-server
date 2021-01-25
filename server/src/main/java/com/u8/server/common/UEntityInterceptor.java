package com.u8.server.common;

import com.u8.server.utils.TimeUtils;
import org.hibernate.EmptyInterceptor;

import java.util.Date;

/**
 * Hibernate数据表拦截器
 * 数据库存储过程，每天晚上11点50分，会将第二天和第三条的日志表建好,这里自动适配表名
 *
 * Created by ant on 2016/8/16.
 */

public class UEntityInterceptor extends EmptyInterceptor{

    public UEntityInterceptor() {}

    public String onPrepareStatement(String sql) {

        if(sql.toLowerCase().contains("uuserlog")){

            String tableName = "uuserlog"+ TimeUtils.format_yyyyMMdd(new Date());
            sql = sql.replaceAll("UUserLog", tableName);

        }

        return sql;

    }

}
