package com.example.user1.stationca;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    //Variable definition
    private int lastExpandedPosition = -1;
    private boolean isConnected;
    private WebView webView;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private boolean success;
    private String Home_URL;
    private String Phone_Number;
    private String Contact_Email;
    LinkedHashMap<String, List<String>> Menu_List = new LinkedHashMap<String, List<String>>();
    List<String> MenuName_List;
    LinkedHashMap<String, LinkedHashMap<String, String>> Menu_Info = new LinkedHashMap<>();
    LinkedHashMap<String, LinkedHashMap<String, String>> SubMenu_Info = new LinkedHashMap<>();
    LinkedHashMap<String, LinkedHashMap<String, String>> Buttons_Info = new LinkedHashMap<>();
    LinkedHashMap<String, String> CSS = new LinkedHashMap<>();
    LinkedHashMap<String, Bitmap> Buttons_Icon = new LinkedHashMap<>();
    List<Button> Button_List = new ArrayList<>();
    Vector<Integer> Color_List = new Vector<>();
    ExpandableListView Exp_list;
    MenuAdapter adapter;
    int test=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView displayMessage = (TextView) findViewById(R.id.displaymessage);
        //Check if there is Internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //Yes indicates there is Internet connection. No, otherwise.
        isConnected = netInfo != null && netInfo.isConnectedOrConnecting();
        //If there is no Internet connection, just show "Please check your internet" when user opens app and leave webview blank.
        if (isConnected == false) {
            displayMessage.setText("Please check your Internet.");
            displayMessage.setVisibility(View.VISIBLE);
            return;
        }
        //Execute AsyncTaskParseJson
        AsyncTaskParseJson Async = new AsyncTaskParseJson();
        Async.execute();
        try {
            //Wait for AsyncTaskParseJson until it finishes. The code will be running on another thread.
            Async.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Check if we got information from Json file successfully
        if (Async.success==false) {
            displayMessage.setText("There is something wrong. Please try it again later.");
            displayMessage.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "There is something wrong. Please try it again later.", Toast.LENGTH_LONG).show();
            return;
        }

        //Get all information from Async
        Menu_List = Async.Menu_List;
        Menu_Info = Async.Menu_Info;
        SubMenu_Info = Async.SubMenu_Info;
        Home_URL = Async.Home_URL;
        Color_List = Async.Color_List;
        Buttons_Info = Async.Buttons_Info;
        Buttons_Icon = Async.Buttons_Icon;
        Phone_Number = Async.Phone_Number;
        Contact_Email = Async.Contact_Email;
        CSS = Async.CSS;
        success = Async.success;

        //Get android view
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(Home_URL);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        Button_List.add(button1);
        Button_List.add(button2);
        Button_List.add(button3);
        Button_List.add(button4);
        Button_List.add(button5);

        int button_len = Buttons_Info.size();
        //Check if client define buttons. If button_len equals to 0 means client doesn't define any
        //buttons and we use default button's icon, text and function. There are 5 buttons in default.
        if (button_len!=0) {

            //loop 5 buttons
            for (int i=0; i<5; i++) {
                Button button = Button_List.get(i);
                //The situation that client defines button(i+1)
                if (i<button_len) {
                    String button_text = Buttons_Info.get("button"+(i+1)).get("button_text");
                    //Use default button text
                    if (!button_text.equals("")) {
                        button.setText(button_text);
                    }
                    Bitmap button_icon = Buttons_Icon.get("button"+(i+1));
                    //Use default button icon
                    if (button_icon!=null) {
                        Drawable drawable = new BitmapDrawable(getResources(),button_icon);
                        button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    }
                }
                //The situation that client doesn't defines button(i+1) and hide it
                else {
                    button.setVisibility(View.GONE);
                }
            }
        }

        //The situation that if the webview is touched
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (Exp_list.getVisibility() == View.VISIBLE) {
                    Exp_list.setVisibility(View.GONE);
                }

                Exp_list.collapseGroup(lastExpandedPosition);



                return false;
            }
        });

        //Detect the loading process
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(final WebView view, String url) {
//                for (final String selector: CSS.keySet()) {
//                    final String declaration = CSS.get(selector);
//                    //Overwrite CSS
//
//                    webView.loadUrl("javascript:(function() {var elementList = document.querySelectorAll('"+selector+"');" +
//                            "var ListLen = document.querySelectorAll('"+selector+"').length;"+
//                            "for (var i=0; i<ListLen; i++) {" +
//                            "var prestyle = elementList[i].getAttribute('style');"+
//                            "elementList[i].style.cssText = prestyle+'"+declaration+"';}})()");
//
//
//                    //webView.setVisibility(View.VISIBLE);
//                }

                //Delay loading time, wait for css
//                Timer timer;
//                timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                webView.setVisibility(View.VISIBLE);
//
//                            }
//                        });
//
//                    }
//                },800);


            };
        });

        //Set ExpandableListView adapter
        Exp_list = (ExpandableListView) findViewById(R.id.expandableListView);
        MenuName_List = new ArrayList<String>(Menu_List.keySet());
        adapter = new MenuAdapter(this, Menu_List, MenuName_List, Color_List);
        Exp_list.setAdapter(adapter);

        //Get the screen height
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int height = displaymetrics.heightPixels;
        //Set menu height
        setListViewHeight(height);

        //Handle collapsing submenu
        Exp_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition!=-1 && groupPosition!=lastExpandedPosition) {
                    Exp_list.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;

            }
        });

        //Handle when submenu is clicked
        Exp_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String parent_name = (String) adapter.getGroup(groupPosition);
                String child_name = (String) adapter.getChild(groupPosition,childPosition);
                String url = SubMenu_Info.get(parent_name+"-"+child_name).get("url");
                webView.loadUrl(url);
                Exp_list.setVisibility(View.GONE);
                Exp_list.collapseGroup(lastExpandedPosition);
                return false;
            }
        });


    }

    //Set menu height
    private void setListViewHeight(int height) {
        ListAdapter adapter = Exp_list.getAdapter();
        int listviewHeight = 0;
        Exp_list.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        listviewHeight = Exp_list.getMeasuredHeight() * adapter.getCount() + (adapter.getCount() * Exp_list.getDividerHeight());
        if (listviewHeight>(2*height/5)) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Exp_list.getLayoutParams();
            params.height=(2*height/5);
            Exp_list.setLayoutParams(params);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    //Handle when menu is clicked
    public void Menu_Click(View v) {
        TextView tv = (TextView) v;
        String url = Menu_Info.get(tv.getText().toString()).get("url");
        String id = Menu_Info.get(tv.getText().toString()).get("id");
        //The situation when menu button goes to a new page directly
        if (id.equals("")) {
            webView.loadUrl(url);
        }
        //The situation when menu button calls modal to fade in
        else {
            webView.loadUrl("javascript:(function(){" +
                    "l=document.getElementById('"+id+"');"+
                    "e=document.createEvent('HTMLEvents');" +
                    "e.initEvent('click',true,true);" +
                    "l.dispatchEvent(e);" +
                    "})()");
        }
        Exp_list.setVisibility(View.GONE);
        Exp_list.collapseGroup(lastExpandedPosition);
    }

    //Handle buttons' functionality
    public void ButtonClicked(View v) {
        if (success==false || isConnected==false) {
            return;
        }
        int button_num=0;
        //Get the id of which button is clicked
        switch (v.getId()) {
            case R.id.button1:
                button_num=1;
                break;
            case R.id.button2:
                button_num=2;
                break;
            case R.id.button3:
                button_num=3;
                break;
            case R.id.button4:
                button_num=4;
                break;
            case R.id.button5:
                button_num=5;
                break;
        }

        //Get the function name of the button is clicked
        String button_func ="";
        //No customized buttons, use default
        if (!Buttons_Info.containsKey("button"+button_num) || Buttons_Info.get("button"+button_num).get("button_func").equals("") ) {
            switch (button_num) {
                case 1:
                    button_func = "Back";
                    break;
                case 2:
                    button_func = "Phone";
                    break;
                case 3:
                    button_func = "Home";
                    break;
                case 4:
                    button_func = "Email";
                    break;
                case 5:
                    button_func = "Menu";
                    break;
            }
        }
        //Customize
        else {
            button_func = Buttons_Info.get("button"+button_num).get("button_func");
        }

        //Set functionality
        switch (button_func) {
            case "Back":
                if (Exp_list.getVisibility() == View.VISIBLE) {
                    Exp_list.setVisibility(View.GONE);
                }
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                break;
            case "Phone":
                if (Exp_list.getVisibility() == View.VISIBLE) {
                    Exp_list.setVisibility(View.GONE);
                }
                Intent myintent = new Intent(Intent.ACTION_VIEW);
                if (Phone_Number.equals("")) {
                    break;
                }
                myintent.setData(Uri.parse("tel:"+Phone_Number));
                startActivity(myintent);
                break;
            case "Home":
                if (Exp_list.getVisibility() == View.VISIBLE) {
                    Exp_list.setVisibility(View.GONE);
                }
                webView.loadUrl(Home_URL);
                break;
            case "Email":
                if (Exp_list.getVisibility() == View.VISIBLE) {
                    Exp_list.setVisibility(View.GONE);
                }
                if (Contact_Email.equals("")) {
                    break;
                }
                String[] TO = {Contact_Email};
                String[] CC = {""};

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                break;
            case "Menu":
                if (Exp_list.getVisibility() == View.VISIBLE) {
                    Exp_list.setVisibility(View.GONE);
                }
                else {
                    Exp_list.setVisibility(View.VISIBLE);
                }
                break;
            case "URLlink":
                if (Exp_list.getVisibility() == View.VISIBLE) {
                    Exp_list.setVisibility(View.GONE);
                }
                webView.loadUrl(Buttons_Info.get("button"+button_num).get("button_url"));
                break;
            case "Modal":
                if (Exp_list.getVisibility() == View.VISIBLE) {
                    Exp_list.setVisibility(View.GONE);
                }
                String modalId = Buttons_Info.get("button"+button_num).get("button_url");
                webView.loadUrl("javascript:(function(){" +
                        "l=document.getElementById('"+modalId+"');"+
                        "e=document.createEvent('HTMLEvents');" +
                        "e.initEvent('click',true,true);" +
                        "l.dispatchEvent(e);" +
                        "})()");
        }
    }
}

