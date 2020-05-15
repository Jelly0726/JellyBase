package systemdb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/28.
 */
@Entity
public class SearchHistory implements Serializable{
    private static final long serialVersionUID=1L;
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private Long time;
    private String history;

    @Generated(hash = 1551902490)
    public SearchHistory(Long id, @NotNull Long time, String history) {
        this.id = id;
        this.time = time;
        this.history = history;
    }

    @Generated(hash = 1905904755)
    public SearchHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
    //深度复制
    public Object deepclone()  {
        try {
            //将对象写到流里
            ByteArrayOutputStream bo=new ByteArrayOutputStream();
            ObjectOutputStream oo=new ObjectOutputStream(bo);
            oo.writeObject(this);//从流里读出来
            ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi=new ObjectInputStream(bi);
            return(oi.readObject());
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
