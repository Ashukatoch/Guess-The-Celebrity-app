package com.example.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
   private ImageView celebimage;
   private Button option1,option2,option3,option4;
   ArrayList<String> celebimageaddress;
   ArrayList<String> celebname;
   ArrayList<String> options;
   int correctlocation,rightanswer,wronganswer,correctoption;
   Random random;

   public class DownloadHtml extends AsyncTask<String, Void, String>
   {
       @Override
       protected String doInBackground(String... urls)
       {
           URL url;
           HttpURLConnection connection=null;
           String Result="";
           try {
                url=new URL(urls[0]);
               connection= (HttpURLConnection) url.openConnection();
               InputStream in=connection.getInputStream();
               InputStreamReader reader=new InputStreamReader(in);
               int data=reader.read();
               while(data!=-1)
               {
                   char current= (char) data;
                   Result+=current;
                   data=reader.read();
               }
               return Result;
           }
           catch (Exception e) {
               e.printStackTrace();
               return null;
           }


       }
   }
   public class DownloadImage extends AsyncTask<String,Void, Bitmap>
   {
       URL url;
       HttpURLConnection connection=null;

       @Override
       protected Bitmap doInBackground(String... urls) {
           try {
               Bitmap bitmap;
               url=new URL(urls[0]);
               connection= (HttpURLConnection) url.openConnection();
               InputStream in=connection.getInputStream();
               bitmap= BitmapFactory.decodeStream(in);
               return bitmap;
           }
           catch (Exception e) {
               e.printStackTrace();
               return null;
           }

       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        celebimage=findViewById(R.id.celebimage);
        option1=findViewById(R.id.option1);
        option2=findViewById(R.id.option2);
        option3=findViewById(R.id.option3);
        option4=findViewById(R.id.option4);
        celebimageaddress=new ArrayList<>();
        celebname=new ArrayList<>();
        options=new ArrayList<>();
        random=new Random();

        DownloadHtml task=new DownloadHtml();

        String result=null;
        try
        {
            result=task.execute("http://www.posh24.se/kandisar").get();
          String[] splitresult=result.split("<div class=\"listedArticle\">");
            Pattern pattern=Pattern.compile("<img src=\"(.*?)\"");
            Matcher matcher=pattern.matcher(splitresult[0]);

            while(matcher.find())
            {
                celebimageaddress.add(matcher.group(1));
            }
            pattern=pattern.compile("alt=\"(.*?)\"");
            matcher=pattern.matcher(splitresult[0]);
            while(matcher.find())
            {
                celebname.add(matcher.group(1));
            }
           // Log.d("Result",celebname.get(0));
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        nextquestion();
    }
    public void nextquestion()
    {
        options.clear();
        correctoption = random.nextInt(4);
        correctlocation =random.nextInt(celebimageaddress.size());
        Bitmap mybitmap;
        DownloadImage task2 =new DownloadImage();
        try
        {
            mybitmap=task2.execute(celebimageaddress.get(correctlocation)).get();
            celebimage.setImageBitmap(mybitmap);
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        rightanswer=random.nextInt(4);
        for(int i=0;i<4;i++)
        {
            if(i==correctoption)
            {
                options.add(celebname.get(correctlocation));
            }
            else
            {
                wronganswer=random.nextInt(celebimageaddress.size());

                    while(wronganswer==correctlocation)
                    wronganswer=random.nextInt(celebimageaddress.size());
                    options.add(celebname.get(wronganswer));
            }
        }
        option1.setText(options.get(0));
        option2.setText(options.get(1));
        option3.setText(options.get(2));
        option4.setText(options.get(3));



    }
    public void Chooseanswer(View v)
    {
    if(correctoption==Integer.parseInt(String.valueOf(v.getTag())))
    {
    Toast.makeText(MainActivity.this,"Correct!!",Toast.LENGTH_SHORT).show();
    }
    else
   Toast.makeText(MainActivity.this,"Wrong :(",Toast.LENGTH_SHORT).show();
    nextquestion();
    }
}
