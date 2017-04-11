package cn.illegal.orm;

import java.io.Serializable;

public class UsernameORM implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -3292563997434686880L;
    
    private long id;
    /** 添加时间 */
    private long addTime;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getAddTime() {
        return addTime;
    }
    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

}
