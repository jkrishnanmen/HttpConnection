package com.nobull.jkrishnan.fakelexiconapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       final TextView usr=(TextView) findViewById(R.id.usrname);
       final TextView pas=(TextView) findViewById(R.id.passwrd);
        Button login=(Button) findViewById(R.id.loginbutton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=usr.getText().toString(),
                        password=pas.getText().toString();
                try{
                    new Login().execute(username,password);
                } catch (Exception e) {

                }

            }
        });

    }

    private class Login extends AsyncTask <String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response="";
            Uri.Builder builder;
            HttpURLConnection urlconn;
            InputStream inputStream;
            try{
                URL url= new URL("http://mentornetz.com/webservice/rest/server.php?wstoken=a8ae49cc747dd1e30239d9eeea1bb96a&wsfunction=local_wstemplate_user_login&moodlewsrestformat=json");
                urlconn=(HttpURLConnection) url.openConnection();
                urlconn.setReadTimeout(20000);
                urlconn.setConnectTimeout(25000);
                urlconn.setRequestMethod("POST");
                urlconn.setDoInput(true);
                urlconn.setDoOutput(true);
                urlconn.connect();
                Log.d("Fakelexicon"," try working");
                builder= new Uri.Builder().appendQueryParameter("username",strings[0]).appendQueryParameter("password",strings[1]);
                Log.d("Fakelexicon"," builder "+builder.toString());
                String query=builder.build().getEncodedQuery();
                Log.d("Fakelexicon"," query "+query);
                OutputStream os=urlconn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int statusCode=urlconn.getResponseCode();
                Log.d("Fakelexicon"," statuscode"+statusCode);

                if(statusCode==200){
                    inputStream= new BufferedInputStream(urlconn.getInputStream());
                    response=convertInputStreamToString(inputStream);
                }

            } catch(Exception e){

            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                Object json=new JSONTokener(s).nextValue();
                if(json instanceof JSONObject){
                    Log.d("Fakelexicon"," onpostexecute");
                } else if(json instanceof JSONArray){

                    JSONArray successArray= new JSONArray(s);
                    JSONObject successObject= successArray.getJSONObject(0);
                    int status=successObject.getInt("status");
                    if(status==1){

                    }
                }
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line="",result="";
            while((line= bufferedReader.readLine())!=null){
                result +=line;
            }

            if(null!=inputStream){
                inputStream.close();
            }
            return result;
        }
    }
}
