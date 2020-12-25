package com.example.nbpanalyzer.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nbpanalyzer.Bean.Nbp_data;
import com.example.nbpanalyzer.R;

import java.util.List;

public class MyListAdapter extends BaseAdapter {
    Context context;
    List<Nbp_data> data;    //数据源
    String per_date = "0";
    int layout;       //Item布局

    public MyListAdapter(Context context, int layout ,List<Nbp_data> data){
        this.context=context;
        this.layout=layout;
        this.data=data;
    }
    /**
     * 这里的getCount方法是程序在加载显示到ui上时就要先读取的
     * 这里获得的值决定了listview显示多少行
     * 实际应用中此处的返回值是由从数据库中查询出来的数据的总条数
     */
    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * 根据ListView所在位置返回View
     * @param position
     * @return 选中的数据
     */
    @Override
    public Object getItem(int position) {
        Nbp_data Nbp_choosen_data=data.get(position);
        //return Nbp_choosen_data == null ? null : data.get(position);
        return Nbp_choosen_data;
    }

    /**
     * 根据ListView位置得到数据源集合中的Id
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 重写adapter最重要的就是重写此方法，此方法也是决定listview界面的样式的
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder viewHolder;
        ImageView imageView_time;
        TextView textView_time;
        TextView textView_date;
        TextView textView_sys;
        TextView  textView_dis;
        TextView textView_pr;
        final Nbp_data Nbp_record_data=data.get(position);
        if (convertView == null) {
            convertView= LayoutInflater.from(context).inflate(layout,null);
        }else {
        }
        imageView_time= (ImageView) convertView.findViewById(R.id.image_time);
        textView_time= (TextView) convertView.findViewById(R.id.textview_time);
        textView_date= (TextView) convertView.findViewById(R.id.textview_date);
        textView_sys= (TextView) convertView.findViewById(R.id.textview_sys);
        textView_dis= (TextView) convertView.findViewById(R.id.textview_dis);
        textView_pr= (TextView) convertView.findViewById(R.id.textview_pr);

//        if(per_date.equals(Nbp_record_data.getDATE()))
//        {
//            textView_date.setVisibility(View.GONE);
//        }
//        else {
//            textView_date.setVisibility(View.VISIBLE);
//            per_date = Nbp_record_data.getDATE();
//        }
        imageView_time.setImageResource(Nbp_record_data.getTimeImage());
        textView_date.setText(Nbp_record_data.getDATE());
        textView_time.setText(Nbp_record_data.getTIME());
        textView_sys.setText(String.valueOf(Nbp_record_data.getSYS()));
        textView_dis.setText(String.valueOf(Nbp_record_data.getDIS()));
        textView_pr.setText(String.valueOf(Nbp_record_data.getPR()));

        return convertView;
    }

}
