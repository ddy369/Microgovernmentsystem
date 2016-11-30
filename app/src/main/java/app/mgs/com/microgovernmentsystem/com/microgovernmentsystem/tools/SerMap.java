package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import java.io.Serializable;
import java.util.HashMap;


public class SerMap implements Serializable {
    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public HashMap<String,String> map;

    public SerMap(){

    }
}
