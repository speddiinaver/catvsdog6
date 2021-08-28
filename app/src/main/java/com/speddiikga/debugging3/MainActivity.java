package com.speddiikga.debugging3;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static final String USER_AGENT = "Mozilla/5.0";
    Button button;
    Button button2;
    ImageView imageView;
    int SELECT_PICTURE = 200;
    String sImage;
    private TextView textView;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        return response.body().string();
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button2 = findViewById(R.id.button2);
        String json = "{\n" +
                "\"check_image\": [\n" +
                sImage + "\"\n" +
                "]\n" +
                "}";
        //String url = "https://reqres.in/api/users";
        String url = "http://127.0.0.1:8000/blog/catdogapi/";
        //String url = ".goorm.io/blog/catdogapi/";
        //String url = "0.0.0.0:8000/blog/catdogapi/";
        //String url = "https://servertest-wtkde.run.goorm.io/blog/cat_dog/";
        button.setOnClickListener(v -> imageChooser());
        button2.setOnClickListener(v -> postAsyncUserInfo());
    }
    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
     public void postAsyncUserInfo() {
        try {
            String url = "http://127.0.0.1:8000/blog/catdogapi/";
            String postBody = "" + "{" + "\n" + "\"check_image\": [\n" + sImage + "\"\n" + "]\n" + "}";
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), postBody);
            Request.Builder builder = new Request.Builder().url(url)
                    .addHeader("Password", "abcdefg")
                    .post(requestBody);
            Request request = builder.build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("Error Occurred");
                    textView.setText("Error Occurred");
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    if (body != null) {
                        System.out.println("Response:" + body.string());
                        textView.setText("Response:" + body.string());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    imageView.setImageURI(selectedImageUri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] bytes = stream.toByteArray();
                        sImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                        textView.setText(sImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //@SuppressLint("StaticFieldLeak")
    /*public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        public NetworkTask(String url) {
            this.url = url;
        }
        @Override
        protected String doInBackground(Void... string) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request("http://127.0.0.1:8000/blog/catdogapi/");
            return result;
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
            if (s==null){
                textView.setText("error!");
            }*/
    /*public class RequestHttpURLConnection {
        public String request(String _url){
            HttpURLConnection urlConn = null;
            try{
                URL url = new URL(_url);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Accept", "application/json");
                urlConn.setRequestProperty("Context_Type", "application/json; UTF-8");
                OutputStream os = urlConn.getOutputStream();
                String json = "{\n" +
                        "\"check_image\": [\n" +
                        sImage+"\"\n" +
                        "]\n" +
                        "}";
                os.write(json.getBytes(StandardCharsets.UTF_8));
                //os.write(Integer.parseInt(json));
                os.flush();
                os.close();
                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder page = new StringBuilder();
                while ((line = reader.readLine()) != null){
                    page.append(line);
                }
                return page.toString();
            } catch (IOException e) { // for URL.
                e.printStackTrace();
            }// for openConnection().
            finally {
                if (urlConn != null)
                    urlConn.disconnect();
            }
            return null;
        }}*/
    private void sendPostHttps() throws Exception {
        String url = "http://127.0.0.1:8000/blog/catdogapi/";
        String urlParameters = "?Param1=aaaa"
                +"&Param2=bbbb";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setConnectTimeout(10000);
        con.setReadTimeout(5000);
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);
        Charset charset = StandardCharsets.UTF_8;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),charset));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        textView.setText(response.toString());
    }
   private void sendpost() throws IOException {
        URL url = new URL("http://127.0.0.1:8000/blog/catdogapi/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String json = "{\n" +
                "\"check_image\": [\n" +
                sImage+"\"\n" +
                "]\n" +
                "}";
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            textView.setText(response.toString());
        }
    }
   /* private void HttpPost(){ //THIS IS NOT USED. IT IS OLD AND BROKEN.
        new AsyncTask<Void, Void, JSONObject>(){
            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject result = null;
                try{
                    URL url = new URL("요청 URL");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setConnectTimeout(15000);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    HashMap<String, String> map = new HashMap<>();
                    map.put("키값","데이터값");
                    StringBuffer sbParams = new StringBuffer();
                    boolean isAnd = false;
                    for(String key: map.keySet()){
                        if(isAnd)
                            sbParams.append("&");
                        sbParams.append(key).append("=").append(map.get(key));
                        if(!isAnd)
                            if(map.size() >= 2)
                                isAnd = true;
                    }
                    wr.write(sbParams.toString());
                    wr.flush();
                    wr.close();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                    } else {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        result = new JSONObject(response.toString());
                    }
                } catch (ConnectException e) {
                    Log.e(TAG, "ConnectException");
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
            }
        }.execute();
    }*/
}