package com.example.foodpickerapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity  implements RadioGroup.OnCheckedChangeListener,
        AdapterView.OnItemClickListener, DialogInterface.OnClickListener{
    Button one_two, one_three, two_one, two_three, three_two, three_one;

    EditText editstore, editfood, editprice;
    RadioGroup type,size,opttype,optsize;
    TextView showadd,showchoose;
    ListView lv;
    String store,food,price;
    String T,S;//用於將種類及大小加入資料庫
    Toast tos;
    Spinner spinnerprice;

    static final String db_name="OptionDB";
    static final String tb_name="option";
    static final String[] FROM = new String[]{"store","food","price","type","size"};
    SQLiteDatabase db;
    Cursor cur;
    SimpleCursorAdapter adapter1;

    ArrayList<String> allstore =new ArrayList<>();
    ArrayList<String> allfood =new ArrayList<>();
    ArrayList<String> allprice =new ArrayList<>();
    ArrayList<String> alltype =new ArrayList<>();
    ArrayList<String> allsize =new ArrayList<>();
    ArrayList<String> optallstore =new ArrayList<>();
    ArrayList<String> optallfood =new ArrayList<>();
    ArrayList<String> optallprice =new ArrayList<>();
    ArrayList<String> optalltype =new ArrayList<>();
    ArrayList<String> optallsize =new ArrayList<>();

    private static final String[] pricelist={"50元以內","51元到75元以內","76元到100元以內",
            "101元到150元以內","超過151元"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater =  getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.activity_main, null);//介面1連結
        final View view2 = inflater.inflate(R.layout.activity_page2, null);//介面2連結
        final View view3 = inflater.inflate(R.layout.activity_page3, null);//介面2連結
        setContentView(view1);//開始畫面在介面1

        editstore=(EditText)view1.findViewById(R.id.editstore);
        editfood=(EditText)view1.findViewById(R.id.editfood);
        editprice=(EditText)view1.findViewById(R.id.editprice);
        type= (RadioGroup)view1.findViewById(R.id.type);
        size= (RadioGroup)view1.findViewById(R.id.size);
        opttype= (RadioGroup)view2.findViewById(R.id.opttype);
        optsize= (RadioGroup)view2.findViewById(R.id.optsize);
        showadd=(TextView)view1.findViewById(R.id.showadd);
        showchoose=(TextView)view2.findViewById(R.id.showchoose);
        lv=(ListView)view3.findViewById(R.id.lv);

        //spinnerprice=(Spinner)findViewById(R.id.spinnerprice);

        type.setOnCheckedChangeListener(this);//監聽Radio Group中的Radio Button的變化
        size.setOnCheckedChangeListener(this);

        one_two = (Button)view1.findViewById(R.id.one_two);//介面button的連結
        one_three = (Button)view1.findViewById(R.id.one_three);
        two_one = (Button)view2.findViewById(R.id.two_one);
        two_three = (Button)view2.findViewById(R.id.two_three);
        three_one  = (Button)view3.findViewById(R.id.three_one);
        three_two  = (Button)view3.findViewById(R.id.three_two);

        one_two.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(view2);
                getarray();//得到食物陣列內容
            }
        });//介面1到介面2
        one_three.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(view3);
            }
        });//介面1到介面3
        two_one.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(view1);
            }
        });//介面2到介面1
        two_three.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(view3);
            }
        });//介面2到介面3
        three_two.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(view2);
                getarray();//得到食物陣列內容
            }
        });//介面3到介面1
        three_one.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(view1);
            }
        });//介面3到介面2

        db=openOrCreateDatabase(db_name, Context.MODE_PRIVATE,null);
        String createTable="CREATE TABLE IF NOT EXISTS "+ tb_name +
                "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "store VARCHAR(64), "+"food VARCHAR(64), "
                +"price VARCHAR(4), "+"type VARCHAR(16), "+"size VARCHAR(16)) ";
        db.execSQL(createTable);//建立資料庫
        cur=db.rawQuery("SELECT  * FROM " +tb_name,null);

        adapter1=new SimpleCursorAdapter(this,R.layout.item,cur,FROM, new int[] {R.id.itemstore,
                R.id.itemfood,R.id.itemprice,R.id.itemtype,R.id.itemsize},0);

        lv.setOnItemClickListener(this);
        lv.setAdapter(adapter1);//將listview與資料庫連結

        tos=Toast.makeText(this,"",Toast.LENGTH_SHORT);//建立toast物件

        ArrayAdapter<String> adapter2=new ArrayAdapter<>(this,R.layout.price_spinner,pricelist);
        spinnerprice = (Spinner)view2.findViewById(R.id.spinnerprice);
        adapter2.setDropDownViewResource(R.layout.price_spinner);
        spinnerprice.setAdapter(adapter2);

    }//主程式
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(type.getCheckedRadioButtonId()==R.id.rice)//得到食物類別
            T="飯類";
        else if(type.getCheckedRadioButtonId()==R.id.noodle)
            T="麵類";
        else if(type.getCheckedRadioButtonId()==R.id.fastfood)
            T="速食";
        if(size.getCheckedRadioButtonId()==R.id.big)//得到食物份量
            S="大份";
        else if(size.getCheckedRadioButtonId()==R.id.mid)
            S="中份";
        else if(size.getCheckedRadioButtonId()==R.id.small)
            S="小份";
    }//當Radio Group中的Radio Button改變
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new AlertDialog.Builder(this).setTitle("刪除").
                setIcon(android.R.drawable.ic_delete).setMessage("確定要刪除此項嗎？").
                setCancelable(true).setPositiveButton("刪除",this).
                setNeutralButton("取消",null).show();
    }//按下listview彈出視窗
    @Override
    public void onClick(DialogInterface dialog, int which) {
        db.delete(tb_name,"_id="+cur.getInt(0),null);
        requery();//更新listview
        tos.setText("已刪除");
        tos.show();
    }//按下刪除後的動作
    public void addfood(View v){//加入菜單的動作
        store=editstore.getText().toString();//得到edittext中的內容
        food=editfood.getText().toString();
        price=editprice.getText().toString();

        if(store.isEmpty()||food.isEmpty()||price.isEmpty()||T==null||S==null)//未完整輸入資料
            showadd.setText("請輸入完整資料!");
        else{
            addDate(store,food,price,T,S);//呼叫addDate()方法
            showadd.setText("已加入\n"+store+"："+food+"\n"+"價格："+price+"元\n"+T+","+S);//顯示新增的菜單
            tos.setText("已新增");
            tos.show();
            editstore.setText("");//將edittext和radio button重製
            editfood.setText("");
            editprice.setText("");
            size.clearCheck();
            type.clearCheck();

        }
    }//新增餐廳
    public void choose(View v){
        int index=spinnerprice.getSelectedItemPosition();
        optallprice.clear();
        optallfood.clear();
        optallsize.clear();
        optallstore.clear();
        optalltype.clear();



        int checkedSize = optsize.getCheckedRadioButtonId();
        int checkedType = opttype.getCheckedRadioButtonId();

        if (checkedSize == R.id.optbig) {
            if (checkedType == R.id.optrice) {
                if (index == 0) { addtooopt("大份", "飯類", 0, 50); show(); }
                else if (index == 1) { addtooopt("大份", "飯類", 50, 75); show(); }
                else if (index == 2) { addtooopt("大份", "飯類", 75, 100); show(); }
                else if (index == 3) { addtooopt("大份", "飯類", 100, 150); show(); }
                else if (index == 4) { addtooopt("大份", "飯類", 150, 1000000000); show(); }
            } else if (checkedType == R.id.optnoodle) {
                if (index == 0) { addtooopt("大份", "麵類", 0, 50); show(); }
                else if (index == 1) { addtooopt("大份", "麵類", 50, 75); show(); }
                else if (index == 2) { addtooopt("大份", "麵類", 75, 100); show(); }
                else if (index == 3) { addtooopt("大份", "麵類", 100, 150); show(); }
                else if (index == 4) { addtooopt("大份", "麵類", 150, 1000000000); show(); }
            } else if (checkedType == R.id.optfastfood) {
                if (index == 0) { addtooopt("大份", "速食", 0, 50); show(); }
                else if (index == 1) { addtooopt("大份", "速食", 50, 75); show(); }
                else if (index == 2) { addtooopt("大份", "速食", 75, 100); show(); }
                else if (index == 3) { addtooopt("大份", "速食", 100, 150); show(); }
                else if (index == 4) { addtooopt("大份", "速食", 150, 1000000000); show(); }
            }
        } else if (checkedSize == R.id.optmid) {
            if (checkedType == R.id.optrice) {
                if (index == 0) { addtooopt("中份", "飯類", 0, 50); show(); }
                else if (index == 1) { addtooopt("中份", "飯類", 50, 75); show(); }
                else if (index == 2) { addtooopt("中份", "飯類", 75, 100); show(); }
                else if (index == 3) { addtooopt("中份", "飯類", 100, 150); show(); }
                else if (index == 4) { addtooopt("中份", "飯類", 150, 1000000000); show(); }
            } else if (checkedType == R.id.optnoodle) {
                if (index == 0) { addtooopt("中份", "麵類", 0, 50); show(); }
                else if (index == 1) { addtooopt("中份", "麵類", 50, 75); show(); }
                else if (index == 2) { addtooopt("中份", "麵類", 75, 100); show(); }
                else if (index == 3) { addtooopt("中份", "麵類", 100, 150); show(); }
                else if (index == 4) { addtooopt("中份", "麵類", 150, 1000000000); show(); }
            } else if (checkedType == R.id.optfastfood) {
                if (index == 0) { addtooopt("中份", "速食", 0, 50); show(); }
                else if (index == 1) { addtooopt("中份", "速食", 50, 75); show(); }
                else if (index == 2) { addtooopt("中份", "速食", 75, 100); show(); }
                else if (index == 3) { addtooopt("中份", "速食", 100, 150); show(); }
                else if (index == 4) { addtooopt("中份", "速食", 150, 1000000000); show(); }
            }
        } else if (checkedSize == R.id.optsmall) {
            if (checkedType == R.id.optrice) {
                if (index == 0) { addtooopt("小份", "飯類", 0, 50); show(); }
                else if (index == 1) { addtooopt("小份", "飯類", 50, 75); show(); }
                else if (index == 2) { addtooopt("小份", "飯類", 75, 100); show(); }
                else if (index == 3) { addtooopt("小份", "飯類", 100, 150); show(); }
                else if (index == 4) { addtooopt("小份", "飯類", 150, 1000000000); show(); }
            } else if (checkedType == R.id.optnoodle) {
                if (index == 0) { addtooopt("小份", "麵類", 0, 50); show(); }
                else if (index == 1) { addtooopt("小份", "麵類", 50, 75); show(); }
                else if (index == 2) { addtooopt("小份", "麵類", 75, 100); show(); }
                else if (index == 3) { addtooopt("小份", "麵類", 100, 150); show(); }
                else if (index == 4) { addtooopt("小份", "麵類", 150, 1000000000); show(); }
            } else if (checkedType == R.id.optfastfood) {
                if (index == 0) { addtooopt("小份", "速食", 0, 50); show(); }
                else if (index == 1) { addtooopt("小份", "速食", 50, 75); show(); }
                else if (index == 2) { addtooopt("小份", "速食", 75, 100); show(); }
                else if (index == 3) { addtooopt("小份", "速食", 100, 150); show(); }
                else if (index == 4) { addtooopt("小份", "速食", 150, 1000000000); show(); }
            }
        }
    }

        private void addDate(String store, String food, String price, String type, String size){//新增5筆資料到資料庫內
        ContentValues cv=new ContentValues(5);
        cv.put(FROM[0],store);
        cv.put(FROM[1],food);
        cv.put(FROM[2],price);
        cv.put(FROM[3],type);
        cv.put(FROM[4],size);
        db.insert(tb_name,null,cv);//將資料加到資料庫
        requery();//更新listview
    }//資料加入資料庫
    private void requery(){
        cur=db.rawQuery("SELECT  * FROM " +tb_name,null);
        adapter1.changeCursor(cur);
    }//更新listview中的資料
    private void getarray(){
        allstore.clear();//將陣列清空
        allfood.clear();
        allprice.clear();
        alltype.clear();
        allsize.clear();
        if (cur.moveToFirst()) {//將資料放入陣列中
            do{
                allstore.add(cur.getString(1));
                allfood.add(cur.getString(2));
                allprice.add(cur.getString(3));
                alltype.add(cur.getString(4));
                allsize.add(cur.getString(5));
            }while (cur.moveToNext());
        }
    }//得到食物陣列
    private void addtooopt(String a, String b, int c, int d ){
        int s = allprice.size();//size of allprice
        for(int i=0;i<s;i++){
            int howmuch = Integer.parseInt(allprice.get(i));
            if(allsize.get(i).equals(a) && alltype.get(i).equals(b) && howmuch>c && howmuch<=d){
                optallprice.add(allprice.get(i));
                optallfood.add(allfood.get(i));
                optallsize.add(allsize.get(i));
                optallstore.add(allstore.get(i));
                optalltype.add(alltype.get(i));
            }
        }
    }
    private void show(){
        int s2,ran;//size of optallprice,same random x
        Random x=new Random();
        s2=optallprice.size();
        if(s2==0){
            showchoose.setText("菜單列表似乎不夠豐富，先去新增菜單吧！");
        }
        else{
            ran=x.nextInt(s2);
            showchoose.setText("登登登！\n"+"就去吃"+optallstore.get(ran)+"的"+optallfood.get(ran)+"吧！\n"+"價格："+optallprice.get(ran)+"元");
        }
    }//showchoose的判斷
}