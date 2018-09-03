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
    private LoginDao loginDao;
    private Login login;
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
                    instance.loginDao = daoSession.getLoginDao();
                }
            }
            return  instance;
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
}
