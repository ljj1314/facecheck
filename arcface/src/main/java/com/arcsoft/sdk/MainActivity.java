package com.arcsoft.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.guo.android_extend.image.ImageConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {
	private final String TAG = this.getClass().toString();

	private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE_OP = 2;
	private static final int REQUEST_CODE_OP = 3;
	private static final int REQUEST_CODE_IMAGE_MORE_OP = 4;
	//add by ljj
	EditText mEditText;
	ProgressBar mProgressBar;
	String name = "";
	private Thread view;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_test);
		View v = this.findViewById(R.id.button1);
		v.setOnClickListener(this);
		v = this.findViewById(R.id.button2);
		v.setOnClickListener(this);
		v = this.findViewById(R.id.button3);
		v.setOnClickListener(this);
		v = this.findViewById(R.id.button4);
		v.setOnClickListener(this);
		if(ArcFaceHelper.getInstance() == null){
			ArcFaceHelper.init(this.getApplicationContext());
		}
		mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
			Uri mPath = data.getData();
			String file = getPath(mPath);
			Bitmap bmp = ArcFaceHelper.decodeImage(file);
			if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
				Log.e(TAG, "error");
			} else {
				Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
			}
			startRegister(bmp, file);
		} else if (requestCode == REQUEST_CODE_OP) {
			Log.i(TAG, "RESULT =" + resultCode);
			if (data == null) {
				return;
			}
			Bundle bundle = data.getExtras();
			String path = bundle.getString("imagePath");
			Log.i(TAG, "path="+path);
		} else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
			Uri mPath = ArcFaceHelper.getInstance().getCaptureImage();
			String file = getPath(mPath);
			Bitmap bmp = ArcFaceHelper.decodeImage(file);
			startRegister(bmp, file);
		} else if (requestCode == REQUEST_CODE_IMAGE_MORE_OP && resultCode == RESULT_OK){
			Uri mPath = data.getData();
			String file = getPath(mPath);
			mProgressBar.setVisibility(View.VISIBLE);
			btnRegistAllFace(file);
		}

	}

	private void btnRegistAllFace(String file) {
		final String filepath = file.substring(0,file.lastIndexOf('/'));
		view = new Thread(new Runnable() {
            @Override
            public void run() {
                AFD_FSDKEngine engine = new AFD_FSDKEngine();
                AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);

                AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
                AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
                String file_error_name = "";
                File[] files = new File(filepath).listFiles();
                for (File file : files) {
                    if (file.getName().indexOf("jpg") >= 0) {
                        String s1 = process_face(file.getPath(),engine,err,engine1,error1);
                        if(!s1.isEmpty())
                            file_error_name += s1 +",";
                    }
                }

                //提示
                if(!file_error_name.isEmpty()) {
                    final String s2 = "导入异常文件:"+file_error_name;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //此时已在主线程中，可以更新UI了
							mProgressBar.setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.errormsg)).setText(s2);
							Toast.makeText(MainActivity.this, "部分图片导入失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //此时已在主线程中，可以更新UI了
							mProgressBar.setVisibility(View.GONE);
							((TextView)findViewById(R.id.errormsg)).setText("导入成功");
                            Toast.makeText(MainActivity.this, "导入成功",Toast.LENGTH_SHORT).show();
                        }
                    });
}
            }
        });
		view.start();
	}

	private String process_face(String file, AFD_FSDKEngine engine, AFD_FSDKError err, AFR_FSDKEngine engine1, AFR_FSDKError error1) {
		//图片
		String filename = file.substring(file.lastIndexOf('/')+1,file.lastIndexOf('.'));
		Bitmap mBitmap = loadResizedBitmap(file,640,480,false);

		//转NV21格式存data
		byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
		ImageConverter convert = new ImageConverter();
		convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
		if (convert.convert(mBitmap, data)) {
            Log.d(TAG, "convert ok!");
        }
		convert.destroy();

		//探测引擎初始化
//		AFD_FSDKEngine engine = new AFD_FSDKEngine();
		AFD_FSDKVersion version = new AFD_FSDKVersion();
		List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();

//		AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
		Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
		err = engine.AFD_FSDK_GetVersion(version);
		Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());

		//探测图片中人脸数
		err  = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
		Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
		if(result.size() == 0){
            //找不到人脸记录下来，跳到下一张图片
			return filename;
        }


		//识别人脸特征
//		AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
		AFR_FSDKFace result1 = new AFR_FSDKFace();
//		AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
		error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
		Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
		if (error1.getCode() != AFD_FSDKError.MOK) {
           //识别特征不成功，记录下来，跳到下一张图片
			return filename;
        }

		ArcFaceHelper.getInstance().mFaceDB.addFace(filename, result1.clone());
		return "";
	}

	@Override
	public void onClick(View paramView) {
		// TODO Auto-generated method stub
		int i = paramView.getId();
		if (i == R.id.button2) {
			if (ArcFaceHelper.getInstance().mFaceDB.mRegister.isEmpty()) {
				Toast.makeText(this, "没有注册人脸，请先注册！", Toast.LENGTH_SHORT).show();
			} else {
				startDetector(0);
//				new AlertDialog.Builder(this)
//						.setTitle("请选择相机")
//						.setIcon(android.R.drawable.ic_dialog_info)
//						.setItems(new String[]{"后置相机", "前置相机"}, new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								startDetector(which);
//							}
//						})
//						.show();
			}
		} else if (i == R.id.button1) {
			btnRegistFace();

		}else if (i == R.id.button3) {
			//批量导入
			((TextView)findViewById(R.id.errormsg)).setText("");
			Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
			getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
			getImageByalbum.setType("image/jpeg");
			startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_MORE_OP);
		}else if (i == R.id.button4) {
			((TextView)findViewById(R.id.errormsg)).setText("");
			btnDeleteAll();
		} else {
			;
		}
	}

	/**
	 * @param uri
	 * @return
	 */
	private String getPath(Uri uri) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (DocumentsContract.isDocumentUri(this, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					}

					// TODO handle non-primary volumes
				} else if (isDownloadsDocument(uri)) {

					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

					return getDataColumn(this, contentUri, null, null);
				} else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] {
							split[1]
					};

					return getDataColumn(this, contentUri, selection, selectionArgs);
				}
			}
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		String end = img_path.substring(img_path.length() - 4);
		if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
			return null;
		}
		return img_path;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param mBitmap
	 */
	private void startRegister(Bitmap mBitmap, String file) {
		Intent it = new Intent(MainActivity.this, RegisterActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", file);
		it.putExtras(bundle);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

	private void startDetector(int camera) {
		Intent it = new Intent(MainActivity.this, DetecterActivity.class);
		it.putExtra("Camera", camera);
		startActivityForResult(it, REQUEST_CODE_OP);
	}


	private void btnRegistFace() {
		new AlertDialog.Builder(this)
				.setTitle("请选择注册方式")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setItems(new String[]{"打开图片", "拍摄照片","搜索人脸"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
								getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
								getImageByalbum.setType("image/jpeg");
								startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
								break;
							case 1:
								Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
								ContentValues values = new ContentValues(1);
								values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
								Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
								ArcFaceHelper.getInstance().setCaptureImage(uri);
								intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
								startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
								break;
							case 2:
								//add by ljj search face
								LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
								View layout = inflater.inflate(R.layout.dialog_register, null);
								mEditText = (EditText) layout.findViewById(R.id.editview);
								mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
								mEditText.setHint("请输入搜索名称");
								new AlertDialog.Builder(MainActivity.this)
										.setTitle("请输入搜索名称")
										.setIcon(android.R.drawable.ic_dialog_info)
										.setView(layout)
										.setPositiveButton("确定", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												boolean isexist = false;
												for (FaceDB.FaceRegist face : ArcFaceHelper.getInstance().mFaceDB.mRegister)
												{
													if(mEditText.getText().toString().equals(face.mName))
													{
														name = face.mName;
														isexist = true;
														break;
													}
												}

												//不存在提示
												if(!isexist) {
													dialog.dismiss();
													Toast.makeText(MainActivity.this,"没有该名称信息，请重新输入",Toast.LENGTH_SHORT).show();
												}else {
													new AlertDialog.Builder(MainActivity.this)
															.setTitle("删除注册名:" + name)
															.setMessage("注意:人脸信息删除后无法恢复，需重新录入")
															.setIcon(android.R.drawable.ic_dialog_alert)
															.setPositiveButton("确定", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	ArcFaceHelper.getInstance().mFaceDB.delete(name);
																	dialog.dismiss();
																}
															})
															.setNegativeButton("取消", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	dialog.dismiss();
																}
															})
															.show();
												}

											}
										})
										.setNegativeButton("取消", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
											}
										})
										.show();
								break;
							default:;
						}
					}
				})
				.show();
	}

	private void btnDeleteAll() {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("删除所有注册名")
				.setMessage("注意:删除后无法恢复，需重新录入")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArcFaceHelper.getInstance().mFaceDB.deleteAll();
						dialog.dismiss();
						Toast.makeText(MainActivity.this, "删除成功",Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
	}

//	private Bitmap decodeImage(String path) {
//		Bitmap res;
//		try {
//			ExifInterface exif = new ExifInterface(path);
//			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//			BitmapFactory.Options op = new BitmapFactory.Options();
//			op.inSampleSize = 1;
//			op.inJustDecodeBounds = false;
//			//op.inMutable = true;
//			res = BitmapFactory.decodeFile(path, op);
//			//rotate and scale.
//			Matrix matrix = new Matrix();
//
//			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//				matrix.postRotate(90);
//			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//				matrix.postRotate(180);
//			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//				matrix.postRotate(270);
//			}
//
//			Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
//			Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());
//
//			if (!temp.equals(res)) {
//				res.recycle();
//			}
//			return temp;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	public static Bitmap loadResizedBitmap( String filename, int width, int height, boolean exact ) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile( filename, options );
		if ( options.outHeight > 0 && options.outWidth > 0 ) {
			options.inJustDecodeBounds = false;
			options.inSampleSize = 2;
			while (    options.outWidth  / options.inSampleSize > width
					&& options.outHeight / options.inSampleSize > height ) {
				options.inSampleSize++;
			}
			options.inSampleSize--;

			bitmap = BitmapFactory.decodeFile( filename, options );
			if ( bitmap != null && exact ) {
				bitmap = Bitmap.createScaledBitmap( bitmap, width, height, false );
			}
		}
		return bitmap;
	}
}

