package com.base.sqldao;

import android.content.Context;

import com.base.Utils.StringUtil;
import com.base.applicationUtil.AppUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import systemdb.DaoSession;
import systemdb.SearchHistory;
import systemdb.SearchHistoryDao;


/**搜索记录
 * Created by Administrator on 2016/4/12.
 */
public class HistoryDaoUtils {

    private static Context mContext;

    private static HistoryDaoUtils instance;
    private SearchHistoryDao dao;
    private HistoryDaoUtils(){

    }
    public  static HistoryDaoUtils getInstance(Context context){
        if(instance ==null) {
            synchronized (HistoryDaoUtils.class) {
                if (instance == null) {
                    instance = new HistoryDaoUtils();
                    if (mContext == null) {
                        mContext = context.getApplicationContext();
                    }
                    //数据库对象
                    DaoSession daoSession = DBManager.getDBManager().getDaoSession();
                    instance.dao = daoSession.getSearchHistoryDao();
                }
            }
            return  instance;
        }
        return  instance;
    }
    //===========↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓=================
    /**
     添加数据
     */
    public void addTable(List<SearchHistory> item){
        for(SearchHistory items:item){
            addTable(items);
        }
    }
    /**
     添加数据
     */
    public long addTable(SearchHistory item){
        long id= dao.insert(item);
        return id;
    }

    /**
     * 取出所有数据
     * @return      所有数据信息
     */
    public List<SearchHistory> getAllList() {
        try {
            // return loginDao.loadAll();//获取整个表的数据集合,一句代码就搞定！
            QueryBuilder<SearchHistory> qb = dao.queryBuilder();
            qb.orderDesc(SearchHistoryDao.Properties.Time);
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
    public List<SearchHistory> query(String where, String... params){
        return dao.queryRaw(where, params);
    }
    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    public List<SearchHistory> query(int offset, int size){
        List<SearchHistory> listMsg = dao.queryBuilder()
                .offset(offset * size).limit(size).list();
        return listMsg;
    }

    /**
     * 模糊查询
     * @param key
     * @return
     */
    public List<SearchHistory> query(String key){
        QueryBuilder<SearchHistory> qb = dao.queryBuilder();
        List<SearchHistory> list=new ArrayList<>();
        //判断输入的是中文还是英文
        if(StringUtil.isEnglish(key)){
            for (SearchHistory item : getAllList()) {
                String username = item.getHistory();
                String firstUsername = StringUtil.getSpells(username);
                if(firstUsername.contains(key)){
                    list.add(item);
                }
            }
        }else {
            list.addAll(qb.whereOr(SearchHistoryDao.Properties.History.like("%" + key + "%")
                    ,SearchHistoryDao.Properties.Time.like("%" + key + "%")).distinct().list());
        }
        return list;
    }
    /**
     * 根据搜索记录,插入或修改信息
     * @param item
     * @return 插入或修改的id
     */
    public long update(SearchHistory item){
        return dao.insertOrReplace(item);
    }


    /**
     * 批量插入或修改数据
     * @param list
     */
    public void update(final List<SearchHistory> list){
        if(list == null || list.isEmpty()){
            return;
        }
        dao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                List<SearchHistory> lists=getAllList();
                for(int i=0; i<list.size(); i++){
                    SearchHistory item = list.get(i);
                    for (SearchHistory items : lists) {
                        if (item.getHistory().equals(items.getHistory())
                                &&item.getTime().equals(items.getTime())){
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
    public void delete(SearchHistory item){
        dao.delete(item);
    }
    /**
     删除全部数据
     */
    public void clear(){
        dao.deleteAll();
    }
    //===========↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑=================
}
