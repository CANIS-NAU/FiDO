/*
Create by: Jiawei Gao
date: 2020/3/17
comments: Download zip files and  unzip one by one
            until receive "all data have been transferred"
 */
package com.jg.fido;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ListActivity extends AppCompatActivity {
    TextView responseText;
    String JsonData = "";
    String username;
    private static final int BUFF_SIZE = 1024;
    private static int BUFFER_SIZE = 6 * 1024;
    private static final int[] EditTextID = {
            R.id.item0,
            R.id.item1,
            R.id.item2,
            R.id.item3,
            R.id.item4,
            R.id.item5,
            R.id.item6,
            R.id.item7,
            R.id.item8,
            R.id.item9,
    };
    private static final int[] ButtonsID = {
            R.id.item0b,
            R.id.item1b,
            R.id.item2b,
            R.id.item3b,
            R.id.item4b,
            R.id.item5b,
            R.id.item6b,
            R.id.item7b,
            R.id.item8b,
            R.id.item9b,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //set Username
        Intent intent = getIntent();
        username = intent.getStringExtra("extraData");
        TextView textView = (TextView) findViewById(R.id.userName);
        textView.setText(username);

        // Get list from PriorityActivity
        // set editText
        JsonData = intent.getStringExtra("JsonData");
        priorityJsonParse(JsonData);


        //Send request & get response to display
        //responseText = (TextView) findViewById(R.id.response_text);

        Button sendRequest = (Button) findViewById(R.id.send_request);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.send_request) {
                    EditText item0 = (EditText) findViewById(R.id.item0);
                    EditText item1 = (EditText) findViewById(R.id.item1);
                    EditText item2 = (EditText) findViewById(R.id.item2);

                    sendRequestWithHttpURLConnection(item0.getText().toString());
                    sendRequestWithHttpURLConnection(item1.getText().toString());
                    sendRequestWithHttpURLConnection(item2.getText().toString());
                }
            }
        });


        // Buttons
/*        for(int i=0; i<ButtonsID.length; i++){
            Button button = (Button) findViewById(ButtonsID[i]);
            final EditText item = (EditText) findViewById(EditTextID[i]);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentL2H = new Intent(ListActivity.this, htmlView.class);
                    //intentL2H.putExtra("fileName", item0.getText().toString() + ".html");

                    intentL2H.putExtra("fileName", item.getText().toString() + ".html");
                    startActivity(intentL2H);
                }
            });
        }*/

        Button open0 = (Button) findViewById(R.id.item0b);
        open0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText item0 = (EditText) findViewById(R.id.item0);
                Intent intentL2H = new Intent(ListActivity.this, htmlView.class);
                //intentL2H.putExtra("fileName", item0.getText().toString() + ".html");
                intentL2H.putExtra("fileName", "main" + ".html");
                startActivity(intentL2H);
            }
        });


        // Button for backing to Priority Activity
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentL2P = new Intent(ListActivity.this, PriorityActivity.class);

                intentL2P.putExtra("extraData", username);
                intentL2P.putExtra("JsonData", JsonData);
                startActivity(intentL2P);
            }
        });
    }


    private void sendRequestWithHttpURLConnection(final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;


                try {
                    //URL url = new URL("https://www.cefns.nau.edu/~jg2764/cs560/get_data.json");
                    //URL url = new URL("https://www.cefns.nau.edu/~jg2764/cs560/"+fileName);

//                 while(true) {
                     URL url = new URL("http://10.18.92.249:5000/get_webpage" + "?domain=" + fileName);
                     connection = (HttpURLConnection) url.openConnection();
                     //connection.setRequestMethod("GET");
                     connection.setConnectTimeout(8000);
                     connection.setReadTimeout(8000);

                     connection.setRequestMethod("GET");
                     //DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                     //out.writeBytes(JsonData);

                     InputStream in = connection.getInputStream();

                     reader = new BufferedReader(new InputStreamReader(in));
                     StringBuilder response = new StringBuilder();
                     String line;
                     while ((line = reader.readLine()) != null) {
                         response.append(line);
                     }

                     //showResponse(response.toString());

                     String responseData = response.toString();
                     //parseJSONWithJSONObject(responseData);

/*
                    // break when all done
                     if(responseData == "all data have been transferred"){
                         break;
                     }
*/

                     save(responseData, fileName + ".html");
/*
                    // save zip file and then unzip it
                    save(responseData,fileName+".zip");
                    String zipFileName = fileName+".zip";
                    String srcFile = getFilesDir() + File.separator + zipFileName;
                    String dstDir = getFilesDir() + File.separator;

                    //file:/data/data/com.jg.webviewtest/files/test.zip
                    unzip(new File(srcFile),dstDir);
                    */
//                 }

                    // record status in the text file
                    // filename: BatteryLevel.txt
                    File file = new File(getFilesDir() + File.separator);
                    recordInFile(getBatteryLevel(),getFilesNum(),getFolderSize(file));

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //UI TO Display
                responseText.setText(response);
            }
        });
    }


    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                str.append("id is " + id + " name is " + name + " verision is " + version + "\n");
            }
            showResponse(str.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void priorityJsonParse(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String str;

            for(int i=0; i<EditTextID.length; i++){
                EditText item = (EditText) findViewById(EditTextID[i]);
                str = jsonObject.getString(Integer.toString(i)).replace("https://", "").replace("http://", "");
                /*Toast.makeText(this,str.substring(str.length()-1),
                        Toast.LENGTH_SHORT).show();*/
                if(str.charAt(str.length()-1) == '/')
                    str = str.substring(0, str.length() - 1);
                item.setText(str);
            }

/*            Toast.makeText(this, jsonObject.getString("0"),
                    Toast.LENGTH_SHORT).show();
            EditText item0 = (EditText) findViewById(R.id.item0);
            str = jsonObject.getString("0").replace("https://", "").replace("http://", "");
            str = str.substring(0, str.length() - 1);
*/
/*            if(str.indexOf(str.length()-1) == '/'){
                str = str.substring(0,str.length()-1);
            }
            item0.setText(str);
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String inputText, String fileName) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            //out = openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            out = openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content.toString();
    }





    public static void unzip(File srcFile, String dstDir) {
        File file = new File(dstDir);
        //需要判断该文件存在，且是文件夹
        if (!file.exists() || !file.isDirectory()) file.mkdirs();
        ZipFile zipFile = null;
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            //默认编码方式为UTF8
            zipFile = new ZipFile(srcFile);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            byte[] buffer = new byte[BUFF_SIZE];
            int len = 0;
            while (zipEntrys.hasMoreElements()) {
                ZipEntry zipEntry = zipEntrys.nextElement();
                String fileName = dstDir + File.separator + zipEntry.getName();
                File tmpFile = new File(fileName);
                File parent = tmpFile.getParentFile();
                if (!parent.exists()) parent.mkdirs();
                if (zipEntry.isDirectory()) {
                    if (!tmpFile.exists()) tmpFile.mkdirs();
                } else {
                    fos = new FileOutputStream(tmpFile);
                    is = zipFile.getInputStream(zipEntry);
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    is = null;
                    fos.flush();
                    fos.close();
                    fos = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zipFile != null) zipFile.close();
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // recordInFile(getBatteryLevel(),getFilesNum(),getFolderSize(file));
    private void recordInFile(float batteryLevel, int filesNum, long fileSize){
        final String fileName = "BatteryLevel.txt";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = simpleDateFormat.format(date);

        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput(fileName, Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(out));

            File f=new File(getFilesDir()+ File.separator+fileName);
            if(f.exists() && f.length() == 0){
                writer.write("currentDate\t" +"currentTime\t"+ "BatteryLevel(%)\t" + "num_Files\t" + "size_File(bit)\n");
            }
            writer.write(currentTime +"\t"+ batteryLevel + "\t" + filesNum + "\t" + fileSize + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private float getBatteryLevel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            return (float)batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent = new ContextWrapper(getApplicationContext()).
                    registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            return (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
                    (float)intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
    }

    private int getFilesNum() {
        File file = new File(getFilesDir()+ File.separator);
        File[] files = file.listFiles();
        int fileNum = 0;
        if(files != null) {
            fileNum = files.length - 1;
        }

        final String fileName = "BatteryLevel.txt";
        File f=new File(getFilesDir()+ File.separator+fileName);
        if(f.exists()){
            fileNum--;
        }

        return fileNum;
    }


    private long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()){
                    size = size + getFolderSize(fileList[i]);
                }else{
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}