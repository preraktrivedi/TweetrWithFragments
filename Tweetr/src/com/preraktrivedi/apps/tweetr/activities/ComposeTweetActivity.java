package com.preraktrivedi.apps.tweetr.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.application.TweetrApp;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.datamodel.User;
import com.preraktrivedi.apps.tweetr.restclient.TweetrJsonHttpResponseHandler;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

public class ComposeTweetActivity extends Activity {

	private static final String TAG = ComposeTweetActivity.class.getSimpleName();
	public static final String QUOTE_USER = "quote_text";
	private EditText etTweetMessage;
	private TextView tvScreenName, tvUsername, tvTweetCount;
	private ImageView ivProfileImage, ivGalleryPicker, ivCameraPicker, ivLocationPicker, ivImageToTweet;
	private Context mContext;
	private String quoteMsg;
	private TweetrAppData mAppData;
	private Bitmap photoToTweet;
	private boolean isPhotoAdded;
	private static final int RESULT_GALLERY_PICKER = 4114;
	private static final int RESULT_CAMERA_PICKER = 4115;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mAppData = TweetrAppData.getInstance();
		quoteMsg = getIntent().getStringExtra(QUOTE_USER);
		initializeView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose_tweet, menu);
		MenuItem item = menu.findItem(R.id.send_tweet);
		item.setTitle(Html.fromHtml(LayoutUtils.getComposeTitle("Tweet")));
		invalidateOptionsMenu();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initializeView() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		styleActionBar();
		setContentView(R.layout.activity_compose_tweet);
		etTweetMessage = (EditText) findViewById(R.id.etTweetMessage);
		tvScreenName = (TextView) findViewById(R.id.tvScreenNameCompose);
		tvUsername = (TextView) findViewById(R.id.tvUserNameCompose);
		tvTweetCount = (TextView) findViewById(R.id.tvTweetCount);
		ivProfileImage = (ImageView) findViewById(R.id.ivProfileCompose);
		ivGalleryPicker = (ImageView) findViewById(R.id.ivGalleryPicker);
		ivCameraPicker = (ImageView) findViewById(R.id.ivCameraPicker);
		ivLocationPicker = (ImageView) findViewById(R.id.ivLocationPicker);
		ivImageToTweet = (ImageView) findViewById(R.id.ivImageToTweet);
		User user =  mAppData.getAuthenticatedTwitterUser();
		tvScreenName.setText("@" + user.getScreenName());
		tvUsername.setText(user.getName());
		etTweetMessage.requestFocus();
		ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfileImage);
		setupListeners();
	}

	private void styleActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#297ABA")));
		getActionBar().setLogo(R.drawable.ic_logo_tweetr_white);
		getActionBar().setTitle(Html.fromHtml(LayoutUtils.getComposeTitle("Compose")));
	}

	private void setupListeners() {
		etTweetMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s!=null) {
					String tweetMsg = s.toString();
					LayoutUtils.validateTweetMsg(tweetMsg, tvTweetCount);
				}
			}
		});

		if (!TextUtils.isEmpty(quoteMsg)) {
			String populateText = quoteMsg;
			int lengthOfMsg = populateText.length();
			if (lengthOfMsg > 140) {
				lengthOfMsg = 140;
			}
			populateText = populateText.substring(0, lengthOfMsg);
			etTweetMessage.setText(populateText);
			etTweetMessage.setSelection(lengthOfMsg);
		}

		ivGalleryPicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_GALLERY_PICKER);
			}
		});

		ivCameraPicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(camera, RESULT_CAMERA_PICKER);	
			}
		});

		ivLocationPicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LayoutUtils.showToast(mContext, "TODO");
			}
		});

		ivImageToTweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmRemovePic();
			}
		});
	}
	
	private void confirmRemovePic() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(R.string.msg_remove_image);
		adb.setTitle(R.string.title_remove_image);
		adb.setPositiveButton(R.string.title_remove_image, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				addPhotoToUI(null);
			}
		});
		adb.setNegativeButton("Keep", null);
		adb.create().show();
	}
	

	public void onClickTweet(MenuItem mi) {
		String tweetMsg = etTweetMessage.getText().toString(), apiUrl = "update";
		if (TextUtils.isEmpty(tweetMsg)) {
			LayoutUtils.showToast(mContext, "Message cannot be empty");
			return;
		} 

		if (isPhotoAdded) {
			apiUrl = "update_with_media";
		}

		showLoader(true);
		TweetrApp.getRestClient().postTweet(apiUrl, tweetMsg, getPhotoBytes(), "", new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(JSONObject jsonTweet) {
				showLoader(false);
				finish();
			}
		});
	}

	private void showLoader(boolean show) {
		setProgressBarIndeterminateVisibility(show);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && data != null) {
			isPhotoAdded = true;
			if(requestCode == RESULT_CAMERA_PICKER){
				Bundle bundle = data.getExtras();
				Bitmap resultPhoto = (Bitmap) bundle.get("data");
				addPhotoToUI(resultPhoto);
			} else if (requestCode == RESULT_GALLERY_PICKER) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				Bitmap photo = BitmapFactory.decodeFile(picturePath.toString());
				addPhotoToUI(photo);
			}
		}
	}
	
	private void addPhotoToUI(Bitmap photo) {
		if (photo != null) {
			photoToTweet = photo;
			ivImageToTweet.setImageBitmap(photoToTweet);
			ivImageToTweet.setVisibility(View.VISIBLE);
			isPhotoAdded = true;
		} else {
			ivImageToTweet.setImageDrawable(null);
			isPhotoAdded = false;
			ivImageToTweet.setVisibility(View.GONE);
		}
	}

	private ByteArrayInputStream getPhotoBytes(){
		if(photoToTweet == null || !isPhotoAdded){
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		photoToTweet.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] myTwitterUploadBytes = baos.toByteArray();
		ByteArrayInputStream bis = new ByteArrayInputStream(myTwitterUploadBytes);
		return bis;
	}

}