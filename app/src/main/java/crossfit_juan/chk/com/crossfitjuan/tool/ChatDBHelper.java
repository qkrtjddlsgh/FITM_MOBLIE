package crossfit_juan.chk.com.crossfitjuan.tool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import crossfit_juan.chk.com.crossfitjuan.DataModel.ChatData;

/**
 * Created by gyuhwan on 2017. 9. 17..
 */

public class ChatDBHelper extends SQLiteOpenHelper{

    final private String TAG="ChatDataDBHelper";
    int last_idx=0;
    public ChatDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM CHATDATA",null);
        last_idx=cursor.getCount();
    }

    public int getLast_idx() {
        return last_idx;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    //    Log.d(TAG,"Create!!");
        db.execSQL("CREATE TABLE CHATDATA (_id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, message TEXT,time TEXT,date TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void InsertData(ChatData cData){
        String sender=cData.getSender();
        String message=cData.getContent();
        String time=cData.getTime();
        String date=cData.getDate();
        SQLiteDatabase db=getWritableDatabase();
     //   Log.d(TAG,"Insert!!"+db.toString());

        db.execSQL("INSERT INTO CHATDATA VALUES(null, "+"'"+sender+"', '"+message+"', '"+time+"', '"+date+"');");
        last_idx++;
        db.close();
    }

    public ArrayList<ChatData> getResult(){
        SQLiteDatabase db=getReadableDatabase();
        ArrayList<ChatData> result=new ArrayList<ChatData>();

        Cursor cursor=db.rawQuery("SELECT * FROM CHATDATA",null);
        while(cursor.moveToNext()){
            ChatData a=new ChatData();
           // Log.d("HEllo",cursor.getString(1));
            a.setSender(cursor.getString(1));
            a.setContent(cursor.getString(2));
            a.setTime(cursor.getString(3));
            a.setDate(cursor.getString(4));
            result.add(a);
        }
//        Log.d(TAG,"Get Result!!");

        db.close();
        return result;
    }

}