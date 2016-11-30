package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans;

/**
 * 供几个信息列表使用的listBean
 */
public class ListBean {


    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String department;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String title;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String time;

    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int read;


    public String field;
    public String getField(){ return field;}
    public void setField(String field){ this.field = field; }

    public String updateDate;
    public String getUpdateDate(){ return updateDate;}
    public void setUpdateDate(String updateDate){ this.updateDate = updateDate; }

    public String subject;
    public String getSubject(){ return subject;}
    public void setSubject(String subject){ this.subject = subject; }

    public String sendPeople;
    public String getSendPeople(){ return sendPeople;}
    public void setSendPeople(String sendPeople){ this.sendPeople = sendPeople; }

    public String departName;
    public String getDepartName(){ return departName;}
    public void setDepartName(String departName){ this.departName = departName; }
}