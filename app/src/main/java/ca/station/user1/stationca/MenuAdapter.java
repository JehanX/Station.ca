package ca.station.user1.stationca;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.station.user1.stationca.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by user1 on 5/4/2016.
 */
public class MenuAdapter extends BaseExpandableListAdapter{

    //Variables definition
    private Context ctx;
    private LinkedHashMap<String, List<String>> Menu_Category;
    private List<String> Movies_List;
    Vector<Integer> Color_List;

    //Constructor
    public MenuAdapter(Context ctx, LinkedHashMap<String, List<String>> Menu_Category, List<String> Movies_List, Vector<Integer> Color_List) {
        this.ctx = ctx;
        this.Menu_Category = Menu_Category;
        this.Movies_List = Movies_List;
        this.Color_List = Color_List;
    }

    @Override
    public int getGroupCount() {
        return Movies_List.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Menu_Category.get(Movies_List.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return Movies_List.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Menu_Category.get(Movies_List.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parentView) {
        String group_title = (String) getGroup(groupPosition);
        if (convertView== null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent_layout, parentView, false);
        }
        TextView parent_textview = (TextView) convertView.findViewById(R.id.parent_txt);
        parent_textview.setTypeface(null, Typeface.BOLD);
        parent_textview.setText(group_title);
        if (Color_List.size()>=3) {
            parent_textview.setTextColor(Color.rgb(Color_List.get(0), Color_List.get(1), Color_List.get(2)));
        }
        ImageView img_selection=(ImageView) convertView.findViewById(R.id.imageView);
        int imgResourceId = isExpanded? R.drawable.arrowright : R.drawable.arrowdown;
        img_selection.setImageResource(imgResourceId);
        if (getChildrenCount(groupPosition)==0) {
            img_selection.setImageResource(android.R.color.transparent);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parentView) {
        String child_title = (String) getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.child_layout, parentView, false);
        }
        TextView child_textview = (TextView) convertView.findViewById(R.id.child_txt);
        child_textview.setText(child_title);
        if (Color_List.size()==6) {
            child_textview.setTextColor(Color.rgb(Color_List.get(3), Color_List.get(4), Color_List.get(5)));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
