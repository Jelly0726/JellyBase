package com.base.sqldao;

import android.content.Context;

import java.io.ObjectStreamException;

public class DBManager {
    /**
     * 但是jdk 1.5 以后java 编译器允许乱序执行 。所以执行顺序可能是1-3-2 或者 1-2-3.如果是前者先执行3 的话
     * 切换到其他线程，instance 此时 已经是非空了，此线程就会直接取走instance ，直接使用，这样就回出错。DCL 失效。
     * 解决方法 SUN 官方已经给我们了。将instance 定义成 private volatile static Singleton instance =null: 即可
     */
    private DBManager() {}
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final DBManager instance = new DBManager();
    }
    /**
     * 单一实例
     */
    public static DBManager getDBManager() {
        return SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException{
        return SingletonHolder.instance;
   }
    private static systemdb.DaoMaster daoMaster;
    private static systemdb.DaoSession daoSession;
    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public systemdb.DaoMaster getDaoMaster(Context context)
    {
        if (daoMaster == null)
        {
//            //获取数据库路径
//            String path=context.getDatabasePath("NuoMember").getPath();
//            File file=new File(path);
//            //判断数据库文件是否存在
//            if(file.exists()){//存在就清除
//                //清除应用所有数据库
//                AppUtils.cleanDatabases(context);
//            }

            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, DBConfig.DBNAME,
                    null);
            daoMaster = new systemdb.DaoMaster(helper.getWritableDatabase());
            //DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,Config.DBNAME, null);
            daoMaster = new systemdb.DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }
    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public systemdb.DaoSession getDaoSession(Context context)
    {
        if (daoSession == null)
        {
            if (daoMaster == null)
            {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
