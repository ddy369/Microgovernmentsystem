package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans;

/**
 * Created by Administrator on 2016-09-19.
 */
public class ListBeanAttachment {

    private String name;
    private String size;
    private String url;

    public ListBeanAttachment(String name,String size,String url){
        this.name = name;
        this.size = size;
        this.url = url;
    }

    public String getName(){
        return name;
    }

    public String getSize(){
        return size;
    }

    public String getUrl(){
        return url;
    }

}
