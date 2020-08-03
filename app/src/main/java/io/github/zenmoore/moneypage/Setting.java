package io.github.zenmoore.moneypage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class Setting {

    static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table jie(name text primary key,value text)";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    //    private static File file;
//    private static SQLiteDatabase rdb;
    private static SQLiteDatabase db;

    public static void setDatabase(Context context){
        db = new DatabaseHelper(context,"setting",null,1).getWritableDatabase();
//        rdb = new DatabaseHelper(context,"setting",null,1).getReadableDatabase();
    }

    private static boolean exists(String name){
        Cursor cursor;
        boolean a=false;
        cursor = db.rawQuery("select name from jie", null);
        while(cursor.moveToNext()){
            //遍历出表名
            if(cursor.getString(0).equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public static void set(String name, String value){
            // delete former
            if(exists(name)){
                ContentValues values = new ContentValues();
                values.put("value", value);
                db.update("jie", values, "name = ?", new String[]{name});
            }else {
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("value", value);
                db.insert("jie",null, values);
            }
    }

    public static String read(String name) throws Exception {
        if(!exists(name)){
            set("default_multiple", "10.0");
            return read(name);
        }else{
            //创建游标对象
            Cursor cursor = db.query("jie", null, null, null, null, null, null);
            //利用游标遍历所有数据对象
            //为了显示全部，把所有对象连接起来，放到TextView中
            //判断游标是否为空
            if(cursor.moveToFirst()){
                for(int i=0;i<cursor.getCount();i++){
                    cursor.move(i);
                    if(cursor.getString(0).equals(name)){
                        return cursor.getString(1);
                    }
                }
            }
            return null;
        }
    }

//    public static void setFile(String dir){
//        file = new File(dir + "/setting.txt");
//    }
//
//    public static void set(String name, String value) throws Exception {
//        if (file.exists()) {
//            // delete former
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            ArrayList<String> list = new ArrayList<>();
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                if (!line.contains(name)) {
//                    list.add(line);
//                }
//            }
//
//            FileWriter fileWritter = new FileWriter(file.getName(), true);
//            Iterator<String> iterator = list.listIterator();
//            while (iterator.hasNext()) {
//                fileWritter.write(iterator.next() + "\n");
//            }
//            fileWritter.write(name + "=" + value + "\n");
//            fileWritter.close();
//
//        } else {
//            if(file.getParentFile().exists()){
//                file.createNewFile();
//            }else {
//                file.getParentFile().mkdir();
//            }
//
//
//            FileWriter fileWritter = new FileWriter(file.getName(), true);
//            fileWritter.write(name + "=" + value + "\n");
//            fileWritter.close();
//        }
//    }
//
//    public static String read(String name) throws Exception {
//        if(!file.exists()){
//            set("default_multiple", "10.0");
//            return read(name);
//        }
//
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        String line = "";
//        while ((line = reader.readLine()) != null) {
//            if (line.contains(name)) {
//                line = line.split("=")[1];
//                return line;
//            }
//        }
//        throw new Exception("can not get \"" + name + "\"");
//    }

}
