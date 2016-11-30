package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans;


public class ListBeanSelect {
    public String getListViewItem() {
        return ListViewItem;
    }

    public void setListViewItem(String listViewItem) {
        ListViewItem = listViewItem;
    }
    public ListBeanSelect(){}

    public ListBeanSelect(String Value,boolean status){
        this.ListViewItem = Value;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String ListViewItem;
    private boolean status;

}
