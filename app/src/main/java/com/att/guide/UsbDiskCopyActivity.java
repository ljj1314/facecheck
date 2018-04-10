package com.att.guide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.att.AppBaseFun;
import com.att.R;
import com.att.SettingPara;
import com.att.SettingParaActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UsbDiskCopyActivity extends Activity {

	private ImageView back_button;
	public AppBaseFun appBaseFun = new AppBaseFun();
	private Handler didplayTimer = new Handler();

	private LinearLayout ll_copy_names;
	//	private LinearLayout ll_names;
	private TextView tv_list;
	// private TextView tv_item_name;
	private Button bt_copy;

	private LinearLayout ll_loading;
	private TextView tv_loading;
	private ImageView iv_loading_anim;
	private AnimationDrawable anim;
	private Button next_step;

	boolean isCopying = false;
	private ArrayList<File> files=null;
	private ListView lv=null;
	private SettingPara sp=new SettingPara();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_usb_disk_copy);
		init();
	}

	private void init() {

		back_button = (ImageView) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		ll_copy_names = (LinearLayout) findViewById(R.id.ll_copy_names);
//		ll_names = (LinearLayout) findViewById(R.id.ll_names);
		tv_list = (TextView) findViewById(R.id.tv_list);
		// tv_item_name = (TextView) findViewById(R.id.tv_item_name);
		bt_copy = (Button) findViewById(R.id.bt_copy);

		lv=(ListView) findViewById(R.id.scrollView1);

		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		iv_loading_anim = (ImageView) findViewById(R.id.iv_loading_anim);
		iv_loading_anim.setBackgroundResource(R.anim.loading_anim);// 设置动画背景
		anim = (AnimationDrawable) iv_loading_anim.getBackground();// 获得动画对象
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		next_step = (Button) findViewById(R.id.next_step);
		next_step.setVisibility(View.GONE);

		didplayTimer.postDelayed(checkUsbDiskTimer, 2000);
	}

	Runnable checkUsbDiskTimer = new Runnable() {
		public void run() {
			long delayMillis = 2000;
			try {
				if (!isCopying) {
					checkUsbDisk();
				}
				didplayTimer.postDelayed(checkUsbDiskTimer, delayMillis);
			} catch (Exception e) {
				didplayTimer.postDelayed(checkUsbDiskTimer, delayMillis);
			}
		}
	};

	private void checkUsbDisk() {
		final List<String> paths = appBaseFun.getUsbPaths();
		if (paths != null && paths.size() > 0) {
			File filePhoto = new File(paths.get(0) + "/轮播图片");
			File fileVideo = new File(paths.get(0) + "/轮播视频");
			if ((filePhoto != null && filePhoto.exists()) || (fileVideo != null && fileVideo.exists())) {
				File[] photofiles = filePhoto.listFiles();
				File[] videofiles = fileVideo.listFiles();
				files = new ArrayList<File>();
				for (int i = 0; photofiles != null && i < photofiles.length; i++) {
					if (photofiles[i].getName().endsWith(".jpg") || photofiles[i].getName().endsWith(".JPG")
							|| photofiles[i].getName().endsWith(".png") || photofiles[i].getName().endsWith(".PNG")) {
						files.add(photofiles[i]);
					}
				}
				for (int i = 0; videofiles != null && i < videofiles.length; i++) {
					if (videofiles[i].getName().endsWith(".mp4") || videofiles[i].getName().endsWith(".MP4")
							|| videofiles[i].getName().endsWith(".avi") || videofiles[i].getName().endsWith(".AVI")) {
						files.add(videofiles[i]);
					}
				}


				if (files.size()>0) {

					if (appBaseFun.getSDPath()==null) {
						long size1=appBaseFun.getAutoFileOrFilesSize(paths.get(0) + "/轮播图片");
						long size2=appBaseFun.getAutoFileOrFilesSize(paths.get(0) + "/轮播视频");
						long size3=appBaseFun.getAutoFileOrFilesSize(appBaseFun.getPhoneCardPath() + "/tpatt/PlayPhoto");
						long size4=appBaseFun.getAutoFileOrFilesSize(appBaseFun.getPhoneCardPath() + "/tpatt/tp");


						if ((size3+size4)>(1024*1024*1024*Integer.valueOf(sp.getVolume()))) {
							Toast.makeText(UsbDiskCopyActivity.this, "本地轮播的内容超过"+Integer.valueOf(sp.getVolume())+"g,请删除后再重新操作", Toast.LENGTH_SHORT).show();
							return;
						}


						if ((size1+size2)>(1024*1024*1024*Integer.valueOf(sp.getVolume()))) {

							Toast.makeText(UsbDiskCopyActivity.this, "选择的内容超过"+Integer.valueOf(sp.getVolume())+"g,请删除后再重新操作", Toast.LENGTH_SHORT).show();
							return;
						}

						if ((size1+size2+size3+size4)>(1024*1024*1024*Integer.valueOf(sp.getVolume()))) {

							Toast.makeText(UsbDiskCopyActivity.this, "选择的内容超过"+Integer.valueOf(sp.getVolume())+"g,请删除后再重新操作", Toast.LENGTH_SHORT).show();
							return;

						}
					}



				}


				if (files.size() > 0) {
					ll_loading.setVisibility(View.GONE);
					ll_copy_names.setVisibility(View.VISIBLE);
					tv_list.setText("搜索到有" + files.size() + "个可用资源文件:");
//					ll_names.removeAllViews();
//					for (int i = 0; i < files.size(); i++) {
//						// if(i==0){
//						// tv_item_name.setText(files.get(i).getName());
//						// }else{
//						TextView item = new TextView(getApplicationContext());
//						LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//								LayoutParams.WRAP_CONTENT);
//						layoutParam.setMargins(0, 10, 0, 0);
//						item.setLayoutParams(layoutParam);
//						item.setBackgroundResource(R.drawable.frame_gray);
//						item.setPadding(10, 10, 10, 10);
//						item.setTextSize(23);
//						item.setTextColor(getResources().getColor(R.color.black_333333));
//						item.setText(files.get(i).getName());
//						ll_names.addView(item);
//						// }
//					}


					lv.setAdapter(new Listadapter());


					bt_copy.setVisibility(View.VISIBLE);
					bt_copy.setEnabled(true);
					bt_copy.setText("复制");
					bt_copy.setFocusable(true);
					bt_copy.requestFocus();
					bt_copy.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							if (appBaseFun.getSDPath()==null) {
								copyUsbFile(paths.get(0) + "/轮播图片", appBaseFun.getPhoneCardPath() + "/tpatt/PlayPhoto/",
										paths.get(0) + "/轮播视频", appBaseFun.getPhoneCardPath() + "/tpatt/tp");
							}else {
								copyUsbFile(paths.get(0) + "/轮播图片", appBaseFun.getSDPath() + "/tpatt/PlayPhoto/",
										paths.get(0) + "/轮播视频", appBaseFun.getSDPath() + "/tp");
							}

						}
					});
					//copyUsbFile(paths.get(0) + "/轮播图片", appBaseFun.getSDPath() + "/tpatt/PlayPhoto/",
					//	 paths.get(0) + "/轮播视频", appBaseFun.getSDPath() + "/tp");
				} else {
					ll_copy_names.setVisibility(View.GONE);
					ll_loading.setVisibility(View.VISIBLE);
					iv_loading_anim.setVisibility(View.GONE);
					tv_loading.setText("“轮播图片”与“轮播视频”文件夹都没有可用文件");
					next_step.setVisibility(View.GONE);
				}
			} else {
				ll_copy_names.setVisibility(View.GONE);
				ll_loading.setVisibility(View.VISIBLE);
				iv_loading_anim.setVisibility(View.GONE);
				tv_loading.setText("“轮播图片”与“轮播视频”文件夹不存在");
				;
				next_step.setVisibility(View.GONE);
			}
		} else {
			ll_copy_names.setVisibility(View.GONE);
			ll_loading.setVisibility(View.VISIBLE);
			iv_loading_anim.setVisibility(View.GONE);
			tv_loading.setText("请插入U盘");
			next_step.setVisibility(View.GONE);
		}
		// iv_pic.setImageBitmap(OKHttpUtils.getImage(files[0].getAbsolutePath()));
	}


	private class Listadapter extends BaseAdapter{

		public int getCount() {
			// TODO Auto-generated method stub
			return files.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			convertView=View.inflate(UsbDiskCopyActivity.this, R.layout.itemview, null);

			TextView tv=(TextView) convertView.findViewById(R.id.tv);

			tv.setText(files.get(position).getName());



			return convertView;
		}




	}




	private void copyUsbFile(final String srcDirName, final String destDirName, final String srcDirName2,
							 final String destDirName2) {
		isCopying = true;
		ll_copy_names.setVisibility(View.GONE);
		ll_loading.setVisibility(View.VISIBLE);
		tv_loading.setText("文件复制中");
		next_step.setVisibility(View.GONE);
		iv_loading_anim.setVisibility(View.VISIBLE);
		anim.setOneShot(false);// 是否仅仅启动一次？
		if (anim.isRunning()) {// 是否正在运行？
			anim.stop();// 停止
		}
		anim.start();// 启动
		AsyncTask<String, Integer, Boolean> asyncTask = new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(String... arg0) {
				boolean ok = copyFolder(srcDirName, destDirName) && copyFolder(srcDirName2, destDirName2);
				File srcDir = new File(srcDirName);
				if (!srcDir.exists()) {
					srcDir.mkdirs();
				}
				return ok;
			}

			protected void onPostExecute(Boolean result) {
				if (result) {
					tv_loading.setText("轮播资源复制成功");
					iv_loading_anim.setVisibility(View.GONE);
					next_step.setVisibility(View.VISIBLE);
					next_step.setEnabled(true);
					next_step.setText("完成");
					next_step.setFocusable(true);
					next_step.requestFocus();
					next_step.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							isCopying = false;
							onBackPressed();
						}
					});
				} else {
					tv_loading.setText("轮播资源复制失败");
					iv_loading_anim.setVisibility(View.GONE);
					next_step.setVisibility(View.VISIBLE);
					next_step.setEnabled(true);
					next_step.setText("重试");
					next_step.setFocusable(true);
					next_step.requestFocus();
					next_step.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							isCopying = false;
							checkUsbDisk();
						}
					});
				}
			};
		};
		asyncTask.execute("");
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(UsbDiskCopyActivity.this, SettingParaActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.back_left_in, R.anim.back_right_out);
		super.onBackPressed();
	}

	/**
	 * 移动文件
	 *
	 * @param srcFileName
	 *            源文件完整路径
	 * @param destDirName
	 *            目的目录完整路径
	 * @return 文件移动成功返回true，否则返回false
	 */
	public boolean moveFile(String srcFileName, String destDirName) {

		File srcFile = new File(srcFileName);
		if (!srcFile.exists() || !srcFile.isFile())
			return false;

		File destDir = new File(destDirName);
		if (!destDir.exists())
			destDir.mkdirs();

		return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
		// renameTo目录要同级别,这个很关键,你交换的两个文件夹要有相同的层数.
	}

	/**
	 * 移动目录
	 *
	 * @param srcDirName
	 *            源目录完整路径
	 * @param destDirName
	 *            目的目录完整路径
	 * @return 目录移动成功返回true，否则返回false
	 */
	public boolean moveDirectory(String srcDirName, String destDirName) {

		File srcDir = new File(srcDirName);
		if (!srcDir.exists() || !srcDir.isDirectory())
			return false;

		File destDir = new File(destDirName);
		if (!destDir.exists())
			destDir.mkdirs();

		/**
		 * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹 注意移动文件夹时保持文件夹的树状结构
		 */
		File[] sourceFiles = srcDir.listFiles();
		boolean ok = true;
		for (File sourceFile : sourceFiles) {
			if (sourceFile.isFile())
				if (!moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath())) {
					ok = false;
				} else if (sourceFile.isDirectory())
					moveDirectory(sourceFile.getAbsolutePath(),
							destDir.getAbsolutePath() + File.separator + sourceFile.getName());
				else
					;
		}
		return ok && srcDir.delete();
	}

	/**
	 * 复制单个文件
	 *
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public boolean copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
			return true;
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 复制整个文件夹内容
	 *
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public boolean copyFolder(String oldPath, String newPath) {
		boolean ok = true;
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			if(file==null||file.length==0){
				ok = true;
			}else{
				File temp = null;
				for (int i = 0; i < file.length; i++) {
					if (oldPath.endsWith(File.separator)) {
						temp = new File(oldPath + file[i]);
					} else {
						temp = new File(oldPath + File.separator + file[i]);
					}

					if (temp.isFile()) {
						// if(tv_loading!=null){
						// tv_loading.setText("正在复制 "+temp.getName());//线程中不能加
						// }
						FileInputStream input = new FileInputStream(temp);
						FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
						byte[] b = new byte[1024 * 5];
						int len;
						while ((len = input.read(b)) != -1) {
							output.write(b, 0, len);
						}
						output.flush();
						output.close();
						input.close();
					}
					ok = true;
					if (temp.isDirectory()) {// 如果是子文件夹
						if (!copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i])) {
							ok = false;
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();
			ok = false;
		}
		return ok;

	}
}
