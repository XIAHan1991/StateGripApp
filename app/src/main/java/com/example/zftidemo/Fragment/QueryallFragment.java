package com.example.zftidemo.Fragment;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.zftidemo.DialogDatePickerSelect;
import com.example.zftidemo.Flag;
import com.example.zftidemo.R;
import com.example.zftidemo.dao.ConnectDB;
import com.example.zftidemo.http.HttpService;
import com.example.zftidemo.http.HttpTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryallFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener {
    ListView listView;
    SimpleAdapter simpleAdapter;
    Button btn_pre,btn_next;
    TextView tx_pagenum;
    int index = 1;
    Toolbar his_toolbar;
    static int count = 0;
    String start_date = "";
    String end_date = "";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.devicelist,container,false);
        listView = view.findViewById(R.id.device_list);
        Flag.setQueryflag(0);
//        simpleAdapter = new SimpleAdapter(getActivity(),getData(),R.layout.device_view,new String[]{"id", "state", "time", "qualinum",
//                "unqualinum"}, new int[]{R.id.task_id, R.id.task_state,R.id.task_time,R.id.num_qualified,R.id.num_unqualified} );
//        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener((AdapterView.OnItemClickListener) this);
        view.setClickable(true);//???????????????????????????fragment?????????????????????????????????
        btn_pre = (Button)view.findViewById(R.id.pre_page);
        btn_next = (Button)view.findViewById(R.id.nex_page);
        tx_pagenum = (TextView)view.findViewById(R.id.page_num);
        his_toolbar = (Toolbar)view.findViewById(R.id.his_toolbar);
        /*his_toolbar.setTitle("??????????????????");*/
        his_toolbar.inflateMenu(R.menu.search_menu);
        his_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showDialog();
                return true;
            }
        });
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
//        btn_pre.setVisibility(View.INVISIBLE);
//        tx_pagenum.setText("??? "+index+" ???");
//        tx_pagenum.setText("??? 1 ???");
        list();

        return view;
    }
    private void showDialog(){
    /*View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog,null,false);
    final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
    dialog.show();*/
        DialogDatePickerSelect datePickerSelect = new DialogDatePickerSelect(getActivity(), new DialogDatePickerSelect.OnDateSelectCallBack() {
            @Override
            public void onDateSelected(String StartDate, String EndDate) {
                start_date = StartDate + " 00:00:00";
                end_date = EndDate + "00:00:00";

                btn_pre.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.VISIBLE);
                tx_pagenum.setText("??? 1 ???");
                selectList(0);
//                Toast.makeText(getContext(),start_date+end_date,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReset() {
                list();
            }
        });
        datePickerSelect.showDatePickView();

    }
//    private List<Map<String,Object>> getData(){
//            int [] ids={1,2,3,4,5,6};
//            String [] states = {"???????????????", "???????????????", "???????????????", "???????????????", "???????????????", "???????????????"};
//            String [] times = {"2022-4-2 11:10:10", "2022-4-2 11:10:10","2022-4-2 11:10:10","2022-4-2 11:10:10","2022-4-2 11:10:10",
//                    "2022-4-2 11:10:10"};
//            int [] qualified_num = {1,2,3,4,5,6};
//            int [] unqualified_num = {1,2,3,4,5,6};
//            List<Map<String , Object>> list = new ArrayList<>();
//            for (int i=0;i<6;i++){
//                Map map = new HashMap();
//                map.put("id", ids[i]);
//                map.put("state",states[i]);
//                map.put("time",times[i]);
//                map.put("qualinum",qualified_num[i]);
//                map.put("unqualinum", unqualified_num[i]);
//                list.add(map);
//            }
//            return list;
//    }

    @Override
    public void onActivityCreated(@NonNull Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    //????????????(???taskid)
    /*private taskidInterface taskidInterface;
    public interface taskidInterface{
        public void onIdTranslate(String id);
    }
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            taskidInterface = (taskidInterface) activity;
        }catch (Exception e){
            throw new ClassCastException(activity.toString() + "must implement OnListener");
        }
    }
*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = listView.getAdapter().getItem(position).toString();
        String str_list[] = text.split(",");
        List<String> resultList = Arrays.asList(str_list);
        String task_id = resultList.get(1).replace("id=", "");
        System.out.println(task_id);

        HttpService httpService = new HttpService(getActivity());
        httpService.getTaskId(task_id);
        Toast.makeText(getContext(),task_id,Toast.LENGTH_SHORT).show();

        /*Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();*/
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.demo2,new MesFragment())
                .commit();
        //?????????????????????taskid???
    }
    /**
     * ????????????????????????????????????
     */
    private void list() {
        btn_pre.setVisibility(View.INVISIBLE);
        btn_next.setVisibility(View.VISIBLE);
        tx_pagenum.setText("??? 1 ???");

        String current = String.valueOf("1");
        String size = String.valueOf("20");
        String status = String.valueOf("");
        start_date = "";
        end_date = "";

        new Thread() {
            @Override
            public void run() {
                HttpService httpService = new HttpService(getActivity());
                httpService.taskList(0,tx_pagenum, btn_pre, btn_next, listView,current,size,start_date,end_date,status);
            }
        }.start();
    }

    private void selectList(int type) {
//        String current = String.valueOf(edit_current.getText());
//        String size = String.valueOf(edit_size.getText());
//        String status = String.valueOf(edit_status.getText());
        String current = String.valueOf("");
        String size = String.valueOf("20");
        String status = String.valueOf("");
//        start_date = "2022-06-01 00:00:00";
//        end_date = "2022-05-31 00:00:00";
        new Thread() {
            @Override
            public void run() {
                HttpService httpService = new HttpService(getActivity());
                httpService.taskList(type,tx_pagenum, btn_pre, btn_next, listView,current,size,start_date, end_date,status);
                httpService.isFinalCurrent(btn_pre, btn_next, size,start_date, end_date,status);
//                HttpTool httpTool = new HttpTool();
//                String address = httpTool.getData(getContext(), "address");
//                count = ConnectDB.getDBCount(address);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pre_page:
                prePage();
                break;
            case R.id.nex_page:
                nexPage();
                break;
        }
    }
    private void prePage(){
//        index--;
////        int p = count/20 + 1;
//        tx_pagenum.setText("??? "+index+" ???");
//                checkButton();
//        list();
        selectList(-1);
    }
    private void nexPage(){
//        index++;
////        int p = count/20 + 1;
//        tx_pagenum.setText("??? "+index+" ???");
//                checkButton();
//        list();
        selectList(1);
    }
    private void checkButton(){
        if (index <= 1){
            btn_pre.setVisibility(View.INVISIBLE);
            btn_next.setVisibility(View.VISIBLE);
        }else if(index > 1){
            btn_pre.setVisibility(View.VISIBLE);
        }
// else if (index>=count/20 + 1){
//            btn_next.setVisibility(View.INVISIBLE);
//            btn_pre.setVisibility(View.VISIBLE);
//        }

    }

}
