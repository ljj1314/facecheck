package com.att;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.att.act.WriteUnit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final int VERSION = 2;// 版本
	private static final String DB_NAME = "att.db";// 数据库名
	// private static final String ASSETS_NAME = DB_NAME;
	/**
	 * 如果数据库文件较大，使用FileSplit分割为小于1M的小文件 此例中分割为 hello.db.101 hello.db.102
	 * hello.db.103
	 */
	// 第一个文件名后缀
	// private static final int ASSETS_SUFFIX_BEGIN = 101;
	// 最后一个文件名后缀
	// private static final int ASSETS_SUFFIX_END = 103;

	// 保存最大考勤数
	private static final int MAX_ATT_ADDR =1000000;

	// 如果你想把数据库文件存放在SD卡的话
	private static AppBaseFun appBaseFun = new AppBaseFun();
	private static String DB_PATH = appBaseFun.getPhoneCardPath() + "/tpatttp/SQLDB/";

	// 考勤记录信息
	public static final String ATTINFO_TABLE = "attinfo"; // 考勤记录
	public static final String _ID = "_id"; // 表中的列名
	public static final String ATTINFO_CARDID = "cardid"; // 卡号
	public static final String ATTINFO_DTIME = "dtime"; // 刷卡时间
	public static final String ATTINFO_STATUS = "status"; // 进出校状态
	public static final String ATTINFO_TRANSACTIONID = "transactionid";// 流程号
	public static final String ATTINFO_PHOTO = "photo";// 考勤图片标志
	// 创建数据库语句，STUDENT_TABLE，_ID ，NAME的前后都要加空格
	private static final String CREATE_ATTINFO_TABLE = "create table " + ATTINFO_TABLE + " ( " + _ID
			+ " Integer primary key autoincrement," + ATTINFO_CARDID + " text," + ATTINFO_DTIME + " text,"
			+ ATTINFO_STATUS + " text," + ATTINFO_TRANSACTIONID + " text," + ATTINFO_PHOTO + " text)";

	// 测量记录信息
	public static final String ATT_MEASURE_TABLE = "attmeasure"; // 测量记录
	public static final String ATT_MEASURE_CARDID = "caid"; // 卡号
	public static final String ATT_MEASURE_TEMP = "tem"; // 体温
	public static final String ATT_MEASURE_HEIG = "heig"; // 体重
	public static final String ATT_MEASURE_TALL = "tall";// 高度
	public static final String ATT_MEASURE_TIME = "time";// 时间
	private static final String CREATE_ATT_MEASURE_TABLE = "create table " + ATT_MEASURE_TABLE + " ( " + _ID
			+ " Integer primary key autoincrement," + ATT_MEASURE_CARDID + " text," + ATT_MEASURE_TEMP + " text,"
			+ ATT_MEASURE_HEIG + " text," + ATT_MEASURE_TALL + " text," + ATT_MEASURE_TIME + " text)";

	// 已刷考勤统计
	public static final String ATTTOTAL_TABLE = "atttotal"; // 考勤统计
	public static final String ATTTOTAL_ATT = "att"; // 已考勤记录数
	public static final String ATTTOTAL_ATTADDR = "attaddr"; // 已考勤保存地址ID
	public static final String ATTTOTAL_ATTPH = "attphoto"; // 已考勤图片记录数
	public static final String ATTTOTAL_ATT_MEASURE = "measure"; // 已测量数
	public static final String ATTTOTAL_ATT_MEASUREADDR = "measureaddr"; // 已测量保存地址ID
	private static final String CREATE_ATTTOTAL_TABLE = "create table " + ATTTOTAL_TABLE + " ( " + _ID
			+ " Integer primary key autoincrement," + ATTTOTAL_ATT + " text," + ATTTOTAL_ATTPH + " text,"
			+ ATTTOTAL_ATTADDR + " text," + ATTTOTAL_ATT_MEASURE + " text," + ATTTOTAL_ATT_MEASUREADDR + " text)";

	// 已报考勤统计
	public static final String UPATTTOTAL_TABLE = "upatttotal"; // 已报考勤统计
	public static final String ATTTOTAL_UPATT = "uploadatt"; // 已报考勤记录数
	public static final String ATTTOTAL_UPATTADDR = "uploadattaddr"; // 已报考勤保存地址ID
	public static final String ATTTOTAL_UPATTPH = "uploadattphoto"; // 已报考勤图片记录数
	public static final String ATTTOTAL_UPATTPHADDR = "uploadattphotoaddr";// 已报考勤图片保存地址ID
	public static final String ATTTOTAL_UP_MEASURE = "uploadmeasure"; // 已报测量数
	public static final String ATTTOTAL_UP_MEASUREADDR = "uploadmeasureaddr"; // 已报测量保存地址ID
	private static final String CREATE_UPATTTOTAL_TABLE = "create table " + UPATTTOTAL_TABLE + " ( " + _ID
			+ " Integer primary key autoincrement," + ATTTOTAL_UPATT + " text," + ATTTOTAL_UPATTPH + " text,"
			+ ATTTOTAL_UPATTADDR + " text," + ATTTOTAL_UPATTPHADDR + " text," + ATTTOTAL_UP_MEASURE + " text,"
			+ ATTTOTAL_UP_MEASUREADDR + " text)";



	public static final String TEMPERATURE_TABLE="temtotal";   //板载蓝牙体温统计
	public static final String TEM_TIME="ttime";    //体温记录时间
	public static final String TEM_CARD="tcard";    //刷卡卡号
	public static final String TEM_TEM="tem";       //体温度数
	public static final String TEM_STATU="tstatu";   //体温标志

	private static final String CREATE_TEMPERATURE_TABLE="create table "+TEMPERATURE_TABLE+" ( "+ _ID+
			" Integer primary key autoincrement," +TEM_TIME+ " text," +TEM_CARD+ " text," +TEM_TEM+ " text," +TEM_STATU
			+ " text)";

	//已报成功统计
	public  static final String  ATTSUC_TABLE       = "attsuc";          //考勤统计
	public  static final String  ATTSUC_ID        = "kdid";               //已考勤ID
	public  static final String ATTSUC_SUC      = "suc";          //已上报标志
	private static final String CREATE_ATTSUC_TABLE = "create table " + ATTSUC_TABLE + " ( " + _ID + " Integer primary key autoincrement,"
			+ ATTSUC_ID + " text,"
			+ ATTSUC_SUC + " text)";


	// private AttTotal attTotal = new AttTotal();
	@SuppressWarnings("unused")
	private boolean iscursor = false;

	private int uploadpic=0;



	private SQLiteDatabase myDataBase = null;
	// private final Context myContext;

	public DBOpenHelper(Context context) {
		super(context, DB_PATH + DB_NAME, null, VERSION);
		// this.myContext = context;
	}

	public synchronized void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();

		if (dbExist) {
			SQLiteDatabase db = this.getReadableDatabase();

			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + ATTINFO_TABLE
					+ "';";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					// 数据库已创建
				} else {
					Log.i("TPATT", "创建数据库表");
					db.execSQL(CREATE_ATTINFO_TABLE);
					db.execSQL(CREATE_ATT_MEASURE_TABLE);
					db.execSQL(CREATE_ATTTOTAL_TABLE);
					db.execSQL(CREATE_UPATTTOTAL_TABLE);
					db.execSQL(CREATE_TEMPERATURE_TABLE);
					db.execSQL(CREATE_ATTSUC_TABLE);

					ContentValues values = new ContentValues();
					values.put(ATTTOTAL_ATT, "0");
					values.put(ATTTOTAL_ATTPH, "0");
					values.put(ATTTOTAL_ATTADDR, "0");
					values.put(ATTTOTAL_ATT_MEASURE, "0");
					values.put(ATTTOTAL_ATT_MEASUREADDR, "0");
					db.insert(ATTTOTAL_TABLE, null, values);

					ContentValues upvalues = new ContentValues();
					upvalues.put(ATTTOTAL_UPATT, "0");
					upvalues.put(ATTTOTAL_UPATTPH, "0");
					upvalues.put(ATTTOTAL_UPATTADDR, "0");
					upvalues.put(ATTTOTAL_UPATTPHADDR, "0");
					upvalues.put(ATTTOTAL_UP_MEASURE, "0");
					upvalues.put(ATTTOTAL_UP_MEASUREADDR, "0");
					db.insert(UPATTTOTAL_TABLE, null, upvalues);


					ContentValues temvalues = new ContentValues();

					temvalues.put(TEM_TIME, "0");
					temvalues.put(TEM_CARD, "0");
					temvalues.put(TEM_TEM, "0");
					temvalues.put(TEM_STATU, "0");



					db.insert(TEMPERATURE_TABLE, null, temvalues);

					ContentValues valuesuc = new ContentValues();
					valuesuc.put(ATTSUC_ID, "0");
					valuesuc.put(ATTSUC_SUC, "0");

					db.insert(ATTSUC_TABLE, null, valuesuc);

				}
			}
		} else {
			// 创建数据库
			// try
			{
				Log.i("TPATT", "创建数据库:createDataBase");

				File dir = new File(DB_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				File dbf = new File(DB_PATH + DB_NAME);
				if (dbf.exists()) {
					dbf.delete();
				}
				SQLiteDatabase.openOrCreateDatabase(dbf, null);

				// copyDataBase();
			}
			// catch ( IOException e )
			{
				// throw new Error("数据库创建失败" + e.toString());
			}
		}
	}

	// 检查数据库是否有效
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		String myPath = DB_PATH + DB_NAME;
		try {
			File file = new File(myPath);
			if (file.exists()) {
				checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
			} else {
				return (false);
			}
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	// 删除已存在的数据库
	public void delDataBase() {
		File dbf = new File(DB_PATH + DB_NAME);

		if (dbf.exists()) {
			dbf.delete();
		}
	}


	public  void delSql(){

		//SQLiteDatabase.execSQL("DELETE FROM CUSTOMERS");
		SQLiteDatabase	db = this.getWritableDatabase();
		db.delete(CREATE_ATTINFO_TABLE,null,null);
		db.delete(CREATE_ATT_MEASURE_TABLE,null,null);
		db.delete(CREATE_ATTTOTAL_TABLE,null,null);
		db.delete(CREATE_UPATTTOTAL_TABLE,null,null);
		db.delete(CREATE_TEMPERATURE_TABLE, null, null);
		db.delete(CREATE_ATTSUC_TABLE, null, null);
	}


	/**
	 * 复制assets文件中的数据库到指定路径 使用输入输出流进行复制
	 **/
	/*
	 * private void copyDataBase() throws IOException { InputStream myInput =
	 * myContext.getAssets().open(ASSETS_NAME); String outFileName = DB_PATH +
	 * DB_NAME; OutputStream myOutput = new FileOutputStream(outFileName);
	 * byte[] buffer = new byte[1024]; int length;
	 *
	 * while ( (length = myInput.read(buffer)) > 0 ) { myOutput.write(buffer, 0,
	 * length); } myOutput.flush(); myOutput.close(); myInput.close(); }
	 */

	/*
	 * //复制assets下的大数据库文件时用这个 private void copyBigDataBase() throws IOException
	 * { InputStream myInput; String outFileName = DB_PATH + DB_NAME;
	 * OutputStream myOutput = new FileOutputStream(outFileName);
	 *
	 * for ( int i = ASSETS_SUFFIX_BEGIN; i < ASSETS_SUFFIX_END+1; i++ ) {
	 * myInput = myContext.getAssets().open(ASSETS_NAME + "." + i); byte[]
	 * buffer = new byte[1024]; int length;
	 *
	 * while ( (length = myInput.read(buffer)) > 0 ) { myOutput.write(buffer, 0,
	 * length); } myOutput.flush(); myInput.close(); } myOutput.close(); }
	 */

	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	// 数据库第一次被创建时调用
	@Override
	public void onCreate(SQLiteDatabase db) {

	}



	// 版本升级时被调用
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		String sql1 = "DROP TABLE IF EXISTS " + ATTINFO_TABLE;
		String sql2 = "DROP TABLE IF EXISTS " + ATT_MEASURE_TABLE;
		String sql3 = "DROP TABLE IF EXISTS " + ATTTOTAL_TABLE;
		String sql4 = "DROP TABLE IF EXISTS " + UPATTTOTAL_TABLE;
		String sql5 = "DROP TABLE IF EXISTS " + TEMPERATURE_TABLE;
		String sql6	=	"DROP TABLE IF EXISTS " + CREATE_ATTSUC_TABLE;
		db.execSQL(sql1);
		db.execSQL(sql2);
		db.execSQL(sql3);
		db.execSQL(sql4);
		db.execSQL(sql5);
		db.execSQL(sql6);
		onCreate(db);

	}

	/**
	 * 保存测量信息
	 */
	public synchronized void  saveAttMeasure(String cardid, String cardtime, String temp, String tall, String height) {
		try {
			Log.i("TPATT", "向数据库中保存测量数据 cardid:" + cardid);
			AttTotal attTotal = findAttTotal();
			// 保存的地址ID
			int measureAddr = Integer.parseInt(attTotal.getMeasureAddr());
			// 保存已测量总数
			int measureCount = Integer.parseInt(attTotal.getMeasureCount());
			if (measureCount >= MAX_ATT_ADDR) {
				if (measureAddr > MAX_ATT_ADDR) {
					measureAddr = 0;
				}
				Log.i("TPATT", "保存测量数据:updataAttMeasure");
				updataAttMeasure(measureAddr, cardid, cardtime, temp, tall, height);
			} else {
				Log.i("TPATT", "保存测量数据:addAttMeasure");
				addAttMeasure(cardid, cardtime, temp, tall, height);
			}
			// 更新统计信息
			measureAddr++;
			if (measureAddr >= MAX_ATT_ADDR) {
				measureAddr = 0;
			}
			measureCount++;
			attTotal.setMeasureAddr(String.valueOf(measureAddr));
			attTotal.setMeasure(String.valueOf(measureCount));
			updataAttTotal(attTotal);
		} catch (SQLiteException e) {
			Log.i("TPATT", "向数据库中保存测量数据:异常");
		}
	}

	/**
	 * 保存上报测量信息
	 */
	public void saveUploadAttMeasure(int measure) {
		try {
			Log.i("TPATT", "向数据库中保存已报测量数据:" + String.valueOf(measure));
			AttTotal upAttTotal = findUpAttTotal();
			// 保存已报测量
			if (measure != 0) {
				int attUploadMeasureAddr = Integer.parseInt(upAttTotal.getUploadMeasureAddr());
				int attUploadMeasure = Integer.parseInt(upAttTotal.getUploadMeasure());
				if (measure > 0) {
					attUploadMeasure += measure;
					attUploadMeasureAddr += measure;
				} else {
					attUploadMeasureAddr += 1;
				}
				if (attUploadMeasureAddr >= MAX_ATT_ADDR) {
					attUploadMeasureAddr -= MAX_ATT_ADDR;
				}
				upAttTotal.setUploadMeasureAddr(String.valueOf(attUploadMeasureAddr));
				upAttTotal.setUploadMeasure(String.valueOf(attUploadMeasure));
			}
			if (measure != 0) {
				updataUpAttTotal(upAttTotal);
			}
		} catch (SQLiteException e) {
			Log.i("TPATT", "向数据库中保存测量数据:异常");
		}
	}

	/*
	 * 增加板载蓝牙体温保存信息
	 *
	 *
	 */
	public synchronized void saveloadTEMPERATURE(String card,String time,String tem){

		try {

			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(TEM_CARD, card);
			values.put(TEM_TIME, time);
			values.put(TEM_STATU, 0);
			values.put(TEM_TEM, tem);

			db.insert(TEMPERATURE_TABLE, null, values);

			Log.i("TPATT", "数据库addtemInfo cardid:" + card+"...tem:"+tem);
		} catch (Exception e) {
			Log.i("TPATT", "数据库addAttInfo:异常" + e.toString());
		}



	}






	/**
	 * 查询数据库中的总条数.
	 * @return
	 */
	public synchronized long allCaseNum( ){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select count(*) from attinfo";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}


	/**
	 * 查询数据库中的总条数.
	 * @return
	 */
	public synchronized long allCaseNumtem( ){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select count(*) from temtotal" ;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}


	/**
	 * 保存考勤信息
	 */
	public void saveAttInfo(String cardid, String dtime, String status, String tranid, String photo) {
		try {
			//Log.i("TPATT", "向数据库中保存考勤数据 cardid:" + cardid);

			//	AttInfo attInfo=findUploadAttInfo();
			AttTotal attTotal = findAttTotal();// TODO 可能报错
			// CursorWindowAllocationException

			// 保存的地址
			int attAddr = Integer.parseInt(attTotal.getAttAddr());
			// 保存已考勤总数
			int attCount = Integer.parseInt(attTotal.getAtt());

//			if (attCount >= MAX_ATT_ADDR) {    //4.7修改备忘解决游标突变
//				if (attAddr > MAX_ATT_ADDR) {
//					attAddr = 0;
//				}
//				updataAttInfo(attAddr, cardid, dtime, status, tranid, photo);
//			} else {
			//Log.i("TPATT", "添加考勤数据addAttInfo cardid:" + cardid);
			addAttInfo(cardid, dtime, status, tranid, photo);
			//	}


			// 更新统计信息
			attAddr++;
//			if (attAddr >= MAX_ATT_ADDR) {
//				attAddr = 0;
//			}
			attCount++;

			long lo=allCaseNum();
			int num=(int)lo;
			if (attCount<num) {
				attCount=num;
			}
			Log.i("num", "num:"+num);
			if (attAddr<attCount&&attAddr<MAX_ATT_ADDR) {
				attAddr=attCount;
			}
			attTotal.setAttAddr(String.valueOf(attAddr));
			attTotal.setAtt(String.valueOf(attCount));
			updataAttTotal(attTotal);
		} catch (Exception e) {
			Log.i("TPATT", "向数据库中保存考勤数据:异常" + e.toString());
		}
	}



	/*
       * 增，用insert向数据库中插入数据
       */
	public void addAttInfosuc(int id,int status)
	{
		try
		{
			Log.v("TPATT", "向数据库中插入成功数据"+id);

			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues  values = new ContentValues();

			values.put(ATTSUC_ID, id);

			values.put(ATTSUC_SUC, status);


			db.insert(ATTSUC_TABLE, null, values);
		}
		catch ( SQLiteException e )
		{
			Log.v("TPATT", "向数据库中插入数据:异常");
		}
	}




	public HashMap<String, String> getdatalist(){

		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from  attsuc";
		Cursor cursor = db.rawQuery(sql,null);
		// List<Map<String, String>> lm=new ArrayList<Map<String,String>>();
		HashMap<String, String> mm=new HashMap<String, String>();
		if ( cursor.moveToFirst() )
		{
			do
			{

				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String sucid = cursor.getString(cursor.getColumnIndex(ATTSUC_ID));
				String status = cursor.getString(cursor.getColumnIndex(ATTSUC_SUC));

				//   mm.put("id", ""+_id);
				mm.put(sucid, status);
				//    mm.put("statu", status);

				//     lm.add(mm);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return mm;


	}



	/**
	 * 保存考勤图片信息
	 */
	public void saveAttPhotoInfo(boolean photo) {
		try {

			AttTotal attTotal = findAttTotal();

			// 保存已考总数
			// 更新统计信息
			if (photo == true) {
				int attPhotoCount = Integer.parseInt(attTotal.getAttPhoto());
				Log.i("TPATT", "向数据库中保存考勤图片数据 attPhotoCount:" + attPhotoCount);
				//Log.i("csv", "正在保存数:" + attPhotoCount);
				attPhotoCount++;
				long lo=allCaseNum();
				int num=(int)lo;
				if (attPhotoCount<num) {
					attPhotoCount=num;
				}
				attTotal.setAttPhoto(String.valueOf(attPhotoCount));
				//Log.i("csv", "正在保存数1:" + String.valueOf(attPhotoCount));
				// attTotal.setUploadAttPhoto("0");
				updataAttTotal(attTotal);
			}

		} catch (Exception e) {
			Log.i("TPATT", "向数据库中保存考勤图片数据:异常" + e.toString());
		}
	}

	/**
	 * 保存上报考勤或考勤图片信息
	 */
	public void saveUploadAttInfo(int att, int photo) {
		try {
			WriteUnit.debugLog("向数据库中保存已报考勤数量:" + String.valueOf(att) + ",考勤图片数量:" + String.valueOf(photo));
			Log.i("tapo", ""+att+".."+photo);
			AttTotal upAttTotal = findUpAttTotal();

			// 保存已报考勤数
			if (att > 0) {
				//Log.i("GOI", "GOI00");
				int attUploadAddr = Integer.parseInt(upAttTotal.getUploadAttAddr());//已报考勤地址ID
				int attUpload = Integer.parseInt(upAttTotal.getUploadAtt());//已报考勤数
				Log.i("tapo", ""+attUploadAddr+".."+attUpload);
				attUpload += att;
				attUploadAddr += att;
				if (attUploadAddr >= MAX_ATT_ADDR) {
					attUploadAddr -= MAX_ATT_ADDR;
				}
				Log.i("tapo", ""+attUpload+".."+attUploadAddr);
				upAttTotal.setUploadAttAddr(String.valueOf(attUploadAddr));
				upAttTotal.setUploadAtt(String.valueOf(attUpload));
			}

			// 保存已报考勤图片数
			if (photo != 0) {
				//Log.i("GOI", "GOI11");
				int attUploadPhotoAddr = Integer.parseInt(upAttTotal.getUploadAttPhotoAddr());
				int attUploadPhoto = Integer.parseInt(upAttTotal.getUploadAttPhoto());
				long lo=allCaseNum();

				if (attUploadPhotoAddr>uploadpic) {
					attUploadPhotoAddr=uploadpic;
				}


				if (photo > 0) {
					attUploadPhoto += photo;
					attUploadPhotoAddr += photo;
				} else {
					attUploadPhotoAddr += 1;
				}
				int num=(int)lo;
				if (attUploadPhotoAddr>num) {
					attUploadPhotoAddr=num;
				}
				if (attUploadPhotoAddr >= MAX_ATT_ADDR) {
					attUploadPhotoAddr -= MAX_ATT_ADDR;
				}


				upAttTotal.setUploadAttPhotoAddr(String.valueOf(attUploadPhotoAddr));
				upAttTotal.setUploadAttPhoto(String.valueOf(attUploadPhoto));
			}

			if ((att > 0) || (photo != 0)) {
				Log.i("GOI", "GOI" + "..." + att + "..." + photo);
				updataUpAttTotal(upAttTotal);
			}
		} catch (Exception e) {
			Log.i("TPATT", "向数据库中保存考勤图片数据:异常" + e.toString());
		}
	}

	/**
	 * 查询上报考勤信息
	 */
	public AttInfo findUploadAttInfo() {
		int index;
		AttInfo attInfo = null;

		try {

			AttTotal attTotal = findAttTotal();
			AttTotal upAttTotal = findUpAttTotal();

			int attaddr = Integer.parseInt(attTotal.getAttAddr());// 保存的地址
			int uploadattaddr = Integer.parseInt(upAttTotal.getUploadAttAddr());
			int attcount = Integer.parseInt(attTotal.getAtt()); //保存已考勤总数

			//if (attcount < MAX_ATT_ADDR) {
			if (attaddr >= (uploadattaddr + 1)) {
				index = uploadattaddr;
				attInfo = findByIdAttInfo(index);
			}
//			} else {
//				if (uploadattaddr != attaddr) {
//					if ((uploadattaddr + 1) >= MAX_ATT_ADDR) {
//						uploadattaddr = 0;
//					}
//					index = uploadattaddr;
//					attInfo = findByIdAttInfo(index);
//				}
//			}
			if (attInfo != null) {
				Log.i("TPATT", "查询上报考勤信息 CardId:" + attInfo.getCardId());
			}

		} catch (Exception e) {
			Log.i("TPATT", "查询上报考勤信息异常:" + e.toString());
		}

		return attInfo;
	}

	/**
	 * 查询上报考勤图片信息
	 */
	public synchronized AttInfo findUploadAttPhotoInfo() {
		int index=0;
		AttInfo attInfo = null;

		try {

			AttTotal attTotal = findAttTotal();
			AttTotal upAttTotal = findUpAttTotal();
			if (attTotal != null && upAttTotal != null) {
				int attaddr = Integer.parseInt(attTotal.getAttAddr());
				int uploadattaddr = Integer.parseInt(upAttTotal.getUploadAttPhotoAddr());
				int attcount = Integer.parseInt(attTotal.getAtt());

				//	if (attcount < MAX_ATT_ADDR) {
				if (attaddr >= (uploadattaddr + 1)) {
					index = uploadattaddr;
					attInfo = findByIdAttInfo(index);
				}
//				} else {
//					if (uploadattaddr != attaddr) {
//						if ((uploadattaddr + 1) >= MAX_ATT_ADDR) {
//							uploadattaddr = 0;
//						}
//						index = uploadattaddr;
//						attInfo = findByIdAttInfo(index);
//					}
//				}
				uploadpic=index;
				Log.i("pic index", "index:"+index+"...attaddr:"+attaddr+"....attcount:"+attcount);
				if (attInfo != null) {
					Log.i("TPATT", "==========查询需上报的考勤(CardId:" + attInfo.getCardId()+")==========");
				}
			}

		} catch (Exception e) {
			Log.i("TPATT", "查询需上报的考勤异常:" + e.toString());
		}

		return attInfo;
	}

	/**
	 * 查询上报测量信息
	 */
	public AttMeasure findUploadAttMeasure() {
		int index;
		AttMeasure attMeasure = null;
		try {
			// Log.i("TPATT", "查询上报测量信息");
			AttTotal attTotal = findAttTotal();
			AttTotal upAttTotal = findUpAttTotal();
			int measureAddr = Integer.parseInt(attTotal.getMeasureAddr());
			int uploadMeasureAddr = Integer.parseInt(upAttTotal.getUploadMeasureAddr());
			int measureCount = Integer.parseInt(attTotal.getMeasureCount());
			if (measureCount < MAX_ATT_ADDR) {
				if (measureAddr >= (uploadMeasureAddr + 1)) {
					index = uploadMeasureAddr;
					attMeasure = findByIdAttMeasure(index);
				}
			} else {
				if (uploadMeasureAddr != measureAddr) {
					if ((uploadMeasureAddr + 1) >= MAX_ATT_ADDR) {
						uploadMeasureAddr = 0;
					}
					index = uploadMeasureAddr;
					attMeasure = findByIdAttMeasure(index);
				}
			}
		} catch (Exception e) {
			Log.i("TPATT", "查询上报测量信息:异常" + e.toString());
		}
		return attMeasure;
	}

	/**
	 * 读统计信息 [0]表示是否有未上报考勤,[1]表示是否有未上报考勤图片,[2]表示已考/已报
	 */
	public synchronized String[] readAttInfo(SettingPara settingPara) {
		String[] text = new String[3];

		text[0] = null;
		text[1] = null;
		text[2] = "";
		try {
			AttTotal attTotal = findAttTotal();
			AttTotal upAttTotal = findUpAttTotal();

			int attaddr = Integer.parseInt(attTotal.getAttAddr());
			int uploadattaddr = Integer.parseInt(upAttTotal.getUploadAttAddr());
			int uploadattphotoaddr = Integer.parseInt(upAttTotal.getUploadAttPhotoAddr());
			int attcount = Integer.parseInt(attTotal.getAtt());

			// 未报考勤
			if (attcount < MAX_ATT_ADDR) {
				if (attaddr >= (uploadattaddr + 1)) {
					text[0] = "1";
				}
			} else {
				if (uploadattaddr != attaddr) {
					text[0] = "1";
				}
			}



			// 未报考勤图片
			if (attcount < MAX_ATT_ADDR) {
				if (attaddr >= (uploadattphotoaddr + 1)) {
					text[1] = "1";
				}
			} else {
				if (uploadattphotoaddr != attaddr) {
					text[1] = "1";
				}
			}

			// 已考勤/已报
			long attCount = Long.parseLong(attTotal.getAtt());
			long uploadattCount = Long.parseLong(upAttTotal.getUploadAtt());
			long attphotoCount = Long.parseLong(attTotal.getAttPhoto());
			long uploadattphotoCount = Long.parseLong(upAttTotal.getUploadAttPhoto());
			long attMeasureCount = Long.parseLong(attTotal.getMeasureCount());
			long uploadMeasureCount = Long.parseLong(upAttTotal.getUploadMeasure());
			if (uploadattCount > attCount) {
				uploadattCount = attCount;
			}
			if (uploadattphotoCount > attphotoCount) {
				uploadattphotoCount = attphotoCount;
			}
			if (uploadMeasureCount > attMeasureCount) {
				uploadMeasureCount = attMeasureCount;
			}

			//4.7修改游标偏移
			if (uploadattaddr>attcount) {
				Log.e("tapp", "考勤游标发生偏移："+uploadattaddr+"数量");
				uploadattaddr=attcount;
				upAttTotal.setUploadAttAddr(String.valueOf(uploadattphotoaddr));
			}

			if (uploadattphotoaddr>attcount) {
				Log.e("tapp", "图片游标发生偏移："+uploadattphotoaddr+"数量");
				uploadattphotoaddr=uploadattaddr;
				upAttTotal.setUploadAttPhotoAddr(String.valueOf(uploadattphotoaddr));
			}

			//	text[0]=""+(attCount-uploadattCount);
			if (attcount==attphotoCount) {
				text[2] = "考勤已刷卡/已上报：" + String.valueOf(attCount) + "/" + String.valueOf(uploadattCount) + "\n"
						+ "考勤已抓拍/已上报：" + String.valueOf(attphotoCount) + "/" + String.valueOf(uploadattphotoCount);
			}else if (attphotoCount>attcount) {

				if (uploadattphotoCount>=attphotoCount) {
					text[2] = "考勤已刷卡/已上报：" + String.valueOf(attCount) + "/" + String.valueOf(uploadattCount) + "\n"
							+ "考勤已抓拍/已上报：" + String.valueOf(attCount) + "/" + String.valueOf(attCount);
				}else  {
					text[2] = "考勤已刷卡/已上报：" + String.valueOf(attCount) + "/" + String.valueOf(uploadattCount) + "\n"
							+ "考勤已抓拍/已上报：" + String.valueOf(attCount) + "/" + String.valueOf(uploadattphotoCount);
				}

			}else {
				text[2] = "考勤已刷卡/已上报：" + String.valueOf(attCount) + "/" + String.valueOf(uploadattCount) + "\n"
						+ "考勤已抓拍/已上报：" + String.valueOf(attphotoCount) + "/" + String.valueOf(uploadattphotoCount);
			}

			if (settingPara != null && settingPara.isIsap()) {
				text[2] = text[2] + "\n" + "体温已测量/已上报：" + String.valueOf(attMeasureCount) + "/"
						+ String.valueOf(uploadMeasureCount);
			}

			if (settingPara != null &&settingPara.isIsbleban()) {

				long allnum=allCaseNumtem();
				List<AttTem> la=findByStatuInfo("1");
				if (la.size()>0) {
					text[2] = text[2] + "\n" + "体温已测量/已上报：" + (allnum-1) + "/"
							+ (la.size()-1);
				}else {
					text[2] = text[2] + "\n" + "体温已测量/已上报：" + (allnum-1) + "/"
							+ (la.size());
				}


			}


			//	int uploadattaddr = Integer.parseInt(upAttTotal.getUploadAttPhotoAddr());
			Log.i("pic index","已考勤图片数:"+attphotoCount+ "....已上报图片记录数:index:"+uploadattphotoCount+"...考勤游标attaddr:"+attaddr+"...考勤记录数attcount:"+attcount+"...图片游标："+upAttTotal.getUploadAttPhotoAddr()+"....考勤上传游标:"+uploadattaddr);

		} catch (Exception e) {
			Log.i("TPATT", "查询统计:异常" + e.toString());
		}

		return text;

	}

	/**
	 * 添加测量记录
	 */
	public void addAttMeasure(String cardid, String cardtime, String temp, String tall, String height) {
		try {
			Log.i("TPATT", "向数据库中插入测量数据");
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(ATT_MEASURE_CARDID, cardid);
			values.put(ATT_MEASURE_TEMP, temp);
			values.put(ATT_MEASURE_HEIG, height);
			values.put(ATT_MEASURE_TALL, tall);
			values.put(ATT_MEASURE_TIME, cardtime);
			db.insert(ATT_MEASURE_TABLE, null, values);
		} catch (Exception e) {
			Log.i("TPATT", "向数据库中插入测量数据:异常" + e.toString());
		}
	}

	/**
	 * 修改测量记录
	 */
	public void updataAttMeasure(int id, String cardid, String cardtime, String temp, String tall, String height) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ATT_MEASURE_CARDID, cardid);
		values.put(ATT_MEASURE_TEMP, temp);
		values.put(ATT_MEASURE_HEIG, height);
		values.put(ATT_MEASURE_TALL, tall);
		values.put(ATT_MEASURE_TIME, cardtime);
		db.update(ATT_MEASURE_TABLE, values, _ID + "=?", new String[] { String.valueOf(id + 1) });
	}

	/**
	 * 查询指定id的测量数据
	 */
	public synchronized AttMeasure findByIdAttMeasure(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + ATT_MEASURE_TABLE + " where _id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(id + 1) });
		AttMeasure attMeasure = null;
		if (cursor.moveToFirst()) {
			do {
				attMeasure = new AttMeasure();
				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String caid = cursor.getString(cursor.getColumnIndex(ATT_MEASURE_CARDID));
				String tem = cursor.getString(cursor.getColumnIndex(ATT_MEASURE_TEMP));
				String heig = cursor.getString(cursor.getColumnIndex(ATT_MEASURE_HEIG));
				String tall = cursor.getString(cursor.getColumnIndex(ATT_MEASURE_TALL));
				String time = cursor.getString(cursor.getColumnIndex(ATT_MEASURE_TIME));
				attMeasure.set_id(_id);
				attMeasure.setCaid(caid);
				attMeasure.setTem(tem);
				attMeasure.setHeig(heig);
				attMeasure.setTall(tall);
				attMeasure.setTime(time);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return attMeasure;
	}

	/**
	 * 添加考勤记录
	 */
	public synchronized  void addAttInfo(String cardid, String dtime, String status, String tranid, String photo) {
		try {

			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(ATTINFO_CARDID, cardid);
			values.put(ATTINFO_DTIME, dtime);
			values.put(ATTINFO_STATUS, status);
			values.put(ATTINFO_TRANSACTIONID, tranid);
			values.put(ATTINFO_PHOTO, photo);

			db.insert(ATTINFO_TABLE, null, values);

			Log.i("TPATT", "数据库addAttInfo cardid:" + cardid);
		} catch (Exception e) {
			Log.i("TPATT", "数据库addAttInfo:异常" + e.toString());
		}
	}

	/**
	 * 删除考勤记录
	 */
	public void deleteAttInfo(int id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(ATTINFO_TABLE, _ID + "=?", new String[] { String.valueOf(id + 1) });
	}

	/**
	 * 修改考勤记录
	 */
	public void updataAttInfo(int id, String cardid, String dtime, String status, String tranid, String photo) {

		try{
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(ATTINFO_CARDID, cardid);
			values.put(ATTINFO_DTIME, dtime);
			values.put(ATTINFO_STATUS, status);
			values.put(ATTINFO_TRANSACTIONID, tranid);
			values.put(ATTINFO_PHOTO, photo);

			db.update(ATTINFO_TABLE, values, _ID + "=?", new String[] { String.valueOf(id + 1) });

			Log.i("TPATT", "数据库updataAttInfo cardid:" + cardid);
		}catch (Exception e) {
			Log.i("TPATT", "数据库updataAttInfo异常:" + e.toString());
		}
	}


	/*
	 * 根据日期查询考勤信息
	 *
	 *
	 */

	public synchronized List<AttInfo> findDateInfo(String date){

		List<AttInfo> li=new ArrayList<DBOpenHelper.AttInfo>();

		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + ATTINFO_TABLE + " where " + ATTINFO_DTIME +  " like and "+ATTINFO_STATUS +" like ?";

		Cursor cursor = db.rawQuery(sql, new String[] { '%'+date+'%' });

		AttInfo attInfo = null;
		if (cursor.moveToFirst()) {
			attInfo = new AttInfo();
			int _id = cursor.getInt(cursor.getColumnIndex(_ID));
			String cardid = cursor.getString(cursor.getColumnIndex(ATTINFO_CARDID));
			String dtime = cursor.getString(cursor.getColumnIndex(ATTINFO_DTIME));
			String status = cursor.getString(cursor.getColumnIndex(ATTINFO_STATUS));
			String tranid = cursor.getString(cursor.getColumnIndex(ATTINFO_TRANSACTIONID));
			String photo = cursor.getString(cursor.getColumnIndex(ATTINFO_PHOTO));
			attInfo.setId(_id);
			attInfo.setCardId(cardid);
			attInfo.setDtime(dtime);
			attInfo.setStatus(status);
			attInfo.setTranid(tranid);
			attInfo.setPhoto(photo);
			li.add(attInfo);
		}
		cursor.close();




		return li;

	}






	/**
	 * 查询指定卡id的考勤记录
	 */
	public synchronized AttInfo findByCardIdAttInfo(String cardId) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + ATTINFO_TABLE + " where " + ATTINFO_CARDID + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { cardId });
		AttInfo attInfo = null;
		if (cursor.moveToLast()) {
			attInfo = new AttInfo();
			int _id = cursor.getInt(cursor.getColumnIndex(_ID));
			String cardid = cursor.getString(cursor.getColumnIndex(ATTINFO_CARDID));
			String dtime = cursor.getString(cursor.getColumnIndex(ATTINFO_DTIME));
			String status = cursor.getString(cursor.getColumnIndex(ATTINFO_STATUS));
			String tranid = cursor.getString(cursor.getColumnIndex(ATTINFO_TRANSACTIONID));
			String photo = cursor.getString(cursor.getColumnIndex(ATTINFO_PHOTO));
			attInfo.setId(_id);
			attInfo.setCardId(cardid);
			attInfo.setDtime(dtime);
			attInfo.setStatus(status);
			attInfo.setTranid(tranid);
			attInfo.setPhoto(photo);
		}
		cursor.close();

		return attInfo;
	}

	/**
	 * 查询指定卡id的考勤记录时间
	 */
	public synchronized String findByCardIdAttInfoTime(String cardId) {
		String dtime = null;
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + ATTINFO_TABLE + " where " + ATTINFO_CARDID + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { cardId });
		try {
			if (cursor.moveToLast()) {
				dtime = cursor.getString(cursor.getColumnIndex(ATTINFO_DTIME));
			}
			cursor.close();
		} catch (Exception e) {
			cursor.close();
			Log.i("TPATT", "查询指定卡id的考勤记录时间出错：" + e.toString());
		}
		return dtime;
	}

	/**
	 * 查询指定id的考勤记录
	 */
	public synchronized AttInfo findByIdAttInfo(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + ATTINFO_TABLE + " where _id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(id + 1) });
		AttInfo attInfo = null;
		if (cursor.moveToFirst()) {
			do {
				attInfo = new AttInfo();
				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String cardid = cursor.getString(cursor.getColumnIndex(ATTINFO_CARDID));
				String dtime = cursor.getString(cursor.getColumnIndex(ATTINFO_DTIME));
				String status = cursor.getString(cursor.getColumnIndex(ATTINFO_STATUS));
				String tranid = cursor.getString(cursor.getColumnIndex(ATTINFO_TRANSACTIONID));
				String photo = cursor.getString(cursor.getColumnIndex(ATTINFO_PHOTO));
				attInfo.setId(_id);
				attInfo.setCardId(cardid);
				attInfo.setDtime(dtime);
				attInfo.setStatus(status);
				attInfo.setTranid(tranid);
				attInfo.setPhoto(photo);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return attInfo;
	}

	/**
	 * 修改指定id的数据
	 */
	public void updataAttTotal(AttTotal attTotal) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ATTTOTAL_ATT, attTotal.getAtt());
		values.put(ATTTOTAL_ATTPH, attTotal.getAttPhoto());
		values.put(ATTTOTAL_ATTADDR, attTotal.getAttAddr());
		values.put(ATTTOTAL_ATT_MEASURE, attTotal.getMeasureCount());
		values.put(ATTTOTAL_ATT_MEASUREADDR, attTotal.getMeasureAddr());

		db.update(ATTTOTAL_TABLE, values, _ID + "=?", new String[] { String.valueOf(1) });
	}

	/**
	 * 修改已报指定id的数据
	 */
	public void updataUpAttTotal(AttTotal upAttTotal) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ATTTOTAL_UPATT, upAttTotal.getUploadAtt());
		values.put(ATTTOTAL_UPATTPH, upAttTotal.getUploadAttPhoto());
		values.put(ATTTOTAL_UPATTADDR, upAttTotal.getUploadAttAddr());
		values.put(ATTTOTAL_UPATTPHADDR, upAttTotal.getUploadAttPhotoAddr());
		values.put(ATTTOTAL_UP_MEASURE, upAttTotal.getUploadMeasure());
		values.put(ATTTOTAL_UP_MEASUREADDR, upAttTotal.getUploadMeasureAddr());

		db.update(UPATTTOTAL_TABLE, values, _ID + "=?", new String[] { String.valueOf(1) });
	}


	/**
	 * 修改已报体温指定id的数据
	 */
	public void updataTemInfo(AttTem atttem) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TEM_CARD, atttem.getTcard());
		values.put(TEM_STATU, atttem.getTstatu());
		values.put(TEM_TEM, atttem.getTem());
		values.put(TEM_TIME, atttem.getTtime());


		db.update(TEMPERATURE_TABLE, values, _ID + "=?", new String[] { String.valueOf(atttem.get_id()) });
	}

	/**
	 * 查询指定id的体温记录
	 */
	public synchronized AttTem findByIdTemInfo(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + TEMPERATURE_TABLE + " where _id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(id ) });
		AttTem attInfo = null;
		if (cursor.moveToFirst()) {
			do {
				attInfo = new AttTem();
				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String cardid = cursor.getString(cursor.getColumnIndex(TEM_CARD));
				String dtime = cursor.getString(cursor.getColumnIndex(TEM_TIME));
				String status = cursor.getString(cursor.getColumnIndex(TEM_STATU));
				String tem = cursor.getString(cursor.getColumnIndex(TEM_TEM));

				attInfo.set_id(_id);
				attInfo.setTcard(cardid);
				attInfo.setTtime(dtime);
				attInfo.setTstatu(status);
				attInfo.setTem(tem);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return attInfo;
	}

	public List<AttInfo>  finddateinfos(String starttime){

		String find="select * from attinfo  where dtime like ?";

		SQLiteDatabase db = this.getReadableDatabase();
		//   String sql = "select * from attinfo where _id=?";
		Cursor cursor = db.rawQuery(find,new String[]{'%'+starttime+'%'});
		List<AttInfo> attInfos = new ArrayList<DBOpenHelper.AttInfo>();
		if ( cursor.moveToFirst() )
		{
			do
			{
				AttInfo attInfo = new AttInfo();
				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String cardid = cursor.getString(cursor.getColumnIndex(ATTINFO_CARDID));
				String dtime = cursor.getString(cursor.getColumnIndex(ATTINFO_DTIME));
				String status = cursor.getString(cursor.getColumnIndex(ATTINFO_STATUS));
				String tranid = cursor.getString(cursor.getColumnIndex(ATTINFO_TRANSACTIONID));
				String photo = cursor.getString(cursor.getColumnIndex(ATTINFO_PHOTO));
//		            String attad = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UPATT));
//		            String attadup = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UPATTADDR));




				attInfo.setId(_id);
				attInfo.setCardId(cardid);
				attInfo.setDtime(dtime);
				attInfo.setStatus(status);
				attInfo.setTranid(tranid);
				attInfo.setPhoto(photo);

				attInfos.add(attInfo);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return attInfos;




	}

	//查找某天不重复的刷卡记录次数
	public List<AttInfo>  finddategrou(String starttime){

		String find="select cardid from attinfo  where dtime like ? group by cardid";

		SQLiteDatabase db = this.getReadableDatabase();
		//   String sql = "select * from attinfo where _id=?";
		Cursor cursor = db.rawQuery(find,new String[]{'%'+starttime+'%'});
		List<AttInfo> attInfos = new ArrayList<DBOpenHelper.AttInfo>();
		if ( cursor.moveToFirst() )
		{
			do
			{
				AttInfo attInfo = new AttInfo();
				//        int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String cardid = cursor.getString(cursor.getColumnIndex(ATTINFO_CARDID));
//		            String dtime = cursor.getString(cursor.getColumnIndex(ATTINFO_DTIME));
//		            String status = cursor.getString(cursor.getColumnIndex(ATTINFO_STATUS));
//		            String tranid = cursor.getString(cursor.getColumnIndex(ATTINFO_TRANSACTIONID));
//		            String photo = cursor.getString(cursor.getColumnIndex(ATTINFO_PHOTO));
//		            String attad = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UPATT));
//		            String attadup = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UPATTADDR));




				//      attInfo.setId(_id);
				attInfo.setCardId(cardid);
//		            attInfo.setDtime(dtime);
//		            attInfo.setStatus(status);
//		            attInfo.setTranid(tranid);
//		            attInfo.setPhoto(photo);

				attInfos.add(attInfo);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return attInfos;




	}


	/*
	 * 查找某天的体温数据
	 *
	 *
	 */

	public List<AttTem>  finddateinfo(String starttime){

		String find="select * from temtotal  where ttime like ?";

		SQLiteDatabase db = this.getReadableDatabase();
		//   String sql = "select * from attinfo where _id=?";
		Cursor cursor = db.rawQuery(find,new String[]{'%'+starttime+'%'});
		List<AttTem> attInfos = new ArrayList<AttTem>();
		if ( cursor.moveToFirst() )
		{
			do
			{
				AttTem attInfo = new AttTem();
				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String cardid = cursor.getString(cursor.getColumnIndex(TEM_CARD));
				String dtime = cursor.getString(cursor.getColumnIndex(TEM_TIME));
				String status = cursor.getString(cursor.getColumnIndex(TEM_STATU));

				String tem = cursor.getString(cursor.getColumnIndex(TEM_TEM));

				attInfo.set_id(_id);
				attInfo.setTcard(cardid);
				attInfo.setTtime(dtime);
				attInfo.setTstatu(status);

				attInfo.setTem(tem);

				attInfos.add(attInfo);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return attInfos;


	}


	/**
	 * 查询指定状态的体温考勤记录
	 */
	public synchronized List<AttTem> findByStatuInfo(String status) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + TEMPERATURE_TABLE + " where " + TEM_STATU + " like ? ";
		Cursor cursor = db.rawQuery(sql, new String[] { status });
		List<AttTem> la=new ArrayList<DBOpenHelper.AttTem>();
		AttTem attInfo = null;
		if (cursor.moveToFirst()) {

			do {


				attInfo = new AttTem();
				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String cardid = cursor.getString(cursor.getColumnIndex(TEM_CARD));
				String dtime = cursor.getString(cursor.getColumnIndex(TEM_TIME));
				String statu = cursor.getString(cursor.getColumnIndex(TEM_STATU));
				String tem = cursor.getString(cursor.getColumnIndex(TEM_TEM));

				attInfo.set_id(_id);
				attInfo.setTcard(cardid);
				attInfo.setTtime(dtime);
				attInfo.setTstatu(statu);
				attInfo.setTem(tem);
				la.add(attInfo);
			} while (cursor.moveToNext());

		}
		cursor.close();

		return la;
	}



	/**
	 * 查询指定日期和状态的体温考勤记录
	 */
	public synchronized List<AttTem> findByTWInfo(String status,String day) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + TEMPERATURE_TABLE + " where " + TEM_STATU + " like ? and "+TEM_TIME +" like ?";
		Cursor cursor = db.rawQuery(sql, new String[] { '%'+status+'%','%'+day+'%' });
		List<AttTem> la=new ArrayList<DBOpenHelper.AttTem>();
		AttTem attInfo = null;
		if (cursor.moveToFirst()) {

			do {


				attInfo = new AttTem();
				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String cardid = cursor.getString(cursor.getColumnIndex(TEM_CARD));
				String dtime = cursor.getString(cursor.getColumnIndex(TEM_TIME));
				String statu = cursor.getString(cursor.getColumnIndex(TEM_STATU));
				String tem = cursor.getString(cursor.getColumnIndex(TEM_TEM));

				attInfo.set_id(_id);
				attInfo.setTcard(cardid);
				attInfo.setTtime(dtime);
				attInfo.setTstatu(statu);
				attInfo.setTem(tem);
				la.add(attInfo);
			} while (cursor.moveToNext());

		}
		cursor.close();

		return la;
	}


	/**
	 * 查询指定以前日期和状态的体温考勤记录
	 */
	public synchronized List<AttTem> findByBefoceTWInfo(String status,String day) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + TEMPERATURE_TABLE + " where " + TEM_STATU + " like ? and "+TEM_TIME +" < ?";
		Cursor cursor = db.rawQuery(sql, new String[] { '%'+status+'%',day });
		List<AttTem> la=new ArrayList<DBOpenHelper.AttTem>();
		AttTem attInfo = null;
		if (cursor.moveToFirst()) {

			do {


				attInfo = new AttTem();
				int _id = cursor.getInt(cursor.getColumnIndex(_ID));
				String cardid = cursor.getString(cursor.getColumnIndex(TEM_CARD));
				String dtime = cursor.getString(cursor.getColumnIndex(TEM_TIME));
				String statu = cursor.getString(cursor.getColumnIndex(TEM_STATU));
				String tem = cursor.getString(cursor.getColumnIndex(TEM_TEM));

				attInfo.set_id(_id);
				attInfo.setTcard(cardid);
				attInfo.setTtime(dtime);
				attInfo.setTstatu(statu);
				attInfo.setTem(tem);
				la.add(attInfo);
			} while (cursor.moveToNext());

		}
		cursor.close();

		return la;
	}




	/**
	 * 查询考勤统计信息
	 */
	public synchronized AttTotal findAttTotal() {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from atttotal where _id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(1) });
		AttTotal attTotal = new AttTotal();

		if (cursor.moveToFirst()) {// TODO 可能出现CursorWindowAllocationException
			int _id = cursor.getInt(cursor.getColumnIndex(_ID));
			String att = cursor.getString(cursor.getColumnIndex(ATTTOTAL_ATT));
			String attphoto = cursor.getString(cursor.getColumnIndex(ATTTOTAL_ATTPH));
			String attaddr = cursor.getString(cursor.getColumnIndex(ATTTOTAL_ATTADDR));
			String measure = cursor.getString(cursor.getColumnIndex(ATTTOTAL_ATT_MEASURE));
			String measureaddr = cursor.getString(cursor.getColumnIndex(ATTTOTAL_ATT_MEASUREADDR));

			attTotal.setId(_id);
			attTotal.setAtt(att);
			attTotal.setAttPhoto(attphoto);
			attTotal.setAttAddr(attaddr);
			attTotal.setMeasure(measure);
			attTotal.setMeasureAddr(measureaddr);

			// Log.i("TPATT-TOTAL", "查询统计:" + att +","+ attphoto +","+ uploadatt
			// +","+ uploadattphoto +","+ attaddr +","+ uploadattaddr +","+
			// uploadattphotoaddr +",");
		} else {
			Log.i("TPATT", "查询统计:表内容为空");
		}
		cursor.close();

		return attTotal;
	}

	/**
	 * 查询已报考勤统计信息
	 */
	public synchronized AttTotal findUpAttTotal() {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from upatttotal where _id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(1) });
		AttTotal attTotal = new AttTotal();
		if (cursor.moveToFirst()) {
			attTotal = new AttTotal();
			@SuppressWarnings("unused")
			int _id = cursor.getInt(cursor.getColumnIndex(_ID));
			String uploadatt = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UPATT));
			String uploadattphoto = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UPATTPH));
			String uploadattaddr = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UPATTADDR));
			String uploadattphotoaddr = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UPATTPHADDR));
			String uploadmeasure = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UP_MEASURE));
			String uploadmeasureaddr = cursor.getString(cursor.getColumnIndex(ATTTOTAL_UP_MEASUREADDR));

			attTotal.setUploadAtt(uploadatt);
			attTotal.setUploadAttPhoto(uploadattphoto);
			attTotal.setUploadAttAddr(uploadattaddr);
			attTotal.setUploadAttPhotoAddr(uploadattphotoaddr);
			attTotal.setUploadMeasure(uploadmeasure);
			attTotal.setUploadMeasureAddr(uploadmeasureaddr);

		} else {
			Log.i("TPATT", "查询已报统计:表内容为空");
		}
		cursor.close();

		return attTotal;
	}

	public class AttMeasure {
		private int _id;
		private String caid;
		private String tem;
		private String heig;
		private String tall;
		private String time;

		public AttMeasure() {
		}

		public int get_id() {
			return _id;
		}

		public void set_id(int _id) {
			this._id = _id;
		}

		public String getCaid() {
			return caid;
		}

		public void setCaid(String caid) {
			this.caid = caid;
		}

		public String getTem() {
			return tem;
		}

		public void setTem(String tem) {
			this.tem = tem;
		}

		public String getHeig() {
			return heig;
		}

		public void setHeig(String heig) {
			this.heig = heig;
		}

		public String getTall() {
			return tall;
		}

		public void setTall(String tall) {
			this.tall = tall;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

	}

	public class AttInfo {
		private int _id;
		private String cardid;
		private String dtime;
		private String status;
		private String tranid;
		private String photo;

		public int getId() {
			return _id;
		}

		public void setId(int _id) {
			this._id = _id;
		}

		public String getCardId() {
			return cardid;
		}

		public void setCardId(String cid) {
			this.cardid = cid;
		}

		public String getDtime() {
			return dtime;
		}

		public void setDtime(String time) {
			this.dtime = time;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String st) {
			this.status = st;
		}

		public String getTranid() {
			return tranid;
		}

		public void setTranid(String tid) {
			this.tranid = tid;
		}

		public String getPhoto() {
			return photo;
		}

		public void setPhoto(String photo) {
			this.photo = photo;
		}

		@Override
		public String toString() {
			return "AttInfo [id=" + _id + ", cardid=" + cardid + ", dtime=" + dtime + ", status=" + status
					+ ", transactionid=" + tranid + "]";
		}

		public AttInfo() {
			super();
		}

		public AttInfo(int _id, String cardid, String dtime, String status, String tranid, String photo) {
			super();

			this._id = _id;
			this.cardid = cardid;
			this.dtime = dtime;
			this.status = status;
			this.tranid = tranid;
			this.photo = photo;
		}
	}

	public class AttTotal {
		private int _id = 0;
		private String att = "0";
		private String attaddr = "0";
		private String attphoto = "0";

		private String measureCount = "0";
		private String measureaddr = "0";

		private String uploadatt = "0";
		private String uploadattaddr = "0";

		private String uploadattphoto = "0";
		private String uploadattphotoaddr = "0";

		private String uploadmeasure = "0";
		private String uploadmeasureaddr = "0";

		public int getId() {
			return _id;
		}

		public void setId(int id) {
			this._id = id;
		}

		public String getAtt() {
			return att;
		}

		public void setAtt(String att) {
			this.att = att;
		}

		public String getAttPhoto() {
			return attphoto;
		}

		public void setAttPhoto(String attphoto) {
			// Log.i("csv", "保存数量是:"+attphoto);
			this.attphoto = attphoto;
		}

		public String getUploadAtt() {
			return uploadatt;
		}

		public void setUploadAtt(String uploadatt) {
			this.uploadatt = uploadatt;
		}

		public String getUploadAttPhoto() {
			return uploadattphoto;
		}

		public void setUploadAttPhoto(String uploadattphoto) {
			// Log.i("csv", "上报数量是:"+uploadattphoto);
			this.uploadattphoto = uploadattphoto;
		}

		public String getAttAddr() {
			return attaddr;
		}

		public void setAttAddr(String attaddr) {
			this.attaddr = attaddr;
		}

		public String getUploadAttAddr() {
			return uploadattaddr;
		}

		public void setUploadAttAddr(String uploadattaddr) {
			this.uploadattaddr = uploadattaddr;
		}

		public String getUploadAttPhotoAddr() {
			return uploadattphotoaddr;
		}

		public void setUploadAttPhotoAddr(String uploadattphotoaddr) {
			// Log.i("csv", "上传图片地址数量是:"+uploadattphoto);
			this.uploadattphotoaddr = uploadattphotoaddr;
		}

		public AttTotal() {
			super();
		}

		public AttTotal(int _id, String att, String attphoto, String uploadatt, String uploadattphoto, String attaddr,
						String uploadattaddr, String uploadattphotoaddr) {
			super();

			this._id = _id;
			this.att = att;
			this.attphoto = attphoto;
			this.uploadatt = uploadatt;
			this.uploadattphoto = uploadattphoto;
			this.attaddr = attaddr;
			this.uploadattaddr = uploadattaddr;
			this.uploadattphotoaddr = uploadattphotoaddr;
		}

		public String getUploadMeasure() {
			return uploadmeasure;
		}

		public void setUploadMeasure(String uploadmeasure) {
			this.uploadmeasure = uploadmeasure;
		}

		public String getMeasureCount() {
			return measureCount;
		}

		public void setMeasure(String measure) {
			this.measureCount = measure;
		}

		public String getMeasureAddr() {
			return measureaddr;
		}

		public void setMeasureAddr(String measureaddr) {
			this.measureaddr = measureaddr;
		}

		public String getUploadMeasureAddr() {
			return uploadmeasureaddr;
		}

		public void setUploadMeasureAddr(String uploadmeasureaddr) {
			this.uploadmeasureaddr = uploadmeasureaddr;
		}
	}



	public class AttTem{

		private int _id = 0;
		private String tem="0";
		private String ttime="0";
		private String tstatu="0";
		private String tcard="0";


		public int get_id() {
			return _id;
		}
		public void set_id(int _id) {
			this._id = _id;
		}
		public String getTem() {
			return tem;
		}
		public void setTem(String tem) {
			this.tem = tem;
		}
		public String getTtime() {
			return ttime;
		}
		public void setTtime(String ttime) {
			this.ttime = ttime;
		}
		public String getTstatu() {
			return tstatu;
		}
		public void setTstatu(String tstatu) {
			this.tstatu = tstatu;
		}
		public String getTcard() {
			return tcard;
		}
		public void setTcard(String tcard) {
			this.tcard = tcard;
		}



		@Override
		public String toString() {
			return "Teminfo [id=" + _id + ", cardid=" + tcard + ", ttime=" + ttime + ", status=" + tstatu
					+ ",tem="+tem+"]";
		}

		public AttTem() {
			super();
		}

		public AttTem(int _id, String cardid, String dtime, String status,  String tem) {
			super();

			this._id = _id;
			this.tcard = cardid;
			this.ttime = dtime;
			this.tstatu = status;

			this.tem = tem;
		}






	}



	public class AttupInfo
	{
		private int    _id;
		private String cardid;
		private String dtime;
		private String status;
		private String tranid;
		private String photo;
		private String uploadatt;
		private String attaddr;

		public int getId()
		{
			return _id;
		}

		public void setId(int _id)
		{
			this._id = _id;
		}

		public String getCardId()
		{
			return cardid;
		}

		public void setCardId(String cid)
		{
			this.cardid = cid;
		}

		public String getDtime()
		{
			return dtime;
		}

		public void setDtime(String time)
		{
			this.dtime = time;
		}

		public String getStatus()
		{
			return status;
		}

		public void setStatus(String st)
		{
			this.status = st;
		}

		public String getTranid()
		{
			return tranid;
		}

		public void setTranid(String tid)
		{
			this.tranid = tid;
		}

		public String getPhoto()
		{
			return photo;
		}

		public void setPhoto(String photo)
		{
			this.photo = photo;
		}

		@Override
		public String toString()
		{
			return "AttInfo [id=" + _id + ", cardid=" + cardid + ", dtime=" + dtime + ", status=" + status + ", transactionid=" + tranid + "]";
		}

		public AttupInfo()
		{
			super();
		}

		public AttupInfo(int _id, String cardid,String dtime,String status,String tranid,String photo)
		{
			super();

			this._id = _id;
			this.cardid = cardid;
			this.dtime = dtime;
			this.status = status;
			this.tranid = tranid;
			this.photo = photo;
		}
	}







//	private static boolean mainTmpDirSet = false;

//	@Override
//	public SQLiteDatabase getReadableDatabase() {
//		if (!mainTmpDirSet) {
//			@SuppressWarnings("unused")
//			boolean rs = new File("/data/data/com.att/databases/main").mkdir();
//			// Log.d("spl", rs + "");
//			super.getReadableDatabase().execSQL("PRAGMA temp_store_directory = '/data/data/com.att/databases/main'");
//			mainTmpDirSet = true;
//			return super.getReadableDatabase();
//		}
//		return super.getReadableDatabase();
//	}
}
