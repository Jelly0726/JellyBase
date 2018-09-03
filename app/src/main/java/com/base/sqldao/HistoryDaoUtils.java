package com.base.sqldao;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

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
    private SearchHistoryDao searchHistoryDao;
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
                    instance.searchHistoryDao = daoSession.getSearchHistoryDao();
                }
            }
            return  instance;
        }
        return  instance;
    }
    //===========↓↓↓↓↓↓↓搜索记录↓↓↓↓↓↓↓↓=================
    /**
     添加搜索记录数据
     */
    public void addToHistoryListfoTable(List<SearchHistory> item){
        for(SearchHistory searchHistory:item){
            addToHistoryfoTable(searchHistory);
        }
    }
    /**
     添加搜索记录数据
     */
    public long addToHistoryfoTable(SearchHistory item){
        long id=searchHistoryDao.insert(item);
        return id;
    }

    /**
     * 取出所有搜索记录数据
     * @return      所有搜索记录数据信息
     */
    public List<SearchHistory> getHistory() {
        try {
            QueryBuilder<SearchHistory> qb = searchHistoryDao.queryBuilder();
            // return loginDao.loadAll();//获取整个表的数据集合,一句代码就搞定！
            qb.orderDesc(SearchHistoryDao.Properties.Time);
            return qb.list();
        }catch (Exception e){
            return null;
        }
    }
    /**
     * 根据查询条件,返回搜索记录数据列表
     * @param where        条件
     * @param params       参数
     * @return             数据列表
     */
    public List<SearchHistory> queryHistory(String where, String... params){
        return searchHistoryDao.queryRaw(where, params);
    }


    /**
     * 根据搜索记录,插入或修改信息
     * @param item  搜索记录
     * @return 插入或修改的搜索记录id
     */
    public long updatePosition(SearchHistory item){
        return searchHistoryDao.insertOrReplace(item);
    }


    /**
     * 批量插入或修改搜索记录
     * @param list      搜索记录列表
     */
    public void updateHistory(final List<SearchHistory> list){
        if(list == null || list.isEmpty()){
            return;
        }
        for(int i=0; i<list.size(); i++){
            SearchHistory item = list.get(i);
            searchHistoryDao.insertOrReplace(item);
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
     * @param id      搜索记录id
     */
    public void deleteHistory(long id){
        searchHistoryDao.deleteByKey(id);
    }
    /**
     * 根据搜索记录,删除信息
     * @param searchHistory    搜索记录
     */
    public void deleteHistory(SearchHistory searchHistory){
        searchHistoryDao.delete(searchHistory);
    }
    /**
     删除全部搜索记录
     */
    public void clearHistory(){
        searchHistoryDao.deleteAll();
    }
    //===========↑↑↑↑↑↑↑↑↑搜索记录↑↑↑↑↑↑↑↑↑=================
}
