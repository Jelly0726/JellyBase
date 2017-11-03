package systemdb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

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
}
