package com.base.sqldao;

import android.content.Context;

import com.base.Utils.StringUtil;
import com.base.applicationUtil.AppUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
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
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    public List<PositionEntity> query(int offset, int size){
        List<PositionEntity> listMsg = dao.queryBuilder()
                .offset(offset * size).limit(size).list();
        return listMsg;
    }
    /**
     * 模糊查询
     * @param key
     * @return
     */
    public List<PositionEntity> query(String key){
        QueryBuilder<PositionEntity> qb = dao.queryBuilder();
        List<PositionEntity> list=new ArrayList<>();
        //判断输入的是中文还是英文
        if(StringUtil.isEnglish(key)){
            for (PositionEntity item : getAllList()) {
                String username = item.getAdCode();
                String firstUsername = StringUtil.getSpells(username);
                if(firstUsername.contains(key)){
                    list.add(item);
                }
            }
        }else {
            list.addAll(qb.whereOr(PositionEntityDao.Properties.AdCode.like("%" + key + "%")
                    ,PositionEntityDao.Properties.Address.like("%" + key + "%")).distinct().list());
        }
        return list;
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
        dao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                List<PositionEntity> lists=getAllList();
                for(int i=0; i<list.size(); i++){
                    PositionEntity item = list.get(i);
                    for (PositionEntity items : lists) {
                        if (item.getAdCode().equals(items.getAdCode())
                                &&item.getAddress().equals(items.getAddress())
                                ){
                            AppUtils.setValue(item,items);
                        }
                    }
                    dao.insertOrReplace(item);
                }
            }
        });
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
