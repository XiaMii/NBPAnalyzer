package com.example.nbpanalyzer.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nbpanalyzer.utils.DbManger;
import com.example.nbpanalyzer.component.MyListAdapter;
import com.example.nbpanalyzer.Bean.Nbp_data;
import com.example.nbpanalyzer.R;
import com.example.nbpanalyzer.model.RecordViewModel;

import java.util.List;
import java.util.Objects;

public class RecordFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    private static final int REQUEST_CHOOSE_DATE = 1;
    private static final String username = "default";
    private RecordViewModel galleryViewModel;
    private MyListAdapter myListAdapter;
    private ListView listView;
    private ImageButton bt_calendar;
    private ImageButton bt_next;
    private ImageButton bt_last;
    private TextView text;
    private TextView text2;

    private EditText nbpPulseText;
    private EditText editTextDATE;
    private EditText editTextTIME;
    private EditText sysPressureText;
    private EditText disPressureText;

    private String tempPR;
    private String tempDATE;
    private String tempTIME;
    private String tempSYS;
    private String tempDIS;

    private List<Nbp_data> recorded_data;
    private DbManger dbManger;
    private AlertDialog alertDialog1;
    private int pageIndex;
    private int pageSize;
    private int offSets;
    private int choosenID;
    private Nbp_data Nbp_choosen_data;
    private String ChoosenDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        View root = inflater.inflate(R.layout.fragment_record, container, false);
        bt_calendar = root.findViewById(R.id.bt_calendar);
        bt_last = root.findViewById(R.id.bt_last);
        bt_next = root.findViewById(R.id.bt_next);
        text = root.findViewById(R.id.text_NBP);
        text2 = root.findViewById(R.id.TextView6);
        //calendarView = root.findViewById(R.id.calendarView);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        pageIndex = 1;
        pageSize = 10;
        offSets = -1;
        ChoosenDate = "2020-01-01";
        choosenID = 0;
        dbManger = DbManger.getInstance(root.getContext());
        //fillData();
        recorded_data = dbManger.getAllData(username,pageIndex, pageSize,offSets);
        Log.e(TAG, "run: "+recorded_data);
        myListAdapter=new MyListAdapter(root.getContext(),R.layout.item_layout,recorded_data);
        listView=(ListView)root.findViewById(R.id.lv_recordedData);
        listView.setAdapter(myListAdapter);
        registerForContextMenu(listView);

        View view1 = View.inflate(root.getContext(), R.layout.activity_alter_dialog_setview, null);

        nbpPulseText = view1.findViewById(R.id.tv_pr_data2);
        sysPressureText= view1.findViewById(R.id.tv_sys_pressure2);
        disPressureText = view1.findViewById(R.id.tv_dis_pressure2);
        editTextDATE = view1.findViewById(R.id.editTextDATE);
        editTextTIME = view1.findViewById(R.id.editTextTIME);
        alertDialog1 = new AlertDialog.Builder(root.getContext())
                .setTitle("修改记录")//标题
                .setView(view1)
                .setIcon(R.mipmap.ic_launcher)//图标
                .setPositiveButton("确定保存(*^▽^*)", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tempDATE = editTextDATE.getText().toString();
                        tempTIME = editTextTIME.getText().toString();
                        tempSYS = sysPressureText.getText().toString();
                        tempDIS = disPressureText.getText().toString();
                        tempPR = nbpPulseText.getText().toString();
                        if(tempDATE.isEmpty()||tempTIME.isEmpty()||tempSYS.isEmpty()||tempDIS.isEmpty()||tempPR.isEmpty()) {
                            Toast.makeText(getContext(),R.string.Data_Empty,Toast.LENGTH_SHORT).show();
                        }
                        else {
                            dbManger.updatebyid(choosenID, username, tempDATE, tempTIME,Integer.valueOf(tempDIS), Integer.valueOf(tempSYS), Integer.valueOf(tempPR));
                            recorded_data.clear();
                            recorded_data.addAll(dbManger.getAllData(username, pageIndex, pageSize, offSets));
                            myListAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .create();

        bt_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(v.getContext(), CalendarActivity.class);
                startActivityForResult(serverIntent, REQUEST_CHOOSE_DATE);
                pageIndex =1;
            }

        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageIndex <= Math.ceil(dbManger.getNumCount(username)/pageSize)){
                    pageIndex = pageIndex+1;
                    recorded_data.clear();
                    recorded_data.addAll(dbManger.getAllData(username,pageIndex, pageSize,offSets));
                    myListAdapter.notifyDataSetChanged();
                }
            }
        });

        bt_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offSets==-1 && pageIndex<=1){}
                if((pageIndex-1)*pageSize+offSets<=0){}
                else {
                    pageIndex = pageIndex-1;
                    recorded_data.clear();
                    recorded_data.addAll(dbManger.getAllData(username,pageIndex, pageSize,offSets));
                    myListAdapter.notifyDataSetChanged();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Nbp_data Nbp_choosen_data = (Nbp_data)arg0.getItemAtPosition(arg2);
                int dis = Nbp_choosen_data.getDIS();
                int sys = Nbp_choosen_data.getSYS();
                text2.setVisibility(View.VISIBLE);
                if (sys>=140||dis>=90){text.setText(R.string.NBP_High);}
                else if(sys<90&&dis<60){text.setText(R.string.NBP_Low);}
                else if(sys<120&&dis<80){text.setText(R.string.NBP_normal);}
                else {text.setText(R.string.NBP_normalHigh);}
                text.setVisibility(View.VISIBLE);
            }
        });


//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            /**
//             * 当滑动状态发生改变时
//             */
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
////				OnScrollListener.SCROLL_STATE_IDLE;  //空闲状态     idle空闲
////				OnScrollListener.SCROLL_STATE_FLING;// 快速滑东，  没有触摸  但在滑动
////				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL; // 触摸并滑动
//
//                //在空闲的时候 判断屏幕最后一个条目，是否是listvist 的最后一个条目， 如果是  说命该加载项更多的数据了
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    //获得可见的最后一个条目的下表
//                    int lastVisiblePosition = listView.getLastVisiblePosition();
//
//                    if (lastVisiblePosition == myListAdapter.getCount() - 1) {
//                        // 看到最后一个条目了
//
//                        if (pageIndex < totalPage - 1) {
//                            //当前页面的下标 加一
//                            pageIndex++;
//                            fillData();
//
//                        } else {
//                            //MyUtils.showToast(ctx, "没有数据了");
//
//                        }
//                    }
//                }
//            }
//
//            @Override
//            /**
//             * 滑动时不断调用此方法
//             */
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });


        return root;
    }

    /**
     * 设置菜单列表项
     * @param menu
     * @param v
     * @param menuInfo
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        /**
         * 这个方法里面有四个参数，它们的意义分别为:
         * ​ 第一个int类型的group ID参数，代表的是组概念，你可以将几个菜单项归为一组，以便更好的以组的方式管理你的菜单按钮。
         *  它对应方法int i = item.getGroupId();
         * ​ 第二个int类型的item ID参数，代表的是项目编号。这个参数非常重要，一个item ID对应一个menu中的选项。
         *  在后面使用菜单的时候，就靠这个item ID来判断你使用的是哪个选项。它对应int id = item.getItemId();
         *  第三个int类型的order ID参数，代表的是菜单项的显示顺序。默认是0，表示菜单的显示顺序就是按照add的显示顺序来显示。
         *  如果两个位置数值一样，那么先定义的拍前面。而且，groupid并不影响排列顺序。
         * ​ 第四个String类型的title参数，表示选项中显示的文字。
         */
        menu.setHeaderTitle("操作：");
        menu.add(1,1,1,"删除记录");
        menu.add(2,2,2,"修改记录");
    }

    /**
     * 处理不同的菜单项
     * @param item
     * @return
     */
    public boolean onContextItemSelected(MenuItem item) {
        //获取上下文菜单适配器
        AdapterView.AdapterContextMenuInfo cmi=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        //获取被选择的菜单位置
        int posMenu=cmi.position;
        //将菜单项与列表视图的条目相关联
        Nbp_choosen_data =(Nbp_data)myListAdapter.getItem(posMenu);
        choosenID = Nbp_choosen_data.getID();
        tempPR = String.valueOf(Nbp_choosen_data.getPR());
        tempDATE = Nbp_choosen_data.getDATE();
        tempTIME = Nbp_choosen_data.getTIME();
        tempSYS = String.valueOf(Nbp_choosen_data.getSYS());
        tempDIS = String.valueOf(Nbp_choosen_data.getDIS());
        switch(item.getItemId()){
            case 1://删除选中项
                dbManger.deletebyid(choosenID);
                recorded_data.remove(Nbp_choosen_data);
                myListAdapter.notifyDataSetChanged();
                break;
            case 2://修改选中项
                nbpPulseText.setText(tempPR);
                sysPressureText.setText(tempSYS);
                disPressureText.setText(tempDIS);
                editTextDATE.setText(tempDATE);
                editTextTIME.setText(tempTIME);
                alertDialog1.show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //获得日期
            case REQUEST_CHOOSE_DATE:
                if (resultCode == Activity.RESULT_OK) {
                    ChoosenDate = Objects.requireNonNull(data.getExtras()).getString(
                            CalendarActivity.CALENDAR_DATE);

                    pageIndex =1;
                    int temp = dbManger.getdataBydate(ChoosenDate,username);
                    if(temp!=0){
                        offSets = dbManger.getNumCount(username) - temp;
                    }
                    else {
                        offSets = -1;
                        Toast.makeText(getContext(),R.string.Data_Not_Found,Toast.LENGTH_SHORT).show();
                    }
                    //Log.e(TAG, "onActivityResult:"+offSets);
                    //recorded_data.addAll(dbManger.getdataBydate(ChoosenDate,username));
                    recorded_data.clear();
                    recorded_data.addAll(dbManger.getAllData(username,pageIndex, pageSize,offSets));
                    Log.e(TAG, "onActivityResult:"+recorded_data);
                    myListAdapter.notifyDataSetChanged();

                }
                break;
            default:
                break;
        }
    }

}