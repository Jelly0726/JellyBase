package com.base.sqldao;

import android.content.Context;
import android.database.Cursor;

import com.jelly.baselibrary.Utils.StringUtil;

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
    private DaoSession daoSession;
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
                    instance.daoSession = DBManager.getDBManager().getDaoSession();
                    instance.dao = instance.daoSession.getPositionEntityDao();
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
     * @param sql          sql语句
     * @param params       参数
     * @return             数据列表
     */
    public List<String> queryBySQL(String sql, String... params){
        ArrayList<String> result = new ArrayList<String>();
        Cursor c = daoSession.getDatabase().rawQuery(sql, params);
        try{
            if (c.moveToFirst()) {
                do {
                    result.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
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
     * @param offset  页码第一页页码为0
     * @param size    每页条数
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
                if(firstUsername.contains(key.toLowerCase())){
                    list.add(item);
                }
            }
        }
        List<PositionEntity> listMsg = qb.whereOr(PositionEntityDao.Properties.AdCode.like("%" + key + "%")
                ,PositionEntityDao.Properties.Address.like("%" + key + "%")).distinct().list();
        for (PositionEntity item : listMsg) {
            boolean isExist=false;
            for (PositionEntity items : list) {
                if (item.getAdCode().equals(items.getAdCode())
                        &&item.getId().equals(items.getId())){
                    isExist=true;
                    break;
                }
            }
            if (!isExist){
                list.add(item);
            }
        }
        return list;
    }
    /**
     * 获取数据总数
     * @return
     */
    public long queryCount(){
        QueryBuilder<PositionEntity> qb = dao.queryBuilder();
        return qb.count();
    }
    /**
     * 根据地址搜索记录,插入或修改信息
     * @param item
     * @return 插入或修改的id
     */
    public long update(PositionEntity item){
        for (PositionEntity items : getAllList()) {
            if (item.getAdCode().equals(items.getAdCode())
                    &&item.getAddress().equals(items.getAddress())
            ){
                item.setId(items.getId());
            }
        }
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
                            item.setId(items.getId());
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
     * 根据会员id,删除数据
     * @param id      id
     */
    public void delete(String id){
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("DELETE");
        stringBuffer.append(" FROM ");
        stringBuffer.append(PositionEntityDao.TABLENAME);
        stringBuffer.append(" WHERE ");
        stringBuffer.append(PositionEntityDao.Properties.AdCode.columnName);
        stringBuffer.append(" = ?");
        daoSession.getDatabase().execSQL(stringBuffer.toString(), new String[]{id});
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
