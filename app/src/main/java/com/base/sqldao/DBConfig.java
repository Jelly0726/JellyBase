package com.base.sqldao;

import com.jelly.jellybase.BuildConfig;

/**
 * Created by Administrator on 2017/9/7.
 */

public class DBConfig {
    public static final String DBNAME = BuildConfig.APPLICATION_ID.replace(".", "_")+".db";//数据库名称
}
