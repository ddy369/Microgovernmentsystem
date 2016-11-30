package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;

/**
 * Created by Administrator on 2016/8/4.
 */
public class Datahelper {
    static Datahelper datahelper = new Datahelper();
    static private List<Contacts> contactses;
    static private List<Contacts> contactsesforname;
    static private Context context;
    static private Toast toast;
    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
    private Dao dao = new Dao(context, null, Values.database_version);

    static public Datahelper getDatahelper() {
        return datahelper;
    }

    static public Context getContext() {
        return context;
    }

    static public void setContext(Context context1) {
        context = context1;
    }

    public List<Contacts> getusers(callback a) {
        if (contactses == null||contactses.size()==0) {
            switch (checkNetworkState.checkNetworkState()) {
                case 0:
                    //0表示无网络，从本地读取数据
                    return Readuserfromdb(a);
                case 1:
                case 2:
                    contactses =Readuserfromdb(a);
                    contactses = getuserfromnet(a);
                 //   contactsesforname=Readuserfromdb(a);
                    //1.2表示有网络，先从云端获取数据，若成功则返回数据并更新数据库，否则从本地读取数据
                    return contactses;
            }
        }
        a.callback(contactses);
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                UserComparator comparator=new UserComparator();
                Collections.sort(contactsesforname,comparator);

            }
        });*/

        return contactses;
    }

    public List<Contacts> getusersforname(callback a) {
        if (contactsesforname == null) {
            switch (checkNetworkState.checkNetworkState()) {
                case 0:
                    //0表示无网络，从本地读取数据
                     Readuserfromdb(a);
                return contactsesforname;

                case 1:
                case 2:
                    Readuserfromdb(a);
                    getuserfromnet(a);

                    //1.2表示有网络，先从云端获取数据，若成功则返回数据并更新数据库，否则从本地读取数据
                    return contactsesforname;
            }
        }
        a.callback(contactsesforname);
        return contactsesforname;
    }
    private List<Contacts> getuserfromnet(final callback a) {
        String canshu = "uid=" + getUid() +
                "&mac=" + getMac() +
                "&sign=" + getSign() +
                "&timestamp=" + getTimeStamp();

        HttpPost.sendHttpRequest(Values.ServerAddress + "/Contact", canshu, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                contactses = new ArrayList<>();
                contactsesforname=new ArrayList<Contacts>();
                try {
// 在这里根据返回内容执行具体的逻辑

                    if (response != null) {
                        JSONObject object = new JSONObject(response);
                        int errorCode = object.getInt("error_code");
                        if (errorCode == 0) {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object1 = (JSONObject) jsonArray.get(i);
                                String name = object1.getString("Name");
                                String department = object1.getString("DepartmentName");
                                String position = object1.getString("OfficeShip");
                                String tel = object1.getString("OfficePhone");
                                String mobile_phone = object1.getString("MobilePhone");
                                //   String Email = object1.getString("Email");
                                String Company = object1.getString("Company");
                                String remark = object1.getString("Alt");
                                String type = object1.getString("Type");
                                String emid = object1.getString("Hid");
                                String rid = object1.getString("RecordId");

                                Contacts tem = new Contacts();
                                tem.set_information(name, position, department, tel, mobile_phone, Company, type, emid, rid, remark);
                                Log.d("net", remark + position + department + tel + mobile_phone + Company + type + emid);
                                contactses.add(tem);
                                contactsesforname.add(tem);

                            }

                        } else if (errorCode == 201) {
                            if (toast == null)
                                toast = new Toast(context);
                            toast = Toast.makeText(context, "uid无效,请重新登录", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (errorCode == 202) {
                            if (toast == null)
                                toast = new Toast(context);
                            toast = Toast.makeText(context, "MAC地址错误", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
                a.callback(contactses);
                Saveusertodb(contactses);
            }

            @Override
            public void onError(Exception e) {
// 在这里对异常情况进行处理
                e.printStackTrace();
                a.callback(contactses);
            }
        });


        return contactses;
    }

    /**
     * 从数据库里读出联系人信息
     */
    private List<Contacts> Readuserfromdb(callback a) {
        contactses = new ArrayList<>();
        contactsesforname= new ArrayList<>();
        SQLiteDatabase db = dao.getWritableDatabase();
        Cursor cursor = db.query("UserTable", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Contacts tem = new Contacts();
                tem.name = cursor.getString(cursor.
                        getColumnIndex("name"));
                tem.department = cursor.getString(cursor.
                        getColumnIndex("department"));
                tem.position = cursor.getString(cursor.
                        getColumnIndex("position"));
                tem.tel = cursor.getString(cursor.
                        getColumnIndex("tel"));
                tem.mobile_phone = cursor.getString(cursor.
                        getColumnIndex("mobile_phone"));
                tem.remark = cursor.getString(cursor.
                        getColumnIndex("remark"));
                tem.emid = cursor.getString(cursor.
                        getColumnIndex("em"));
                tem.Company = cursor.getString(cursor.
                        getColumnIndex("Company"));
                tem.type = cursor.getString(cursor.
                        getColumnIndex("type"));
                tem.RecordId = cursor.getString(cursor.
                        getColumnIndex("Rd"));

                contactses.add(tem);
                contactsesforname.add(tem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        a.callback(contactses);
        return contactses;
    }

    /**
     * 把通讯录里的联系人信息存入数据库
     */
    public void Saveusertodb(List<Contacts> users1) {
        SQLiteDatabase db = dao.getWritableDatabase();
        db.delete("UserTable", null, null);
        ContentValues values = new ContentValues();
        values.clear();
        for (Contacts contacts : users1) {
            values.put("name", contacts.name);
            values.put("department", contacts.department);
            values.put("position", contacts.position);
            values.put("tel", contacts.tel);
            values.put("mobile_phone", contacts.mobile_phone);
            values.put("remark", contacts.remark);
            values.put("em", contacts.emid);
            values.put("type", contacts.type);
            values.put("Company", contacts.Company);
            values.put("Rd", contacts.RecordId);
            db.insert("UserTable", null, values);
            values.clear();
        }
        db.close();
    }

    /**
     * 测试数据生成
     */
    /**
     * public List<Contacts> testdata() {
     * contactses = new ArrayList<>();
     * String[] department = new String[]{
     * "党工委会领导", "管委会领导", "办公室", "政协工委领导", "区直工委"};
     * <p>
     * //子视图显示文字
     * String[][] name = new String[][]{
     * {"霍玉民", "杨艳敏", "刘宏伟", "安豫军", "余菁"},
     * {"霍玉民", "杨艳敏", "刘宏伟", "安豫军", "余菁"},
     * {"霍玉民", "杨艳敏", "刘宏伟", "安豫军", "余菁"},
     * {"霍玉民", "杨艳敏", "刘宏伟", "安豫军", "余菁"},
     * {"霍玉民", "杨艳敏", "刘宏伟", "安豫军", "余菁"},
     * };
     * <p>
     * String[] position = new String[]{
     * "党工委委员", "办公室主任", "副主任", "110联动办主任", "宣传信息处处长"};
     * int i, j;
     * for (i = 0; i < 5; i++)
     * for (j = 0; j < 5; j++) {
     * Contacts tem = new Contacts();
     * tem.set_information(name[i][j], position[j], department[i], "", "");
     * contactses.add(tem);
     * }
     * return contactses;
     * }
     */

    public void setremark(Contacts contacts) {
        String canshu = "uid=" + getUid() +
                "&mac=" + getMac() +
                "&sign=" + getSign1(contacts.name) +
                "&timestamp=" + getTimeStamp() + "&alt=" + contacts.name;
        HttpPost.sendHttpRequest(Values.ServerAddress + "/Contact/" + contacts.RecordId, canshu, new HttpCallbackListener() {

            @Override
            public void onFinish(String response) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

    }
    public static void showToast(final String toast)
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    private String getMac() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getTimeStamp() {
        //获取时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    private String getUid() {
        return getUserInformationDao.getUid();
    }

    private String getSign() {
        HashMap<String, String> map = new HashMap<>();
        map.put("mac", getMac());
        map.put("timestamp", getTimeStamp());
        map.put("uid", getUid());
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
        //获取签名值
//        return md5Tool.getMDSStr("govoa"+"mac"+getMac()+"password"+getPassword()+"timestamp"+getTimeStamp()
//                +"username"+account+"govoa");
    }
    private String getSign1(String name) {
        HashMap<String, String> map = new HashMap<>();
        map.put("mac", getMac());
        map.put("timestamp", getTimeStamp());
        map.put("uid", getUid());
        map.put("alt", name);
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
        //获取签名值
//        return md5Tool.getMDSStr("govoa"+"mac"+getMac()+"password"+getPassword()+"timestamp"+getTimeStamp()
//                +"username"+account+"govoa");
    }
    public interface callback {
        void callback(List<Contacts> contactses);
    }

    static private List<Contacts> RecentContacts;
    public List<Contacts> getRecentContacts()
    {
        if (RecentContacts==null||RecentContacts.size()==0)
        {
            RecentContacts=new  ArrayList<>();
            SQLiteDatabase db = dao.getWritableDatabase();
            Cursor cursor = db.query("RecentUserTable", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Contacts tem = new Contacts();
                    tem.name = cursor.getString(cursor.
                            getColumnIndex("name"));
                    tem.department = cursor.getString(cursor.
                            getColumnIndex("department"));
                    tem.position = cursor.getString(cursor.
                            getColumnIndex("position"));
                    tem.tel = cursor.getString(cursor.
                            getColumnIndex("tel"));
                    tem.mobile_phone = cursor.getString(cursor.
                            getColumnIndex("mobile_phone"));
                    tem.remark = cursor.getString(cursor.
                            getColumnIndex("remark"));
                    tem.emid = cursor.getString(cursor.
                            getColumnIndex("em"));
                    tem.Company = cursor.getString(cursor.
                            getColumnIndex("Company"));
                    tem.type = cursor.getString(cursor.
                            getColumnIndex("type"));
                    tem.RecordId = cursor.getString(cursor.
                            getColumnIndex("Rd"));

                    RecentContacts.add(tem);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

        }
        else
        {
            return RecentContacts;
        }
        return RecentContacts;
    }
    public void setRecentContacts(Contacts a)
    {
        if (RecentContacts==null)
        {
            getRecentContacts();
        }
        RecentContacts.remove(a);
        RecentContacts.add(0,a);
        if(RecentContacts.size()>50)
            RecentContacts.remove(RecentContacts.size()-1);
        SQLiteDatabase db = dao.getWritableDatabase();
        db.delete("RecentUserTable", null, null);
        ContentValues values = new ContentValues();
        values.clear();
        for (Contacts contacts : RecentContacts) {
            values.put("name", contacts.name);
            values.put("department", contacts.department);
            values.put("position", contacts.position);
            values.put("tel", contacts.tel);
            values.put("mobile_phone", contacts.mobile_phone);
            values.put("remark", contacts.remark);
            values.put("em", contacts.emid);
            values.put("type", contacts.type);
            values.put("Company", contacts.Company);
            values.put("Rd", contacts.RecordId);
            db.insert("RecentUserTable", null, values);
            values.clear();
        }
        db.close();
    }

}
