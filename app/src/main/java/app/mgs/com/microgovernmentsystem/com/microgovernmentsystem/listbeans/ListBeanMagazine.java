package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans;

/**
 * Created by Administrator on 2016-08-22.
 */
public class ListBeanMagazine {
    private String id;
    private String title;
    private String subject;
    private String updateDate;
    private int read;
    private String sendPeople;
    private String sysId;
    private String year_no;

    public ListBeanMagazine(String id,String title,String subject,String updateDate,int read,String sendPeople,String sysId,String year_no ){
        this.id = id;
        this.title =title;
        this.subject = subject;
        this.updateDate = updateDate;
        this.read = read;
        this.sendPeople = sendPeople;
        this.sysId = sysId;
        this.year_no =year_no;
    }

    public String getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getSubject(){
        return subject;
    }
    public String getUpdateDate(){
        return  updateDate;
    }
    public int getRead(){return read;}
    public String getSendPeople(){
        return sendPeople;
    }
    public String getSysId(){
        return  sysId;
    }
    public String getYear_no(){
        return  year_no;
    }
}
