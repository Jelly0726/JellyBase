package com.base.sqldao;

import android.content.Context;
import android.text.TextUtils;

import com.base.Utils.StringUtil;
import com.base.applicationUtil.AppUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import systemdb.DaoSession;
import systemdb.Login;
import systemdb.LoginDao;


/**用户数据
 * Created by Administrator on 2016/4/12.
 */
public class LoginDaoUtils {

    private static Context mContext;
    /**
     * 但是jdk 1.5 以后java 编译器允许乱序执行 。所以执行顺序可能是1-3-2 或者 1-2-3.如果是前者先执行3 的话
     * 切换到其他线程，instance 此时 已经是非空了，此线程就会直接取走instance ，直接使用，这样就回出错。DCL 失效。
     * 解决方法 SUN 官方已经给我们了。将instance 定义成 private volatile static Singleton instance =null: 即可
     */
    private volatile static LoginDaoUtils instance;
    private LoginDao dao;
    private Login item;
    private LoginDaoUtils(){

    }
    /**
     * 单一实例
     */
    public  static LoginDaoUtils getInstance(Context context){
        if(instance ==null) {
            synchronized (LoginDaoUtils.class) {
                if (instance == null) {
                    instance = new LoginDaoUtils();
                    if (mContext == null) {
                        mContext = context.getApplicationContext();
                    }
                    //数据库对象
                    DaoSession daoSession = DBManager.getDBManager().getDaoSession();
                    instance.dao = daoSession.getLoginDao();
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
    public long addTable(Login item){
        long id= dao.insert(item);
        return id;
    }

    /**
     * 取出所有数据
     * @return      所有数据信息
     */
    public List<Login> getAllList() {
        try {
            QueryBuilder<Login> qb = dao.queryBuilder();
            // return dao.loadAll();//获取整个表的数据集合,一句代码就搞定！
            return qb.list();
        }catch (Exception e){
            return null;
        }
    }
    /**
     获取一条数据
     */
    public Login getItem(){
        try {
            Login items = null;
            if (getAllList().size() > 0) {
                items = getAllList().get(0);
                if (items != null) {
                    if (items.getUserID() != null) {
                        if (items.getUserID() == 0) {
                            items = null;
                        }
                    } else {
                        items = null;
                    }
                }
            }
            if (item == null) {
                item = items;
            } else {
                item = (Login) AppUtils.setValue(item, items);
            }
            return item;
        }catch (Exception e){
            return null;
        }
    }
    /**
     查询某个表是否包含某个id:
     */
    public boolean isSaved(int Id){
        QueryBuilder<Login> qb = dao.queryBuilder();
        // qb.where(CustomerDao.Properties.Id.eq(Id));
        qb.buildCount().count();
        return qb.buildCount().count()>0?true:false;
    }
    /**
     * 根据用户id,取出用户信息
     * @param id           用户id
     * @return             用户信息
     */
    public Login load(long id) {
        if(!TextUtils.isEmpty(id + "")) {
            return dao.load(id);
        }
        return  null;
    }
    /**
     * 根据查询条件,返回数据列表
     * @param where        条件
     * @param params       参数
     * @return             数据列表
     */
    public List<Login> query(String where, String... params){
        return dao.queryRaw(where, params);
    }
    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    public List<Login> query(int offset, int size){
        List<Login> listMsg = dao.queryBuilder()
                .offset(offset * size).limit(size).list();
        return listMsg;
    }
    /**
     * 模糊查询
     * @param key
     * @return
     */
    public List<Login> query(String key){
        QueryBuilder<Login> qb = dao.queryBuilder();
        List<Login> list=new ArrayList<>();
        //判断输入的是中文还是英文
        if(StringUtil.isEnglish(key)){
            for (Login item : getAllList()) {
                String username = item.getName();
                String firstUsername = StringUtil.getSpells(username);
                if(firstUsername.contains(key)){
                    list.add(item);
                }
            }
        }else {
            list.addAll(qb.whereOr(LoginDao.Properties.Name.like("%" + key + "%")
                    ,LoginDao.Properties.Code.like("%" + key + "%")).distinct().list());
        }
        return list;
    }

    /**
     * 根据用户信息,插入或修改信息
     * @param item
     * @return 插入或修改的id
     */
    public long update(Login item){
        return dao.insertOrReplace(item);
    }


    /**
     * 批量插入或修改信息
     * @param list
     */
    public void update(final List<Login> list){
        if(list == null || list.isEmpty()){
            return;
        }
        dao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<list.size(); i++){
                    Login item = list.get(i);
                    for (Login items : getAllList()) {
                        if (item.getCode().equals(items.getCode())
                                &&item.getId().equals(items.getId())){
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
    public void delete(Login item){
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
