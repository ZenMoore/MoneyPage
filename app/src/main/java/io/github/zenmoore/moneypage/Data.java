package io.github.zenmoore.moneypage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class Data {

    static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table kun(_id integer primary key autoincrement, commodities text,amounts real, times text, books text, pages real)";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static SQLiteDatabase database;

    private static int num = 0;
    private static double multiple = 0.0;
    private static String[] commodities;
    private static double[] amounts;
    private static String[] times;
    private static String[] books;
    private static double[] pages;

    public static int getNum() {
        return num;
    }

    public static String[] getCommodities() {
        return commodities;
    }

    public static double[] getAmounts() {
        return amounts;
    }

    public static String[] getTimes() {
        return times;
    }

    public static String[] getBooks() {
        return books;
    }

    public static double[] getPages() {
        return pages;
    }

    public static boolean[] getFinished() {
        return finished;
    }

    private static boolean[] finished;


    public static void setDatabase(final Context context) {
        database = new DatabaseHelper(context, "data", null, 1).getWritableDatabase();
//        database.execSQL("drop table kun");
//        database.execSQL("create table kun(_id integer primary key autoincrement, commodities text,amounts real,times text,books text,pages real)");
    }

    public static void insert(String commodity, String book, String time, double amount, double page) {
        ContentValues cv = new ContentValues();
//        cv.put("_id", null);
        cv.put("commodities", commodity);
        cv.put("books", book);
        cv.put("times", time);
        cv.put("amounts", amount);
        cv.put("pages", page);
        database.insert("kun", null, cv);
    }

    public static void update(int id, String commodity, String book, String time, double amount, double page) {
        ContentValues values = new ContentValues();
        values.put("commodities", commodity);
        values.put("amounts", amount);
        values.put("times", time);
        values.put("books", book);
        values.put("pages", page);
        printall();
        database.update("kun", values, "_id=?", new String[]{"" + id});
        printall();

    }

    public static void printall(){
        Cursor cursor = database.query("kun", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            for(int i=0; i<cursor.getCount(); i++){
                cursor.moveToPosition(i);
                System.out.print("_id=" + cursor.getString(0)+", ");
                System.out.print("commodity=" + cursor.getString(1)+", ");
                System.out.print("amount=" + cursor.getDouble(2)+", ");
                System.out.print("time=" + cursor.getString(3)+", ");
                System.out.print("book=" + cursor.getString(4)+", ");
                System.out.println("page=" + cursor.getString(5));
            }
        }
    }

    //
    public static void remove(int id) {
        Data.load();
//        database.delete("kun", "_id=?", new String[]{""+id});
        database.execSQL("drop table kun");
        database.execSQL("create table kun(_id integer primary key autoincrement, commodities text,amounts real, times text, books text, pages real)");
        for(int i=0; i<Data.num; i++){
            if(i != id-1){
                Data.insert(commodities[i], books[i],times[i], amounts[i], pages[i]);
            }
        }

    }

    public static int num_items() {
        return database.query("kun", null, null, null, null, null, null).getCount();
    }

    public static void change_multiple(double multiple) {
        Data.multiple = multiple;
        // first set the multiple
        Cursor cursor = database.query("kun", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                double p = cursor.getDouble(2) * Data.multiple;
                ContentValues values = new ContentValues();
                values.put("pages", p);
                database.update("kun", values, "_id=?", new String[]{"" + (i + 1)});
            }
        }
    }

    public static void load() {
        try {
            // 将数据库的信息加载到全局变量中
            Cursor cursor = database.query("kun", null, null, null, null, null, null);
            //利用游标遍历所有数据对象
            //为了显示全部，把所有对象连接起来，放到TextView中
            //判断游标是否为空
            if (cursor.moveToFirst()) {
//                if (cursor.getCount() == 0) {
//                    Data.insert("placeholder", "placeholder", "placeholder", 1.0, 10.0);
//                }
                num = cursor.getCount();
                commodities = new String[num];
                amounts = new double[num];
                times = new String[num];
                books = new String[num];
                pages = new double[num];

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    commodities[i] = cursor.getString(1);
                    amounts[i] = cursor.getDouble(2);
                    times[i] = cursor.getString(3);
                    books[i] = cursor.getString(4);
                    pages[i] = cursor.getDouble(5);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
