package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans;

/**
 * Created by Administrator on 2016-08-17.
 */
public class ListBeanDocument {

    private String id;
    private String title;
    private String subject;
    private String updateDate;
    private int read;
    private String purview;

    public ListBeanDocument(String id,String title,String subject,String updateDate,int read,String purview){
        this.id = id;
        this.title =title;
        this.subject = subject;
        this.updateDate = updateDate;
        this.read = read;
        this.purview = purview;
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
    public String getPurview(){return purview;}
}
