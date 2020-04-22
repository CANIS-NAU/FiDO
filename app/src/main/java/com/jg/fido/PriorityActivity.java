package com.jg.fido;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.jg.fido.Json2Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PriorityActivity extends Activity {
    // Save and rank data from browser history
    Dictionary dic = new Hashtable();
    List<Map.Entry<String,Long>> list;

    private ListView listView;
    private Adapter adapter;
    List<String> dataList = new ArrayList<String>();

    String username = "";
    String domain0 = "www.google.com";
    String domain1 = "www.google.com";
    String domain2 = "www.google.com";

    //定义一个HashMap，用来存放EditText的值，Key是position
    //define a HashMap variable to save the value from EditText, key is the position of the url
    HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
    /** Called when the activity is first created. */
    // 存储json形式的string
    // save it in json format data in string for sending
    String MapData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority);

        //加入数据 into the variable named "dic" and a hashMap variable named "hashMap"
        // get all history in the browser
        history();
        //calculate the data in dictionary after rank
        rank();

        //设置推荐url
        // setup the recommend urls
        Gson gson=new Gson();
        MapData=gson.toJson(hashMap);
        //Toast.makeText(getApplicationContext(), MapData,
        //       Toast.LENGTH_SHORT).show();
        parseJSONWithJSONObject(MapData);

        dataList = addData();

        //实例化ListView 并设置Adapter
        //set Adapter for url changing
        listView = (ListView)findViewById(R.id.listView);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        // display the user's name
        Intent intent = getIntent();
        final String data = intent.getStringExtra("extraData");
        username = data;
        TextView textView = (TextView)findViewById(R.id.userName);
        textView.setText(data);

        try {
            // Check if there are data from List Activity
            String JsonData = intent.getStringExtra("JsonData");

            //Map<Integer, String> Map = new HashMap<Integer, String>();
            //getMap(MapData);

            hashMap = getMap(JsonData);

/*            gson=new Gson();
            String dis=gson.toJson(hashMap);
            Toast.makeText(getApplicationContext(), MapData,
                  Toast.LENGTH_SHORT).show();*/
        }catch (Exception e){

/*            Toast.makeText(getApplicationContext(), e.toString(),
                    Toast.LENGTH_LONG).show();*/
        }

        // get pages
        // Go to ListActivity
        Button buttonA = (Button) findViewById(R.id.getPages);
        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson=new Gson();
                String MapData=gson.toJson(hashMap);
                //Toast.makeText(getApplicationContext(), MapData,
                //       Toast.LENGTH_SHORT).show();
                parseJSONWithJSONObject(MapData);
                sendRequestWithHttpURLConnection();

                Intent intentP2L = new Intent(PriorityActivity.this, ListActivity.class);
                intentP2L.putExtra("extraData",data);
                intentP2L.putExtra("JsonData",MapData);
                startActivity(intentP2L);
            }
        });

        // log off
        // Go to MainActivity
        Button back = (Button) findViewById(R.id.logOff);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentP2M = new Intent(PriorityActivity.this, MainActivity.class);
                startActivity(intentP2M);
            }
        });
    }

    //往ListView 里面添加数据的方法
    //add data into ListView
    private List<String> addData(){
        List<String> list = new ArrayList<String>();
/*        list.add(domain0);
        list.add(domain1);
        list.add(domain2);*/
        int i;
        for(i=0;i<hashMap.size();i++){
            list.add(hashMap.get(i));
        }

        for(; i<10; i++){
            list.add("www.google.com");
        }


        return list;
    }

    private HashMap<Integer, String> getMap(String jsonString)
    {
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(jsonString);   @SuppressWarnings("unchecked")
        Iterator<String> keyIter = jsonObject.keys();
            String key = "0";
            Integer intKey = 0;
            String value;
            HashMap<Integer, String> valueMap = new HashMap<Integer, String>();
            while (keyIter.hasNext())
            {
                key = (String) keyIter.next();
                value = jsonObject.get(key).toString();
                intKey = Integer.parseInt(key);
                valueMap.put(intKey, value);

                //dataList.add(intKey,value);
            }
            return valueMap;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // we did not use these variables any more . it's for sending priority list in url head in the old version
    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);

            domain0  = jsonObject.getString("0");

            domain1  = jsonObject.getString("1");

            domain2  = jsonObject.getString("2");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable(){
            @Override
            public void run(){
                HttpURLConnection connection = null;
                BufferedReader reader=null;
                try{

                    //URL url = new URL("https://www.cefns.nau.edu/~jg2764/cs560/get_data.json");
                    //URL url = new URL("http://10.18.92.249:5000/webpage"+"?username="+username+"&web1="+domain0+"&web2="+domain1);
                    URL url = new URL("http://10.18.92.249:5000/webpage");
                    //showResponse(url.toString());

                    //URL url = new URL("http://10.3.10.102:5000/login"+"?username="+username+"&password="+pwd);
                    connection =(HttpURLConnection)url.openConnection();
                    //connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);


                    // Get method using
                    //connection.setRequestMethod("GET");

                    //Post method using
                    connection.setRequestMethod("POST");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                    Gson gson=new Gson();
                    //add the username into the json data. The key is "99"
                    hashMap.put(99,username);
                    String MapData=gson.toJson(hashMap);
                    showResponse(MapData);
                    out.writeBytes(MapData);
                    //out.writeBytes("{\"username\":" + username + ",\"password\":" + pwd +"}");

                    //String strLogin = "{username:"+ username + ",password:" + pwd + "}";
                    //JSONObject jsonLogin = new JSONObject(strLogin);
                    //out.writeBytes(jsonLogin);

                    InputStream in = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line=reader.readLine()) != null){
                        response.append(line);
                    }

                    //showResponse(response.toString());

                    // parse json object
                    String authentication = "";

                    String responseData = response.toString();
                    JSONArray jsonArray = new JSONArray(responseData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    authentication = jsonObject.getString("authentication");


                    //parseJSONWithJSONObject(responseData);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(reader != null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                //UI TO Display
                Toast.makeText(PriorityActivity.this, response,
                        Toast.LENGTH_LONG).show();

            }
        });
    }


    private String history() {
        String string = "";
        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(
                Uri.parse("content://browser/bookmarks"),
                new String[] { "url" , "date", "created"}, null, null, null);

        while (cursor != null && cursor.moveToNext()) {
            String url = cursor.getString(cursor.getColumnIndex("url"));
            Long closed_date = cursor.getLong(cursor.getColumnIndex("date"));
            Long created_date = cursor.getLong(cursor.getColumnIndex("created"));
            Long duration = closed_date - created_date;

            string = string + "\n" + url ;               // url
            string = string + "\n closed date: " + closed_date;    //date
            string = string + "\n created date: " + created_date;    //created
            string = string + "\n duration time: " + duration +"\n";          //duration

            // dictionary data
            if(dic.get(url) == null) {
                dic.put(url, duration);
            }else{
                duration = Long.parseLong(dic.get(url).toString()) + duration;
                dic.remove(url);
                dic.put(url, duration);
            }
        }
        return string;
    }

    public String stampToDate(long timeMillis){
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Date date = new Date(timeMillis);
        // return simpleDateFormat.format(date);
        String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", timeMillis).toString();

        return date;
    }

    private String rank(){
        Map<String, Long> map = new TreeMap<String, Long>();
        Enumeration i = dic.keys();
        Enumeration j = dic.elements();
        String string = "";

        for (; i.hasMoreElements();) {
            map.put(i.nextElement().toString(), Long.parseLong(j.nextElement().toString()));
        }

        //Convert map.entrySet() to list
        list = new ArrayList<Map.Entry<String,Long>>(map.entrySet());
        //然后通过比较器来实现排序 Comparator to sort
        Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
            //升序排序 Sort Ascending
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        dic = new Hashtable();
        int position = 0;
        for(Map.Entry<String,Long> mapping:list){
            System.out.println(mapping.getKey()+" : "+mapping.getValue());
            dic.put(mapping.getKey(),mapping.getValue());
            //写入到hashMap变量 便于pages之间传输数据
            // put data into hashMap for transferring it into ListActivity
            hashMap.put(position, mapping.getKey());
            position ++;
            string = string + mapping.getKey() + ": " + mapping.getValue().toString() +"\n";
        }

        return string;
    }


    //自定义Adapter
    class Adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            String str = dataList.get(position);
            convertView = LayoutInflater.from(getApplication()).inflate(R.layout.list_view, null);

            final EditText editText = (EditText)convertView.findViewById(R.id.editText1);
            editText.setText(str);
            hashMap.put(position, str);

            //为editText设置TextChangedListener，每次改变的值设置到hashMap
            //我们要拿到里面的值根据position拿值
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count,int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //将editText中改变的值设置的HashMap中
                    hashMap.put(position, s.toString());
                    //Toast.makeText(getApplicationContext(), s.toString(),
                    //        Toast.LENGTH_SHORT).show();
                }
            });

            //如果hashMap不为空，就设置的editText
            if(hashMap.get(position) != null){
                editText.setText(hashMap.get(position));
            }
            return convertView;
        }

    }

}