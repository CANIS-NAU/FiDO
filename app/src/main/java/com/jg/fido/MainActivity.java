package com.jg.fido;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendRequest = (Button) findViewById(R.id.login);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.login) {

                    EditText editText_a = (EditText) findViewById(R.id.login_edit_account);
                    String username = editText_a.getText().toString();

                    Intent intentM2L = new Intent(MainActivity.this, PriorityActivity.class);
                    intentM2L.putExtra("extraData", username);
                    startActivity(intentM2L);

                    //sendRequestWithHttpURLConnection();
                }
            }
        });

        final Button B_signUp = (Button) findViewById(R.id.signUp);
        B_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intentM2S = new Intent(MainActivity.this,SignUpActivity.class);
                    startActivity(intentM2S);
            }
        });

        final Button B_cPw = (Button) findViewById(R.id.btCPassword);
        B_cPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentM2P = new Intent(MainActivity.this,passwordChangeActivity.class);
                startActivity(intentM2P);
            }
        });
    }
    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable(){
            @Override
            public void run(){
                HttpURLConnection connection = null;
                BufferedReader reader=null;
                try{
                    EditText editText_a = (EditText) findViewById(R.id.login_edit_account);
                    String username = editText_a.getText().toString();
                    EditText editText_p = (EditText) findViewById(R.id.login_edit_pwd);
                    String pwd = editText_p.getText().toString();


                    //URL url = new URL("https://www.cefns.nau.edu/~jg2764/cs560/get_data.json");
                    URL url = new URL("http://192.168.0.11:5000/login"+"?username="+username+"&password="+pwd);
                    //URL url = new URL("http://10.3.10.102:5000/login"+"?username="+username+"&password="+pwd);
                    connection =(HttpURLConnection)url.openConnection();
                    //connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);


                    //connection.setRequestMethod("POST");
                    connection.setRequestMethod("GET");
                    //DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                    //out.writeBytes("{username:"+ username + ",password:" + pwd + "}");
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


                    if(authentication.equals("True")){
                        showResponse("Login successful!");
                        //Toast.makeText(getApplicationContext(), "True",
                         //       Toast.LENGTH_SHORT).show();

                        Intent intentM2L = new Intent(MainActivity.this, PriorityActivity.class);
                        intentM2L.putExtra("extraData",username);
                        startActivity(intentM2L);
                    }else{
                        //Toast.makeText(getApplicationContext(), "False",
                         //       Toast.LENGTH_SHORT).show();
                    }

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
                Toast.makeText(MainActivity.this, response,
                        Toast.LENGTH_LONG).show();

            }
        });
    }


    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            StringBuilder str = new StringBuilder();
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                str.append("id is "+id+ " name is "+name +" verision is " + version + "\n");
            }
            showResponse(str.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

