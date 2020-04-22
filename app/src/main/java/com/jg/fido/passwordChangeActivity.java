
package com.jg.fido;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class passwordChangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        final Button B_signUp = (Button) findViewById(R.id.cPw);
        B_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intentS2M = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intentS2M);*/
                sendRequestWithHttpURLConnection();
            }
        });

    }
    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable(){
            @Override
            public void run(){
                HttpURLConnection connection = null;
                BufferedReader reader=null;

                EditText editText_Name = (EditText) findViewById(R.id.cName);
                String cName = editText_Name.getText().toString();
                EditText editText_Email = (EditText) findViewById(R.id.cEmail);
                String cEmail = editText_Email.getText().toString();
                EditText editText_Password = (EditText) findViewById(R.id.cPassword);
                String cPassword = editText_Password.getText().toString();

                try{
                    //Check format
                    // email format
                    String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
                    Pattern p = Pattern.compile(str);
                    Matcher m = p.matcher(cEmail);
                    if(!m.matches()){
                        showResponse("Email address is not valid!");
                        IOException ex = new IOException();
                        throw ex;
                    }


                    //URL url = new URL("https://www.cefns.nau.edu/~jg2764/cs560/get_data.json");
                    //URL url = new URL("http://10.18.92.249:5000/signUp"+"?name="+Name+"&ID="+ID+"&Email="+Email+"&pwd="+Password);
                    URL url = new URL("http://10.18.92.121:5000/testReg?"+"username="+cName+"&password="+cPassword+"&email="+cEmail);

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
                        showResponse("Sign up successful!");
                        //Toast.makeText(getApplicationContext(), "True",
                        //       Toast.LENGTH_SHORT).show();

                        Intent intentP2M = new Intent(passwordChangeActivity.this, MainActivity.class);
                        //intentM2L.putExtra("extraData",username);
                        startActivity(intentP2M);
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
                Toast.makeText(passwordChangeActivity.this, response,
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


