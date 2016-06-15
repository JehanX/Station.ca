package com.example.user1.stationca;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by user1 on 5/5/2016.
 */
//Get all information from Json
public class AsyncTaskParseJson extends AsyncTask<String, String, String> {
    //Indicate if we get all information from json successfully.
    boolean success = false;
    //URL of the home page
    String Home_URL;
    //Phone number of company
    String Phone_Number = "";
    //Email address of company
    String Contact_Email = "";
    //Kye: Name of menu. Value: List of names of submenu belongs to key
    LinkedHashMap<String, List<String>> Menu_List = new LinkedHashMap<>();
    //Key: Name of menu. Value: linkedhashmap [key: (id, url, submenu) value: the values according to keys]
    LinkedHashMap<String, LinkedHashMap<String, String>> Menu_Info = new LinkedHashMap<>();
    //Key: Name of submenu, format is 'MenuName'-'SubmenuName' where 'MenuNmae' is the name of menu and 'SubmenuName'
    //is the name of submenu which belongs to this menu. Value: linkedhashmap [key: (name, url) values: the values according to keys]
    LinkedHashMap<String, LinkedHashMap<String, String>> SubMenu_Info = new LinkedHashMap<>();
    //Key: Name of the button. Value: The bitmap format of button's icon
    LinkedHashMap<String, Bitmap> Buttons_Icon = new LinkedHashMap<>();
    //Key: Name of the button. Value: linkedhashmap [key: (button_text, button_icon, button_func, button_url) value: the values according to keys]
    LinkedHashMap<String, LinkedHashMap<String, String>> Buttons_Info = new LinkedHashMap<>();
    //Key: Selector. Value: Declaration
    LinkedHashMap<String, String> CSS = new LinkedHashMap<>();
    //A vector with 6 integer items. The first three integers represent rgb of the primary color of this site.
    //The last three integers represent rgb of the secondary color of this site.
    Vector<Integer> Color_List = new Vector<>();
    //Set your json string url here. The only thing you need to change is json URL. Everything will be changed according to Json file.
    String JsonURL = "http://www.jehanxue.ca/idea/en/station.json";

    //Actions before doInBackgroud
    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... arg0) {
        try {
            //Instantiate our json parser
            JSONParser jParser = new JSONParser();

            //Get json string from url
            JSONObject json = jParser.getJSONFromUrl(JsonURL);

            //Detect if json file exists or not
            if (json==null) {
                return null;
            }

            //Set URL of the home page
            if (json.has("homeURL")) {
                Home_URL = json.getString("homeURL");
            }
            //Set phone number
            if (json.has("contactPhone")) {
                Phone_Number = json.getString("contactPhone");
            }
            //Set contact Emial
            if (json.has("contactEmail")) {
                Contact_Email = json.getString("contactEmail");
            }

            JSONArray Menu_JsonArr = null;
            //Detect if json file has a key named "Menu" 
            if (json.has("Menu")) {
                Menu_JsonArr = json.getJSONArray("Menu");

                //Loop through all Menu and Submenu
                for (int i = 0; i < Menu_JsonArr.length(); i++) {

                    JSONObject Menu_Name = Menu_JsonArr.getJSONObject(i);


                    //Used to save information of every single menu.
                    LinkedHashMap<String, String> menu_info = new LinkedHashMap<>();

                    //Get menu's attribute
                    String name = Menu_Name.getString("name");
                    String id = Menu_Name.getString("id");
                    String url = Menu_Name.getString("url");
                    String submenu = Menu_Name.getString("submenu");

                    //Put menu's attribute into menu_info
                    menu_info.put("id", id);
                    menu_info.put("url", url);
                    menu_info.put("submenu", submenu);

                    //Put menu_info into Menu_Info
                    Menu_Info.put(name, menu_info);

                    //Used to store all the names of submenu which under the same menu
                    List<String> SubMenu_List = new ArrayList<String>();
                    //Only menu with submenu attribute is Yes has submenu
                    if (submenu.equals("Yes")) {
                        JSONArray SubMenu_JsonArr = json.getJSONArray(name);
                        for (int j = 0; j < SubMenu_JsonArr.length(); j++) {
                            JSONObject SubMenu_Object = SubMenu_JsonArr.getJSONObject(j);

                            //Get submenu's attribute
                            String submenu_name = SubMenu_Object.getString("name");
                            String submenu_url = SubMenu_Object.getString("url");

                            //Used to save information of every single submenu.
                            LinkedHashMap<String, String> submenu_info = new LinkedHashMap<>();

                            //Put submenu's attribute into menu_info
                            submenu_info.put("name", submenu_name);
                            submenu_info.put("url", submenu_url);

                            //Put submenu_info into SubMenu_Info
                            SubMenu_Info.put(name + "-" + submenu_name, submenu_info);

                            //Put name of submenu into SubMenu_List
                            SubMenu_List.add(submenu_name);
                        }
                    }
                    //Put all the names of submenu into Menu_List according to name fo menu
                    Menu_List.put(name, SubMenu_List);
                }
            }

            //Set color
            if (json.has("Color")) {
                JSONArray Colors = json.getJSONArray("Color");
                for(int i = 0; i < Colors.length(); i++) {
                    JSONObject AColor = Colors.getJSONObject(i);
                    //Get and store rgb of primary and secondary color of the page
                    Color_List.add(Integer.parseInt(AColor.getString("r")));
                    Color_List.add(Integer.parseInt(AColor.getString("g")));
                    Color_List.add(Integer.parseInt(AColor.getString("b")));
                }
            }

            //Set buttons
            if (json.has("Buttons")) {
                JSONArray ButtonsJsonArr = json.getJSONArray("Buttons");
                for (int i=0; i<ButtonsJsonArr.length();i++) {
                    JSONObject ButtonJsonObj = ButtonsJsonArr.getJSONObject(i);
                    //Get button's attributes
                    String button_text = ButtonJsonObj.getString("button_text");
                    String button_icon = ButtonJsonObj.getString("button_icon");
                    String button_func = ButtonJsonObj.getString("button_func");
                    String button_url = ButtonJsonObj.getString("button_url");
                    LinkedHashMap<String, String> Button_Info = new LinkedHashMap<>();
                    //Put button's attributes into Button_Info
                    Button_Info.put("button_text",button_text);
                    Button_Info.put("button_icon",button_icon);
                    Button_Info.put("button_func",button_func);
                    Button_Info.put("button_url",button_url);
                    //Put Button_Info into Buttons_Info
                    Buttons_Info.put("button"+(i+1),Button_Info);

                    //Use default button icon
                    if (button_icon.equals("")) {
                        Buttons_Icon.put("button"+(i+1), null);
                    }
                    //Customize button icon
                    else {
                        //Get button icon from url and convert it to bitmap
                        Bitmap bitmap = drawable_from_url(button_icon);
                        if (bitmap==null) {
                            return null;
                        }
                        //Set icon size
                        bitmap = Bitmap.createScaledBitmap(bitmap,70,70,true);
                        //Put the button name and icon into Buttons_Icon
                        Buttons_Icon.put("button"+(i+1), bitmap);
                    }
                }
            }

            //Set CSS
            if (json.has("CSS")) {
                JSONArray CSSJsonArr = json.getJSONArray("CSS");
                for (int i=0; i<CSSJsonArr.length();i++) {
                    JSONObject CSSJsonObj = CSSJsonArr.getJSONObject(i);
                    //Get attributes
                    String selector = CSSJsonObj.getString("selector");
                    String declaration = CSSJsonObj.getString("declaration");
                    //Put attributes into CSS
                    CSS.put(selector,declaration);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Successfully got all information from Json
        success = true;
        return null;
    }
    //Actions after doInBackground
    @Override
    protected void onPostExecute(String strFromDoInBg) {
    }
    //Get button from url and convert to bitmap format
    private Bitmap drawable_from_url(String url) throws java.net.MalformedURLException, java.io.IOException {
        Bitmap x;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream input = new BufferedInputStream(connection.getInputStream());
            x = BitmapFactory.decodeStream(input);
            return x;
        } catch (IOException e) {
            return null;
        }
    }
}


