package com.base.sqldao;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import systemdb.DaoSession;
import systemdb.PositionEntity;
import systemdb.PositionEntityDao;

/**
 * 地址搜索记录数据
 */
public class PositionDaoUtils {
    private static Context mContext;

    private static PositionDaoUtils instance;
    private PositionEntityDao dao;
    private PositionDaoUtils(){

    }
    public  static PositionDaoUtils getInstance(Context context){
        if(instance ==null) {
            synchronized (PositionDaoUtils.class) {
                if (instance == null) {
                    instance = new PositionDaoUtils();
                    if (mContext == null) {
                        mContext = context.getApplicationContext();
                    }
                    //数据库对象
                    DaoSession daoSession = DBManager.getDBManager().getDaoSession();
                    instance.dao = daoSession.getPositionEntityDao();
                }
            }
            return  instance;
        }
        return  instance;
    }
    //===========↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓=================
    /**
     添加数据
     */
    public void addTable(List<PositionEntity> item){
        for(PositionEntity items:item){
            addTable(items);
        }
    }
    /**
     添加数据
     */
    public long addTable(PositionEntity item){
        long id= dao.insert(item);
        return id;
    }

    /**
     * 取出所有数据
     * @return      所有数据信息
     */
    public List<PositionEntity> getAllList() {
        try {
            QueryBuilder<PositionEntity> qb = dao.queryBuilder();
            // return loginDao.loadAll();//获取整个表的数据集合,一句代码就搞定！
            qb.orderDesc(PositionEntityDao.Properties.Id);
            return qb.list();
        }catch (Exception e){
            return null;
        }
    }
    /**
     * 根据查询条件,返回数据列表
     * @param where        条件
     * @param params       参数
     * @return             数据列表
     */
    public List<PositionEntity> query(String where, String... params){
        return dao.queryRaw(where, params);
    }


    /**
     * 根据地址搜索记录,插入或修改信息
     * @param item
     * @return 插入或修改的id
     */
    public long update(PositionEntity item){
        return dao.insertOrReplace(item);
    }


    /**
     * 批量插入或修改
     * @param list
     */
    public void update(final List<PositionEntity> list){
        if(list == null || list.isEmpty()){
            return;
        }
        for(int i=0; i<list.size(); i++){
            PositionEntity item = list.get(i);
            dao.insertOrReplace(item);
        }
//        dao.getSession().runInTx(new Runnable() {
//            @Override
//            public void run() {
//                for(int i=0; i<list.size(); i++){
//                    PositionEntity positionEntity = list.get(i);
//                    dao.insertOrReplace(positionEntity);
//                }
//            }
//        });
    }

    /**
     * 根据id,删除数据
     * @param id      id
     */
    public void delete(long id){
        dao.deleteByKey(id);
    }

    /**
     * 根据对象,删除信息
     * @param item
     */
    public void delete(PositionEntity item){
        dao.delete(item);
    }

    /**
     删除全部
     */
    public void clear(){
        dao.deleteAll();
    }
    //===========↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑=================
}
