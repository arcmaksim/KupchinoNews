package ru.kupchinonews.rssreader.ui.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

import ru.kupchinonews.rssreader.R;
import ru.kupchinonews.rssreader.ui.activity.BaseActivity;
import ru.kupchinonews.rssreader.ui.activity.MainActivity;

public class SendNewsFragment extends Fragment implements View.OnClickListener {
    private View view;

    private EditText mNewsDescription;
    private TextView mUserPhotoTitle;
    private ImageView mAddPhotoIcon;
    private ImageButton mAddPhotoButton;
    private ImageView mUserPhoto;
    private Button mSendNews;
    private ImageView mGeoImageView;
    private TextView mGeoTextView;
    private TextView mGeoText;
    private CheckBox mGeoCheckBox;
    String path = "";
    String t = "";
    Uri imagePath;
    File photo;
    Bitmap image;

    String mSubject = "";
    String mBody = "";
    String mFrom = "";
    String mTo = "";

    private MainActivity mAct;

    public SendNewsFragment() {
    }

    @Override
    public void onAttach(Activity myActivity) {
        super.onAttach(myActivity);
        mAct = (MainActivity) myActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_send_news, container, false);

            mNewsDescription = (EditText) view.findViewById(R.id.edit_text);
            mNewsDescription.setTypeface(BaseActivity.getDefaultFont());
            mUserPhotoTitle = (TextView) view.findViewById(R.id.image_view_title);
            mUserPhotoTitle.setTypeface(BaseActivity.getDefaultFont());
            mAddPhotoIcon = (ImageView) view.findViewById(R.id.image_view_icon);
            mAddPhotoIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.red_700), PorterDuff.Mode.SRC_ATOP);
            mAddPhotoButton = (ImageButton) view.findViewById(R.id.image_button);
            mAddPhotoButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            mAddPhotoButton.setOnClickListener(this);
            mUserPhoto = (ImageView) view.findViewById(R.id.image_view);
            mSendNews = (Button) view.findViewById(R.id.send_news_button);
            mSendNews.setOnClickListener(this);
            mSendNews.setBackgroundResource(0);
            mGeoTextView = (TextView) view.findViewById(R.id.geo_text_view);
            mGeoTextView.setTypeface(BaseActivity.getDefaultFont());
            mGeoImageView = (ImageView) view.findViewById(R.id.image_view_geo);
            mGeoText = (TextView) view.findViewById(R.id.geo_text);
            mGeoText.setTypeface(BaseActivity.getDefaultFont());
            mGeoCheckBox = (CheckBox) view.findViewById(R.id.geo_check_box);

        } else {
            if (Build.VERSION.SDK_INT <= 10) {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.send_news_button:
                if(mAct.hasInternet()) {
                    boolean lock = false;
                    if(mNewsDescription.getText().length() == 0) {
                        lock = true;
                    } else {
                    }
                    if(mUserPhoto.getDrawable() == null) {
                        lock = true;
                        mAddPhotoIcon.setVisibility(View.VISIBLE);
                    } else {
                        mAddPhotoIcon.setVisibility(View.GONE);
                    }
                    if(!lock)
                        sendNews();
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_LONG);
                }
                break;
            case R.id.image_button:
                takePhoto();
                break;
        }
    }

    public void setImage(Bitmap bmp) {
        mUserPhoto.setImageBitmap(bmp);
        mGeoTextView.setVisibility(View.VISIBLE);
        mGeoImageView.setVisibility(View.VISIBLE);
    }

    private void takePhoto() {
        /*String filename = Environment.getExternalStorageDirectory().getPath() + "/test/testfile.jpg";
        imagePath = Uri.fromFile(new File(filename));
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
        getActivity().startActivityFromFragment(SendNewsFragment.this, intent, 100);*/
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        photo = new File("");
        try
        {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
            //photo.delete();
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG);
        }
        imagePath = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
        getActivity().startActivityFromFragment(SendNewsFragment.this, intent, 100);
    }

    public void grabImage(ImageView imageView)
    {
        getActivity().getContentResolver().notifyChange(imagePath, null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imagePath);
            image = bitmap;
            t = getRealPathFromURI(getImageUri(getContext(), bitmap));
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNews() {
        path = mNewsDescription.getText().toString();
        path += (mGeoCheckBox.isChecked()) ? "\nГеоданные: " + mAct.getLat() + " " + mAct.getLng() : "\nГеоданные отсутсвуют";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"arcmaksim2@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
        intent.putExtra(Intent.EXTRA_TEXT, path);
        Uri uri = Uri.parse("file://" + photo);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Send email..."));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    //setImage(bitmap);
                    grabImage(mUserPhoto);
                    //mUserPhoto.setImageURI(imagePath);
                    mGeoTextView.setVisibility(View.VISIBLE);
                    mGeoImageView.setVisibility(View.VISIBLE);
                    mGeoText.setVisibility(View.VISIBLE);
                    mGeoCheckBox.setVisibility(View.VISIBLE);
                    //path = getImageUri(getContext(), bitmap).toString();
                    //path = getRealPathFromURI(getImageUri(getContext(), bitmap));
                    mAddPhotoIcon.setVisibility(View.GONE);
                    mGeoTextView.setText(String.valueOf(mAct.getLat() + ", " + mAct.getLng()));
                }
        }
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {

            }
            catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(getContext(), "Email send", Toast.LENGTH_LONG).show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

}
