package com.example.project2.Home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.EnumImagePixelFormat;
import com.dynamsoft.core.ImageData;
import com.dynamsoft.core.Quadrilateral;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.dynamsoft.ddn.DocumentNormalizerException;
import com.dynamsoft.ddn.NormalizedImageResult;
import com.example.project2.R;
import com.example.project2.VolleyRequest.CardAttachRequest;
import com.jsibbold.zoomage.ZoomageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewerActivity extends AppCompatActivity {

    private ZoomageView normalizedImageView;
    private Point[] points;
    private Bitmap rawImage;
    private Bitmap normalized;
    private DocumentNormalizer ddn;
    private int rotation = 0;

    private static final String[] WRITE_EXTERNAL_STORAGE_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 10;

    private String fileName;
    private String getFile;

    Uri photoUri;

    private static final int PICK_FORM_ALBUM = 1;

    private String ImagePath;
    private String ImageName;
    private String boardImg;

    private String basicPath;

    String upLoadServerUri = null;
    private int serverResponseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        upLoadServerUri = "http://43.201.55.113/UploadToServer.php";//서버컴퓨터의 ip주소

        Button rotateButton = findViewById(R.id.rotateButton);
        Button saveImageButton = findViewById(R.id.saveImageButton);

        rotateButton.setOnClickListener(v -> {
            rotation = rotation + 90;
            if (rotation == 360) {
                rotation = 0;
            }
            normalizedImageView.setRotation(rotation);
        });

        saveImageButton.setOnClickListener(v -> {
            if (hasStoragePermission()) {
                saveImage(normalized);
                System.out.println(getFile);

                // 현재 시간 가져오기
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String getTime = simpleDate.format(mDate);

                ImagePath = getFile;
                ImageName = fileName;

                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here
                    uploadFile(ImagePath);
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        ViewerActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();

                                finish();//인텐트 종료

                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                CardAttachRequest cardAttachRequest = new CardAttachRequest("1", "/Images/" + fileName, "image", getTime, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ViewerActivity.this);
                queue.add(cardAttachRequest);



            }else{
                requestPermission();
            }
        });

        RadioGroup filterRadioGroup = findViewById(R.id.filterRadioGroup);
        filterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.binaryRadioButton) {
                    updateSettings(R.raw.binary_template);
                }else if (checkedId == R.id.grayscaleRadioButton) {
                    updateSettings(R.raw.gray_template);
                }else{
                    updateSettings(R.raw.color_template);
                }
                normalize();
            }
        });

        normalizedImageView = findViewById(R.id.normalizedImageView);
        try {
            ddn = new DocumentNormalizer();
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }
        loadImageAndPoints();
        normalize();
    }

    private void loadImageAndPoints(){
        Uri uri = Uri.parse(getIntent().getStringExtra("imageUri"));
        try {
            rawImage = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            Matrix matrix = new Matrix();
            matrix.preRotate(90, 0, 0);
            rawImage = Bitmap.createBitmap(rawImage, 0, 0, rawImage.getWidth(), rawImage.getHeight(), matrix, false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int bitmapWidth = getIntent().getIntExtra("bitmapWidth",720);
        int bitmapHeight = getIntent().getIntExtra("bitmapHeight",1280);
        Parcelable[] parcelables = getIntent().getParcelableArrayExtra("points");
        points = new Point[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            points[i] = (Point) parcelables[i];
            points[i].x = points[i].x*rawImage.getWidth()/bitmapWidth;
            points[i].y = points[i].y*rawImage.getHeight()/bitmapHeight;
        }

    }

    private void normalize(){
        Quadrilateral quad = new Quadrilateral();
        quad.points = points;
        try {
            //NormalizedImageResult result = ddn.normalize(rawImage,quad);
            int bytes = rawImage.getByteCount();
            ByteBuffer buf = ByteBuffer.allocate(bytes);
            rawImage.copyPixelsToBuffer(buf);
            ImageData imageData = new ImageData();
            imageData.bytes = buf.array();
            imageData.width = rawImage.getWidth();
            imageData.height = rawImage.getHeight();
            imageData.stride = rawImage.getRowBytes();
            imageData.format = EnumImagePixelFormat.IPF_ABGR_8888;
            NormalizedImageResult result = ddn.normalize(imageData,quad);
            normalized = result.image.toBitmap();
            normalizedImageView.setImageBitmap(normalized);
        } catch (DocumentNormalizerException | CoreException e) {
            e.printStackTrace();
        }
    }

    private void updateSettings(int id) {
        try {
            ddn.initRuntimeSettingsFromString(readTemplate(id));
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }
    }

    private String readTemplate(int id){
        Resources resources = this.getResources();
        InputStream is=resources.openRawResource(id);
        byte[] buffer;
        try {
            buffer = new byte[is.available()];
            is.read(buffer);
            String content = new String(buffer);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void convertBitmapToImageData(){
        ImageData data = new ImageData();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rawImage.compress(Bitmap.CompressFormat.JPEG,80,stream);
        byte[] byteArray = stream.toByteArray();
        data.format = EnumImagePixelFormat.IPF_RGB_888;
        data.orientation = 0;
        data.width = rawImage.getWidth();
        data.height = rawImage.getHeight();
        data.bytes = byteArray;
        data.stride = 4 * ((rawImage.getWidth() * 3 + 31)/32);
    }

    public void saveImage(Bitmap bmp) {
        File appDir = new File("/data/data/com.example.project2/cache");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            if (rotation != 0) {
                Matrix matrix  = new Matrix();
                matrix.setRotate(rotation);
                Bitmap rotated = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,false);
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }else{
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            fos.flush();
            fos.close();
            getFile = file.getAbsolutePath();
//            Toast.makeText(ViewerActivity.this,"File saved to "+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                WRITE_EXTERNAL_STORAGE_PERMISSION,
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage(normalized);
                } else {
                    Toast.makeText(this, "Please grant the permission to write external storage.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :"
                    + ImagePath + "" + ImageName);
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + ImageName;

//                            Toast.makeText(Join.this, "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(Join.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(Join.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return serverResponseCode;

        } // End else block
    }
}