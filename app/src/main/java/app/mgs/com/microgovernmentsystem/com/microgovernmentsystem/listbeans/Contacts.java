package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/31.
 */
public class Contacts implements Parcelable {
    public static final Parcelable.Creator<Contacts> CREATOR = new Creator<Contacts>() {
        @Override
        public Contacts[] newArray(int size) {
            return new Contacts[size];
        }

        @Override
        public Contacts createFromParcel(Parcel in) {
            return new Contacts(in);
        }
    };
    //此处名字为备注，备注为名字；
    public String name;
    public String position;
    public String department;
    public String tel;
    public String mobile_phone;
    public String remark;
    //false为公共联系人，true 为私有联系人
    public String type;
    public String Company;
    public boolean ischeck;
    public String emid;
    public String RecordId;
    public boolean flag=true;
//修改重置信息函数，done
// 修改接口，done
// 在datahelper里构造users，done
// 重构数据库,done
// 修改联系人详细信息页面done
// 根据type显示不同的效果，拨打电话跳转目标修改，登录环信帐号修改，done
// 环信新消息来通知完善。
// 修改备注页面，done
// 实现私人联系人适配器改造done

    public Contacts() {
        name = position = department = "";
        mobile_phone = "";
        tel="";
        ischeck = false;
        emid=remark=name="";
        RecordId="";
    }

    public Contacts(Parcel in) {
        name = in.readString();
        department = in.readString();
        position = in.readString();
        tel = in.readString();
        mobile_phone = in.readString();
        emid= in.readString();
        Company= in.readString();
        type= in.readString();
        RecordId= in.readString();
        ischeck = false;
    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(department);
        out.writeString(position);
        out.writeString(tel);
        out.writeString(mobile_phone);
        out.writeString(emid);
        out.writeString(Company);
        out.writeString(type);
        out.writeString(RecordId);
    }
    public void set_information(String name1, String position1, String department1, String tel1, String mobile_phone1,
                                String Company1,String type1,String emid1,String RecordId1,String remark1) {
        if(!name1.isEmpty())
        remark = name1;
        if(!position1.isEmpty())
        position = position1;
        if(!department1.isEmpty())
        department = department1;
        if(!tel1.isEmpty())
        tel = tel1;
        if(!mobile_phone1.isEmpty())
        mobile_phone = mobile_phone1;
        if(!emid1.isEmpty())
        emid=emid1;
        if(!Company1.isEmpty())
            Company=Company1;
        if(!type1.isEmpty())
            type=type1;
        if(name.isEmpty())
            name=remark1;
        if(name.isEmpty())
            name=remark;
      /*  if(name.isEmpty())
            name=position;*/
        if(name.isEmpty())
            name=position;
        if(name.isEmpty())
            name=department;
        if(name.isEmpty())
            name=Company;
        if(name.isEmpty())
            name="未知";
        if(!RecordId1.isEmpty())
            RecordId=RecordId1;
       // if(department.isEmpty())
       //     department="私有联系人";
    }

    public void toMail_list(Context context) {
        Log.d("main", name + "\n" + tel);
        Toast  toast= new Toast(context); ;
        Cursor cursor = null;
        try {
            // 查询联系人数据
            cursor = context.getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                    + "=?",
                            new String[]{name}, null);

            while (cursor.moveToNext()) {
                // 获取联系人姓名
                String displayName = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                // 获取联系人手机号
                String number = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d("main", displayName + "\n" + number);
            }
            Log.d("main", String.valueOf(cursor.getCount()));

            if (cursor.getCount() == 0) {
                            /* 往 raw_contacts 中添加数据，并获取添加的id号 */
                Uri uri = Uri
                        .parse("content://com.android.contacts/raw_contacts");
                ContentResolver resolver = context
                        .getContentResolver();
                ContentValues values = new ContentValues();
                long contactId = ContentUris.parseId(resolver
                        .insert(uri, values));

							/* 往 data 中添加数据（要根据前面获取的id号） */
                // 添加姓名
                uri = Uri
                        .parse("content://com.android.contacts/data");
                values.put("raw_contact_id", contactId);
                values.put("mimetype",
                        "vnd.android.cursor.item/name");
                values.put("data2", name);
                resolver.insert(uri, values);



                //往data表插入电话数据
                values.clear();
                values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, contactId);
                values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile_phone);
                values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);//插入手机号码
                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                //往data表插入其他电话数据

                values.clear();
                values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, contactId);
                values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, tel);
                values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);//插入除了其他号码
                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

//                // 添加电话
//                values.clear();
//                values.put("raw_contact_id", contactId);
//                values.put("mimetype",
//                        "vnd.android.cursor.item/phone_v2");
//                values.put("data2", "2");
//                values.put("data1", tel);
//                resolver.insert(uri, values);
//                // 添加电话
//                values.clear();
//                values.put("raw_contact_id", contactId);
//                values.put("mimetype",
//                        "vnd.android.cursor.item/phone_v2");
//                values.put("data2", "2");
//                values.put("data1", mobile_phone);
//                resolver.insert(uri, values);

            }
            else
            {
                if(flag) {
                    toast = Toast.makeText(context, "此联系人已在通讯录中", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                if(flag) {
                    toast = Toast.makeText(context, "导入成功", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        }
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public boolean equals(Object obj)
    {
        Contacts tem=(Contacts)obj;
        if(tem.name.equals(name))
            if(tem.tel.equals(tel))
                if(tem.department.equals(department))
                    if(tem.position.equals(position))
                        if(tem.mobile_phone.equals(mobile_phone))
                            return true;
        return false;
    }
}
