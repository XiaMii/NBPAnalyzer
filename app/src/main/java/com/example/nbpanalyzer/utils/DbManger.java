package com.example.nbpanalyzer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.nbpanalyzer.Constant;
import com.example.nbpanalyzer.Bean.Nbp_data;

import java.util.ArrayList;
import java.util.List;
/**
 * DbManger 操作我们数据库的工具类  我们一般写成单例模式
 * 单例模式 ：  在整个应用程序中  不管什么地方（类）  获得的都是同一个对象实例
 * @author XIAMII
 */
public class DbManger {
    private static final String TAG = "DbManger";
    private static MySqliteHelper helper; //建立一个数据库对象
    //表名
    private String table_name ="Nbp_data";
    /**单例模式
     * 不能让每一个类都能new一个
     * 那样就不是同一个对象了
     * 所以首先构造函数要私有化
     * 以上下文作为参数
     * @param ctx 本类的上下文对象
     * @return
     */
     private DbManger(Context ctx){
        //由于数据库只需要调用一次，所以在单例中建出来
        helper= new MySqliteHelper(ctx);
    }

    //public static 为静态类型  要调用就要有一个静态的变量    为私有的
    private static DbManger instance;


    //既然BlackDao类是私有的  那么别的类就不能够调用    那么就要提供一个public static（公共的  共享的）的方法
    //方法名为getInstance 参数为上下文    返回值类型为BlackDao
    //要加上一个synchronized（同步的）
    //如果同时有好多线程 同时去调用getInstance()方法  就可能会出现一些创建（new）多个BlackDao的现象  所以要加上synchronized
    public static synchronized DbManger getInstance(Context ctx){
        //就可以判断  如果为空 就创建一个， 如果不为空就还用原来的  这样整个应用程序中就只能获的一个实例
        if(instance == null){
            instance = new DbManger(ctx);
        }
        return  instance;
    }

    //常用方法  增删改查
    /**
     * 添加数据 至数据库
     * @param USER
     * @param DATE
     */
    public void addData(String USER,String DATE,String TIME,int DIS,int SYS,int PR){
        //获得一个可写的数据库的一个引用
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values= new ContentValues();
        values.put(Constant.USER, USER); // KEY 是列名，vlaue 是该列的值
        values.put(Constant.DATE, DATE);// KEY 是列名，vlaue 是该列的值
        values.put(Constant.TIME, TIME);// KEY 是列名，vlaue 是该列的值
        values.put(Constant.DIS, DIS); // KEY 是列名，vlaue 是该列的值
        values.put(Constant.SYS, SYS);// KEY 是列名，vlaue 是该列的值
        values.put(Constant.PR, PR); // KEY 是列名，vlaue 是该列的值

        // 参数一：表名，参数三，是插入的内容
        // 参数二：只要能保存 values中是有内容的，第二个参数可以忽略
        db.insert(table_name, null, values);
        Log.e(TAG, "addData: 数据更新成功："+USER+""+DATE+""+TIME+""+PR);
    }

    /**
     * 删除用户
     * @param user
     */
    public void deletebyuser(String user){
        SQLiteDatabase db = helper.getWritableDatabase();
        //表名  删除的条件
        db.delete(table_name, "user = ?", new String[] {user});

    }

    /**
     * 删除某条记录
     * @param id
     */
    public void deletebyid(int id){
        SQLiteDatabase db = helper.getWritableDatabase();
        //表名  删除的条件
        db.delete(table_name, "id = ?", new String[] {Integer.valueOf(id).toString()});
    }


    /**
     * 修改某条记录
     * @param id
     */
    public void updatebyid(int id,String USER,String DATE,String TIME,int DIS,int SYS,int PR){
        SQLiteDatabase db = helper.getWritableDatabase();
        //table：代表想要更新数据的表名。values：代表想要更新的数据。
        // whereClause：满足该whereClause子句的记录将会被更新。
        // whereArgs：用于为whereArgs子句传递参数。
        Log.e(TAG, "修改某条记录" );
        ContentValues values= new ContentValues();
        values.put(Constant.USER, USER); // KEY 是列名，vlaue 是该列的值
        values.put(Constant.DATE, DATE);// KEY 是列名，vlaue 是该列的值
        values.put(Constant.TIME, TIME);// KEY 是列名，vlaue 是该列的值
        values.put(Constant.DIS, DIS); // KEY 是列名，vlaue 是该列的值
        values.put(Constant.SYS, SYS);// KEY 是列名，vlaue 是该列的值
        values.put(Constant.PR, PR); // KEY 是列名，vlaue 是该列的值
        values.put(Constant.ID,id);
        db.update(table_name, values,"id = ?", new String[] {Integer.valueOf(id).toString()});
    }


    /**
     * //查找 每一个黑名单都有 号码和模式  先把号码和模式封装一个bean
     * 获得所有的黑名单
     * @param user 用户名
     * @param pageIndex 页数
     * @param pageSize 每页显示的行数
     * @return
     */
    //分页查询 修改
    public List<Nbp_data> getAllData(String user, int pageIndex, int pageSize, int offSets){
        //public List<BlackNumBean> getAllBlackNum(){
        //创建集合对象
        List<Nbp_data> data = new ArrayList<Nbp_data>();
        SQLiteDatabase db = helper.getReadableDatabase();
        //Cursor cursor = db.query(table_black_num, null, null, null, null, null, null);

        //pageSize显示的数目，pageIndex页数
        Cursor cursor = db.rawQuery("select * from Nbp_data where user = ? order by id desc limit "+pageSize +" offset "+((pageIndex-1)*pageSize+(offSets))+";", new String[]{user});
        //Cursor cursor = db.rawQuery("select * from black_num order by _id desc limit "+pageSize+" offset "+(pageIndex*pageSize)+";", null);

        // 返回的 cursor 默认是在第一行的上一行
        //遍历
        while(cursor.moveToNext()){// cursor.moveToNext() 向下移动一行,如果有内容，返回true
            String TIME = cursor.getString(cursor.getColumnIndex("time"));
            // 获得time 这列的值
            String DATE = cursor.getString(cursor.getColumnIndex("date"));
            // 获得date 这列的值
            int SYS = cursor.getInt(4);
            int DIS = cursor.getInt(5);
            int PR = cursor.getInt(6);
            int ID = cursor.getInt(0);

            //将number mode 封装到bean中
            Nbp_data bean = new Nbp_data(ID,user,DATE,TIME,SYS,DIS,PR);
            //封装的对象添加到集合中
            data.add(bean);
        }

        //关闭cursor
        cursor.close();
        //SystemClock.sleep(1000);// 休眠2秒，模拟黑名单比较多，比较耗时的情况
        Log.e(TAG, "getAllData: 发送数据库数据"+data );
        return data;
    }


    /**
     * 获得数据的数量
     */
    public int getNumCount(String user){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(table_name, new String[] {"count(*)"}, "user = ?",new String[] {user}, null, null, null, null);

        cursor.moveToNext();
        int count = cursor.getInt(0);// 仅查了一列，count(*) 这一刻列

        cursor.close();
        return count;

    }




    /**
     * 根据用户日期，获得所有数据
     * @param date user
     * @return
     */
    public int getdataBydate(String date,String user) {
        //List<Nbp_data> data = new ArrayList<Nbp_data>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select id from Nbp_data where user=? and date = ?",
                new String[]{user,date});
        int ID = 0;
        //Cursor cursor = db.rawQuery("select TIME, SYS, DIS, PR from Nbp_data where user = ? and date = ?;", new String[]{user,date});
        //Cursor cursor = db.rawQuery("select * from Nbp_data where user = ? order by id desc limit "+pageSize +" offset "+((pageIndex-1)*pageSize)+";", new String[]{user});
//        int index = 1;
//        while (!cursor.moveToNext() && index<10){
//            String[] days = date.split("-",3);
//            Log.e(TAG, "getdataBydate: "+days[2]);
//            int day = Integer.valueOf(days[2]);
////            date = days[0]+days[1]+"-"+day;
////            cursor = db.rawQuery("select * from Nbp_data where user = ? and date = ?", new String[]{user , date});
//            index = index+1;
//        }

        if(cursor.moveToNext()){
            cursor.moveToLast();
            ID = cursor.getInt(cursor.getColumnIndex("id"));
        }

        //遍历
//        while(cursor.moveToNext()){
//            String TIME = cursor.getString(cursor.getColumnIndex("time"));
//            // 获得time 这列的值
//            int SYS = cursor.getInt(4);
//            int DIS = cursor.getInt(5);
//            int PR = cursor.getInt(6);
//
//            //将number mode 封装到bean中
//            Nbp_data bean = new Nbp_data(user,date,TIME,SYS,DIS,PR);
//            //封装的对象添加到集合中
//            data.add(bean);
//            Log.e(TAG, "getAllData:遍历" );
//        }
        // cursor.moveToNext() 向下移动一行,如果有内容，返回true
        cursor.close();
        Log.e(TAG, "getAllData: ID"+ID );

        return ID;
    }

}
