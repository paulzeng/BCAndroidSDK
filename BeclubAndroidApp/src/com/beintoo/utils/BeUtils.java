package com.beintoo.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;

import com.beintoo.beclubapp.R;

public class BeUtils {

	public static String getPicturePathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		return File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);
	}

	/**
	 * Show popup to disable Mock Location. If user press 'Later' button,
	 * activity will be closed.
	 * 
	 * @param activity
	 */
	private static void showMockLocationAlert(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		View dialogView = View.inflate(activity,
				R.layout.mock_location_disallowed_dialog, null);
		dialogView.findViewById(R.id.btn_not_now).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						activity.finish();
						dialog.dismiss();
					}
				});
		dialogView.findViewById(R.id.btn_disable).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(
								Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
						if (intent.resolveActivity(activity.getPackageManager()) != null) {
							activity.startActivity(intent);
						} else {
							intent = new Intent(Settings.ACTION_SETTINGS);
							activity.startActivity(intent);
						}
						dialog.dismiss();
					}
				});

		dialog.setView(dialogView);
		dialog.show();
	}

	/**
	 * Returns true if mock location enabled, false otherwise.
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isMockLocationEnabled(Activity activity) {
		if (!Settings.Secure.getString(activity.getContentResolver(),
				Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
			showMockLocationAlert(activity);
			return true;
		}
		return false;
	}

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input,
			int pixels, int w, int h, boolean squareTL, boolean squareTR,
			boolean squareBL, boolean squareBR) {
		if (context == null) {
			return input;
		}

		Bitmap output;
		try {
			output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		} catch (Exception e) {
			e.printStackTrace();
			output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		}

		Canvas canvas = new Canvas(output);
		final float densityMultiplier = context.getResources()
				.getDisplayMetrics().density;

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);

		// make sure that our rounded corner is scaled appropriately
		final float roundPx = pixels * densityMultiplier;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		// draw rectangles over the corners we want to be square
		if (squareTL) {
			canvas.drawRect(0, 0, w / 2, h / 2, paint);
		}
		if (squareTR) {
			canvas.drawRect(w / 2, 0, w, h / 2, paint);
		}
		if (squareBL) {
			canvas.drawRect(0, h / 2, w / 2, h, paint);
		}
		if (squareBR) {
			canvas.drawRect(w / 2, h / 2, w, h, paint);
		}

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(input, 0, 0, paint);

		input.recycle();
		input = null;

		return output;
	}
}
