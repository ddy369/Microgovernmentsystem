package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Dao extends SQLiteOpenHelper {
    private final static String databaseName = "AppDatabase";

    public Dao(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, databaseName, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreateLoginTable = "CREATE TABLE LOGINTABLE (account TEXT PRIMARY KEY," +
                "uid TEXT ," +
                "password TEXT NOT NULL," +
                "lastlogin INTEGER DEFAULT 0," +
                "name TEXT," +
                "position TEXT," +
                "mac TEXT);";
        sqLiteDatabase.execSQL(sqlCreateLoginTable);
        String CREATE_usertable="CREATE TABLE UserTable(" +
                "id integer primary key autoincrement," +
                "name TEXT ," +
                "department TEXT ," +
                "position TEXT ," +
                "tel TEXT," +
                "mobile_phone TEXT," +
                "remark TEXT," +
                "em TEXT,"+
                "type TEXT,"+
                "Company TEXT,"+
                "Rd TEXT);";
        sqLiteDatabase.execSQL(CREATE_usertable);
        String CREATE_Recentusertable="CREATE TABLE RecentUserTable(" +
                "id integer primary key autoincrement," +
                "name TEXT ," +
                "department TEXT ," +
                "position TEXT ," +
                "tel TEXT," +
                "mobile_phone TEXT," +
                "remark TEXT," +
                "em TEXT,"+
                "type TEXT,"+
                "Company TEXT,"+
                "Rd TEXT);";
        sqLiteDatabase.execSQL(CREATE_Recentusertable);
        String sqlCreateNoticeListTable = "CREATE TABLE NOTICELISTTABLE (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "department TEXT," +
                "sendtime TEXT," +
                "title TEXT," +
                "read INTEGER DEFAULT 0," +
                "recordid TEXT);";
        sqLiteDatabase.execSQL(sqlCreateNoticeListTable);
        String sqlCreateRecordIdTable = "CREATE TABLE RECORDIDTABLE (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " recordid TEXT);";
        sqLiteDatabase.execSQL(sqlCreateRecordIdTable);

        //在此处更改了数据库
        String sqlCreateNoticeTable = "CREATE TABLE NOTICETABLE (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "recordid TEXT," +
                "title TEXT," +
                "sender TEXT," +
                "time TEXT," +
                "organizelist TEXT," +
                "peoplelist TEXT," +
                "isreturn TEXT," +
                "sendmessage TEXT," +
                "content TEXT);";
        sqLiteDatabase.execSQL(sqlCreateNoticeTable);
        String sqlCreateNoticeAttachmentTable = "CREATE TABLE NoticeAttachmentTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "url TEXT," +
                "size TEXT," +
                "recordid TEXT);";
        sqLiteDatabase.execSQL(sqlCreateNoticeAttachmentTable);

        String sqlCreateDocumentTable = "CREATE TABLE DocumentTable (" +
                "id TEXT," +
                "purview TEXT," +
                "subject TEXT," +
                "updateTime TEXT," +
                "title TEXT," +
                "read INTEGER DEFAULT 0," +
                "sendPeople TEXT);";
        sqLiteDatabase.execSQL(sqlCreateDocumentTable);

        String sqlCreateDocumentFile = "CREATE TABLE DocumentFile (" +
                "id INTEGER ," +
                "fileName TEXT," +
                "filePath TEXT," +
                "parentId TEXT);";
        sqLiteDatabase.execSQL(sqlCreateDocumentFile);


        String sqlCreateDocumentDetail = "Create Table DocumentDetail (" +
                "id INTEGER , "+
                "mainTitle TEXT ,"+
                "docNumber TEXT,"+
                "docPeople TEXT,"+
                "signPeople TEXT, "+
                "docSignDate TEXT,"+
                "docPrintDate TEXT,"+
                "title TEXT,"+
                "docSecret TEXT,"+
                "docUrgent TEXT,"+
                "docContent TEXT,"+
                "docAttachment TEXT,"+
                "docAttachmentName TEXT,"+
                "docMark TEXT,"+
                "docExplain TEXT);";
        sqLiteDatabase.execSQL(sqlCreateDocumentDetail);

        String sqlCreateMemorandumTable = "CREATE TABLE MemorandumTable(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "content TEXT," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "hour INTEGER," +
                "minute INTEGER," +
                "switch INTEGER);";
        sqLiteDatabase.execSQL(sqlCreateMemorandumTable);

        String sqlCreateMagazineTable = "CREATE TABLE MagazineTable (" +
                "id TEXT," +
                "subject TEXT," +
                "updateTime TEXT," +
                "title TEXT," +
                "numNO TEXT," +
                "read INTEGER DEFAULT 0," +
                "sendPeople TEXT," +
                "year TEXT," +
                "year_no TEXT,"+
                "sysId TEXT);";
        sqLiteDatabase.execSQL(sqlCreateMagazineTable);

        String sqlCreateMagazineDetailTable = "CREATE TABLE MagazineDetail (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "magNum TEXT,"+
                "magDepart TEXT ,"+
                "updateTime TEXT," +
                "subject TEXT);" ;
        sqLiteDatabase.execSQL(sqlCreateMagazineDetailTable);

        String sqlCreateMagazineFileTable = "CREATE TABLE MagazineFile (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "filePath TEXT," +
                "parentId TEXT," +
                "totalNO TEXT);" ;
        sqLiteDatabase.execSQL(sqlCreateMagazineFileTable);

        String sqlCreatePublicInformationListTable = "CREATE TABLE PublicInformationListTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "updateDate TEXT," +
                "field TEXT,"+
                "read INTEGER DEFAULT 0);";
        sqLiteDatabase.execSQL(sqlCreatePublicInformationListTable);

        String sqlCreatePublicInformationTable = "CREATE TABLE PublicInformationTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "infoPeople TEXT," +
                "field TEXT,"+
                "infoDate TEXT,"+
                "infotitle TEXT,"+
                "infocContent TEXT,"+
                "infoSecret TEXT,"+
                "infoVideoAddress TEXT,"+
                "infoPictureFile TEXT);";
        sqLiteDatabase.execSQL(sqlCreatePublicInformationTable);

        String sqlCreateDepartmentInformationListTable = "CREATE TABLE DepartmentInformationListTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "updateDate TEXT," +
                "subject TEXT,"+
                "sendPeople TEXT,"+
                "departName TEXT,"+
                "read INTEGER DEFAULT 0);";
        sqLiteDatabase.execSQL(sqlCreateDepartmentInformationListTable);

        String sqlCreateDepartmentInformationTable = "CREATE TABLE DepartmentInformationTable ("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "depDate TEXT,"+
                "depPeople TEXT,"+
                "mainTitle TEXT,"+
                "FileName TEXT,"+
                "departName TEXT,"+
                "depAttachment TEXT);";
        sqLiteDatabase.execSQL(sqlCreateDepartmentInformationTable);

        //isChoice表示部门联络人被选中，用于通知到部门功能中
        //selected表示部门及部门下的所有人被选中，用于通知到人功能中
        String sqlCreateDepartmentTable = "CREATE TABLE DepartmentTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "departmentId TEXT," +
                "departmentName TEXT," +
                "isChoice INTEGER  DEFAULT 0," +
                "selected INTEGER  DEFAULT 0);";
        sqLiteDatabase.execSQL(sqlCreateDepartmentTable);

        String sqlCreateReceiverTable = "CREATE TABLE ReceiverTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "receiverId TEXT," +
                "receiverName TEXT," +
                "isChoice INTEGER  DEFAULT 0," +
                "departmentId TEXT," +
                "orderNum INTEGER);";
        sqLiteDatabase.execSQL(sqlCreateReceiverTable);

        //备注，仅用于存放在本地的联系人，flag用于查找并更改备注
        //flag由姓名+部门+职位经md5加密后产生
        String sqlCreateRemarkTable = "CREATE TABLE RemarkTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "flag TEXT," +
                "remark TEXT);";
        sqLiteDatabase.execSQL(sqlCreateRemarkTable);

        //增加了三个表用于查看自己发布的通知
        String sqlCreatePublishedNoticeListTable = "CREATE TABLE PublishedNoticeListTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "department TEXT," +
                "sendtime TEXT," +
                "title TEXT," +
                "read INTEGER DEFAULT 0," +
                "recordid TEXT);";
        sqLiteDatabase.execSQL(sqlCreatePublishedNoticeListTable);

        String sqlCreatePublishedNoticeTable = "CREATE TABLE PublishedNoticeTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "recordid TEXT," +
                "title TEXT," +
                "sender TEXT," +
                "time TEXT," +
                "organizelist TEXT," +
                "peoplelist TEXT," +
                "isreturn TEXT," +
                "sendmessage TEXT," +
                "content TEXT);";
        sqLiteDatabase.execSQL(sqlCreatePublishedNoticeTable);

        String sqlCreatePublishedNoticeAttachmentTable = "CREATE TABLE PublishedNoticeAttachmentTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "url TEXT," +
                "size TEXT," +
                "recordid TEXT);";
        sqLiteDatabase.execSQL(sqlCreatePublishedNoticeAttachmentTable);

        String sqlCreateReadStatusTable = "CREATE TABLE ReadStatusTable (" +
                "ReadId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id INTEGER);";
        sqLiteDatabase.execSQL(sqlCreateReadStatusTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        switch (i) {
            case 1:

                break;
//            case 2:
//
//                break;
//            case 3:
//
//                break;
        }
    }
}
