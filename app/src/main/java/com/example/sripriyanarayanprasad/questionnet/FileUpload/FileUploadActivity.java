package com.example.sripriyanarayanprasad.questionnet.FileUpload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sripriyanarayanprasad.questionnet.R;
import com.example.sripriyanarayanprasad.questionnet.RegisterActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by VijayarajSekar on 8/8/16.
 */
public class FileUploadActivity extends Activity {

    private static final String TAG = FileUploadActivity.class.getSimpleName();

    String FILE_UPLOAD_URL = "http://192.168.1.10:8888/fileUpload.php";

    private Context mContext;

    private Button mSelectFile;
    private Button mUploadFile;

    private TextView mResultFileLocal;
    private TextView mResultFileName;
    private TextView mResultFilePath;

    private Uri mAudioUri;

    private ProgressDialog mProgressBar;

    private int AUDIO_REQUEST = 1;

    private String mFilePath;

    private JSONObject mServerResp;
    private String mServerImgUrl;
    private String mServerImgName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        mContext = this;

        mProgressBar = new ProgressDialog(mContext);
        mProgressBar.setMessage(mContext.getResources().getString(R.string.text_processing));

        mSelectFile = (Button) findViewById(R.id.id_select);
        mUploadFile = (Button) findViewById(R.id.id_upload);

        mResultFileName = (TextView) findViewById(R.id.text_file_name);
        mResultFilePath = (TextView) findViewById(R.id.text_file_path);
        mResultFileLocal = (TextView) findViewById(R.id.text_file_local);

        // Logout button click event
        mUploadFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mAudioUri != null) {

                    new UploadFileToServer().execute();

                }
            }
        });

        mSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio "), AUDIO_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mAudioUri = data.getData();

            try {

                if (mAudioUri != null) {

                    mFilePath = getPath(mContext, mAudioUri);

                    mResultFileLocal.setText(mFilePath);
                    Log.v(TAG, "Selected File : " + mFilePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setCanceledOnTouchOutside(false);
            mProgressBar.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);

            try {

                MultiPartEntity entity = new MultiPartEntity(
                        new MultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                            }
                        });

                File sourceFile = new File(mFilePath);
                System.out.println(mFilePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                Log.v(TAG, "Response from server: " + result);

                mServerResp = new JSONObject(result);

                if (Integer.parseInt(mServerResp.getString("errcode")) == 0) {
                    mServerImgUrl = mServerResp.getString("image_url");
                    mServerImgName = mServerResp.getString("image_name");
                } else {
                    mServerImgUrl = "null";
                    mServerImgName = "null";
                }

                mResultFileName.setText(mServerImgName);
                mResultFilePath.setText(mServerImgUrl);

                mProgressBar.dismiss();

            } catch (Exception e) {
                Log.v(TAG, e.getMessage().toString());
            }

            super.onPostExecute(result);
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                @SuppressLint({"NewApi", "LocalSuppress"}) final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
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
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. For All Kind of Files
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
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
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
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

}
