package com.base.sqldao;

import android.content.Context;
import android.text.TextUtils;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.greenrobot.greendao.query.QueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
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
                item = (Login) setValue(item, items);
            }
            return item;
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
                                .equals("interface com.android.tools.fd.runtime.IncrementalChange")
                                ||cl.toString()
                                .equals("interface com.android.tools.ir.runtime.IncrementalChange"))
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
                                .equals("interface com.android.tools.fd.runtime.IncrementalChange")
                                ||cl.toString()
                                .equals("interface com.android.tools.ir.runtime.IncrementalChange"))
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
