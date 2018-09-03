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
    private PositionEntityDao positionEntityDao;
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
                    instance.positionEntityDao = daoSession.getPositionEntityDao();
                }
            }
            return  instance;
        }
        return  instance;
    }
    //===========↓↓↓↓↓↓↓地址搜索记录↓↓↓↓↓↓↓↓↓=================
    /**
     添加地址搜索记录数据
     */
    public void addToPositionListfoTable(List<PositionEntity> item){
        for(PositionEntity positionEntity:item){
            addToPositionfoTable(positionEntity);
        }
    }
    /**
     添加地址搜索记录数据
     */
    public long addToPositionfoTable(PositionEntity item){
        long id=positionEntityDao.insert(item);
        return id;
    }

    /**
     * 取出所有地址搜索记录数据
     * @return      所有地址搜索记录数据信息
     */
    public List<PositionEntity> getPositionList() {
        try {
            QueryBuilder<PositionEntity> qb = positionEntityDao.queryBuilder();
            // return loginDao.loadAll();//获取整个表的数据集合,一句代码就搞定！
            qb.orderDesc(PositionEntityDao.Properties.Id);
            return qb.list();
        }catch (Exception e){
            return null;
        }
    }
    /**
     * 根据查询条件,返回地址搜索记录数据列表
     * @param where        条件
     * @param params       参数
     * @return             数据列表
     */
    public List<PositionEntity> queryPosition(String where, String... params){
        return positionEntityDao.queryRaw(where, params);
    }


    /**
     * 根据地址搜索记录,插入或修改信息
     * @param positionEntity  地址搜索记录
     * @return 插入或修改的地址搜索记录id
     */
    public long updatePosition(PositionEntity positionEntity){
        return positionEntityDao.insertOrReplace(positionEntity);
    }


    /**
     * 批量插入或修改地址搜索记录
     * @param list      地址搜索记录列表
     */
    public void updatePositionLists(final List<PositionEntity> list){
        if(list == null || list.isEmpty()){
            return;
        }
        for(int i=0; i<list.size(); i++){
            PositionEntity positionEntity = list.get(i);
            positionEntityDao.insertOrReplace(positionEntity);
        }
//        positionEntityDao.getSession().runInTx(new Runnable() {
//            @Override
//            public void run() {
//                for(int i=0; i<list.size(); i++){
//                    PositionEntity positionEntity = list.get(i);
//                    positionEntityDao.insertOrReplace(positionEntity);
//                }
//            }
//        });
    }

    /**
     * 根据id,删除数据
     * @param id      地址搜索记录id
     */
    public void deletePosition(long id){
        positionEntityDao.deleteByKey(id);
    }

    /**
     * 根据地址搜索记录,删除信息
     * @param positionEntity    地址搜索记录
     */
    public void deletePosition(PositionEntity positionEntity){
        positionEntityDao.delete(positionEntity);
    }

    /**
     删除全部地址搜索记录
     */
    public void clearPosition(){
        positionEntityDao.deleteAll();
    }
    //===========↑↑↑↑↑↑↑↑↑地址搜索记录↑↑↑↑↑↑↑↑↑=================
}
