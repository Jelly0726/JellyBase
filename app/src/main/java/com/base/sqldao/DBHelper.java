package com.base.sqldao;

import android.content.Context;
import android.text.TextUtils;

import com.base.applicationUtil.MyApplication;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import systemdb.DaoSession;
import systemdb.Login;
import systemdb.LoginDao;
import systemdb.PositionEntity;
import systemdb.PositionEntityDao;
import systemdb.SearchHistory;
import systemdb.SearchHistoryDao;


/**
 * Created by Administrator on 2016/4/12.
 */
public class DBHelper {

    private static Context mContext;

    private static DBHelper instance;
    private LoginDao loginDao;
    private PositionEntityDao positionEntityDao;
    private SearchHistoryDao searchHistoryDao;
    private Login login;
    private DBHelper(){

    }
    public  static DBHelper getInstance(Context context){
        if(instance ==null){
            instance=new DBHelper();
            if(mContext == null){
                mContext = context;
            }
            //数据库对象
            DaoSession daoSession = MyApplication.getDaoSession(mContext);
            instance.loginDao = daoSession.getLoginDao();
            instance.positionEntityDao=daoSession.getPositionEntityDao();
            instance.searchHistoryDao=daoSession.getSearchHistoryDao();
        }
        return  instance;
    }
    //===========↓↓↓↓↓↓↓用户数据↓↓↓↓↓↓↓↓=================
    /**
     添加登录数据
     */
    public long addToLoginfoTable(Login item){
        long id=loginDao.insert(item);
        return id;
    }

    /**
     * 取出所有数据
     * @return      所有数据信息
     */
    public List<Login> getLoginList() {
        try {
            QueryBuilder<Login> qb = loginDao.queryBuilder();
            // return loginDao.loadAll();//获取整个表的数据集合,一句代码就搞定！
            return qb.list();
        }catch (Exception e){
            return null;
        }
    }
    /**
     获取一条数据
     */
    public Login getLogin(){
        try {
            Login logi = null;
            if (getLoginList().size() > 0) {
                logi = getLoginList().get(0);
                if (logi != null) {
                    if (logi.getUserID() != null) {
                        if (logi.getUserID() == 0) {
                            logi = null;
                        }
                    } else {
                        logi = null;
                    }
                }
            }
            if (login == null) {
                login = logi;
            } else {
                login = (Login) setValue(login, logi);
            }
            return login;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 给对象的变量赋值
     * @param set   要赋值的对象 不能为空
     * @param get   要取值的对象 不能为空
     * @return
     */
    public Object setValue(Object set, Object get){
        if(set!=null&&get!=null) {
            Class setTemp = set.getClass(); // 获取Class类的对象的方法之一
            Class getTemp = get.getClass(); // 获取Class类的对象的方法之一
            Field[] setfb = setTemp.getDeclaredFields();//返回所有属性的成员变量的数组
            Field[] getfb = getTemp.getDeclaredFields();//返回所有属性的成员变量的数组
            //List setList=Arrays.asList(setfb);//数组转list
            List getList= Arrays.asList(getfb);//数组转list
            try {
                for (int i = 0; i < setfb.length; i++) {
                    if(getList.contains(setfb[i])) {
                        if(setfb[i].getName().equals("serialVersionUID")){
                            continue;
                        }
                        Class cl = setfb[i].getType();    // 属性的类型
                        if(cl.toString()
                                .equals("interface com.android.tools" +
                                        ".fd.runtime.IncrementalChange"))
                            continue;
                        int md = setfb[i].getModifiers();    // 属性的修饰域
                        Field f = getTemp.getDeclaredField(setfb[i].getName());// 属性的值
                        f.setAccessible(true);    // Very Important
                        Object value = (Object) f.get(get);
                        if(value==null) {
                            value=new ConvertUtilsBean().convert(0,cl);
                        }
                        Method method = set.getClass()
                                .getMethod("set" + getMethodName(setfb[i].getName()), cl);
                        method.invoke(set, value);
                        //Object returnObj = ConvertUtils.convert(str, clazz);
                    }else{
                        if(setfb[i].getName().equals("serialVersionUID")){
                            continue;
                        }
                        Class cl = setfb[i].getType();    // 属性的类型
                        if(cl.toString()
                                .equals("interface com.android.tools" +
                                        ".fd.runtime.IncrementalChange"))
                            continue;
                        int md = setfb[i].getModifiers();    // 属性的修饰域
                        Field f = setTemp.getDeclaredField(setfb[i].getName());// 属性的值
                        f.setAccessible(true);    // Very Important
                        Object value =  f.get(set);
                        if(value==null) {
                            value = new ConvertUtilsBean().convert(0, cl);
                            Method method = set.getClass()
                                    .getMethod("set" + getMethodName(setfb[i].getName()), cl);
                            method.invoke(set, value);
                        }
                    }
                }

                return set;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    /**
     * 将属性名称的首字母变成大写
     * */
    private String getMethodName(String fieldName) {
        byte[] bytes = fieldName.getBytes();
        bytes[0] = (byte) (bytes[0] - 'a' + 'A');
        return new String(bytes);
    }
    /**
     查询某个表是否包含某个id:
     */
    public boolean isSaved(int Id){
        QueryBuilder<Login> qb = loginDao.queryBuilder();
        // qb.where(CustomerDao.Properties.Id.eq(Id));
        qb.buildCount().count();
        return qb.buildCount().count()>0?true:false;
    }
    /**
     * 根据用户id,取出用户信息
     * @param id           用户id
     * @return             用户信息
     */
    public Login loadLogin(long id) {
        if(!TextUtils.isEmpty(id + "")) {
            return loginDao.load(id);
        }
        return  null;
    }
    /**
     * 根据查询条件,返回数据列表
     * @param where        条件
     * @param params       参数
     * @return             数据列表
     */
    public List<Login> queryLogin(String where, String... params){
        return loginDao.queryRaw(where, params);
    }


    /**
     * 根据用户信息,插入或修改信息
     * @param login  用户信息
     * @return 插入或修改的用户id
     */
    public long updateLogin(Login login){
        return loginDao.insertOrReplace(login);
    }


    /**
     * 批量插入或修改用户信息
     * @param list      用户信息列表
     */
    public void updateLoginLists(final List<Login> list){
        if(list == null || list.isEmpty()){
            return;
        }
        loginDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<list.size(); i++){
                    Login login = list.get(i);
                    loginDao.insertOrReplace(login);
                }
            }
        });

    }

    /**
     * 根据id,删除数据
     * @param id      用户id
     */
    public void deleteLogin(long id){
        loginDao.deleteByKey(id);
    }

    /**
     * 根据用户类,删除信息
     * @param login    用户信息类
     */
    public void deleteLogin(Login login){
        loginDao.delete(login);
    }

    /**
     删除全部用户
     */
    public void clearLogin(){
        loginDao.deleteAll();
    }
    //===========↑↑↑↑↑↑↑↑↑用户数据↑↑↑↑↑↑↑↑↑=================
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
