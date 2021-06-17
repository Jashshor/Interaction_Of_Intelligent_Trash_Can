package trash_can.jashshor.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.friendlyarm.FriendlyThings.HardwareControler;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    public final static double max= 100;
    public int bugs = 0;
    private TextView bug;
    private TextView julidui;
    private Button button5;
    private Button button4;
    private Button button3;
    private Button button1;
    private ImageButton arc1;
    private ImageButton arc2;
    private ImageButton arc3;
    private ImageButton arc4;
    private String lan;
    private TextView zhuangtai;
    private MediaPlayer mp_zh;
    private MediaPlayer mp_en;

    private String devName = "/dev/ttyAMA3";             //用哪个串口，需要改成对应串口文件
    private int speed = 9600;		//波特率
    private int dataBits = 8;		//数据位
    private int stopBits = 1;		//停止位
    private int devfd = -1;         //devfd表示串口打开（成功）与否，初始关闭
    private final int BUFSIZE = 512;
    private byte[] buf = new byte[BUFSIZE];
    private Timer timer = new Timer();

    @Override
    public void onDestroy() {       //周期末收尾工作
        timer.cancel();
        if (devfd != -1) {
            HardwareControler.close(devfd);
            devfd = -1;
        }
        super.onDestroy();      //父类继承
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) this.findViewById(R.id.button2);   //维护模式按钮
        zhuangtai =(TextView)findViewById(R.id.zhuangtai);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {            //切换activity
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);       //弹对话框管理员身份认证
                builder.setTitle("请输入管理员密码");
                EditText editText = new EditText(MainActivity.this);
                builder.setView(editText);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==-1){
                            String key=editText.getText().toString();
                        if(key.equals("a"))                                                 //密码匹配后，跳转界面
                        {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, DebugActivity.class);
                            startActivity(intent);
                            bugs = 0;
                            bug.setText("错误报告："+ bugs);//维护后bugs清零，状态变正常
                            zhuangtai.setText("系统状态：正常");}
                    }}
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }});
        lan = "zh";                                                         //中英文讲解切换以及语音提示功能
        mp_zh = MediaPlayer.create(this, R.raw.chinese);
        mp_en = MediaPlayer.create(this, R.raw.english);
        button1=(Button)this.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(lan.equals("zh"))
                    lan="en";
                else if(lan.equals("en"))
                    lan="zh";
            }
        });
        button4=(Button)this.findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(lan.equals("zh")){
                mp_zh.start();}
                else if(lan.equals("en")){
                    mp_en.start();}
            }
        });
        arc1=(ImageButton)this.findViewById(R.id.arc1);
        arc1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);       //弹可回收垃圾 介绍对话框
                builder.setTitle("可回收垃圾");
                ImageView kehui = new ImageView(MainActivity.this);
                kehui.setImageResource(R.drawable.kehui);
                builder.setView(kehui);
                builder.show();
            }
        });
        arc2=(ImageButton)this.findViewById(R.id.arc2);
        arc2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("其他垃圾");
                ImageView qi = new ImageView(MainActivity.this);
                qi.setImageResource(R.drawable.qi);
                builder.setView(qi);
                builder.show();
            }
        });
        arc3=(ImageButton)this.findViewById(R.id.arc3);
        arc3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("厨余垃圾");
                ImageView chu = new ImageView(MainActivity.this);
                chu.setImageResource(R.drawable.chu);
                builder.setView(chu);
                builder.show();
            }
        });
        arc4=(ImageButton)this.findViewById(R.id.arc4);
        arc4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("有害垃圾");
                ImageView hai = new ImageView(MainActivity.this);
                hai.setImageResource(R.drawable.hai);
                builder.setView(hai);
                builder.show();
            }
        });
        button3 = (Button) this.findViewById(R.id.button3);      //故障反馈按钮
        bug = (TextView)findViewById(R.id.bug);
        julidui = (TextView)findViewById(R.id.julidui);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bugs = bugs +1;
                bug.setText("错误报告："+ bugs);
                if(bugs ==20 ) zhuangtai.setText("系统状态：可能异常");
            }
        });
        devfd = HardwareControler.openSerialPort( devName, speed, dataBits, stopBits );		//重写 打开串口 方法，设定传输参数，获取串口打开状态
        if (devfd >= 0) {
            zhuangtai.setText("系统状态：正常");
            timer.schedule(task, 0, 500);  //设置 timer ：0延时，500周期——重复task
        } else {
            devfd = -1;
            Toast.makeText(this,"出现错误，已记录",Toast.LENGTH_SHORT).show();
            bugs = bugs+1;
            bug.setText("错误报告："+ bugs);
            zhuangtai.setText("系统状态：异常");           //状态异常为真异常（串口异常），bugs更偏向用户反馈情况
        }
        button5 =(Button)findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //GO命令运转电机
                int ret = HardwareControler.write(devfd, "g".getBytes());

                if (ret > 0) {
                    Toast.makeText(MainActivity.this,"Succeed in sending!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,"Fail to send!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

private TimerTask task = new TimerTask() {
    public void run() {
        Message message = new Message();
        message.what = 1;				//接收标志位
        handler.sendMessage(message);			//触发信息接收handler ，转至上一段handler处理
    }
};


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (HardwareControler.select(devfd, 0, 0) == 1) {       //以下：timer 通过调用 select 接口轮询串口设备是否有数据到来
                        int retSize = HardwareControler.read(devfd, buf, BUFSIZE);      //读取串口状态，传入数据，数据位数
                        if (retSize > 0) {
                            String str = new String(buf, 0, retSize); //buf二进制数 offset解码偏移量 retSize位数
                            double in;
                            if(str.contains("juli")) {                              //距离传入
                                str=strDel(str, "juli");
                                if(str.contains("dianjibugongzuo"))
                                    str=strDel(str, "dianjibugongzuo");
                                if(str.contains("dianjigongzuo"))                           //判断有没有再删除，否则会闪退
                                    str=strDel(str, "dianjigongzuo");
                                str= str.replaceAll("\\D+","");
                                in= Integer.parseInt(str);               //String字面转Int
                                if(in >= 100 & in< 800) julidui.setText("用户距离：正常");
                                else  {
                                    julidui.setText("用户距离：异常");
                                    Toast.makeText(MainActivity.this,"Fail to send!",Toast.LENGTH_SHORT).show();     //TODO 不表明事件会当成子控件
                                }
                            }
                            switch(str)                         //处理传入数据                       //TODO 测试传输数据，约定数据内容
                            {
                                case "f":
                                    TextView ke=(TextView)findViewById(R.id.kehuishou);
                                    ke.setText("可回收垃圾(已满)");
                                    ke.setTextColor(MainActivity.this.getResources().getColor(R.color.red));
                                    break;
                                case "e":
                                    TextView ke2= findViewById(R.id.kehuishou);
                                    ke2.setText("可回收垃圾(已满)");                           //TODO  目前垃圾满程度只有可回收垃圾
                                    ke2.setTextColor(Color.GRAY);break;
                            }
                        }
                    }
                break;          //标准格式 跳出switch case语句
            }
            super.handleMessage(msg);       //父类继承
        }
    };

    public static String strDel(String str, String indexStr){
        if(str == null){
            return null;
        }
        StringBuilder newStr = new StringBuilder(str);
        if(newStr.indexOf(indexStr) == 0){
            newStr = new StringBuilder(newStr.substring(indexStr.length()));

        }else if(newStr.indexOf(indexStr) == newStr.length() - indexStr.length()){
            newStr = new StringBuilder(newStr.substring(0,newStr.lastIndexOf(indexStr)));//在结尾

        }else if(newStr.indexOf(indexStr) < (newStr.length() - indexStr.length())){
            newStr =  new StringBuilder(newStr.substring(0,newStr.indexOf(indexStr))+newStr.substring(newStr.indexOf(indexStr)+indexStr.length(),newStr.length()));

        }
        return newStr.toString();
    }
//    public static String getNumberText(String str){
//        if(str.isEmpty()){
//            throw new RuntimeException("参数str不能为空");
//        }
//        StringBuffer number = new StringBuffer("");
//
//        String[] strArray = str.split("");
//        for (String string : strArray) {
//            if(!string.isEmpty() && string.contains){
//                number.append(string);
//            }
//        }
//        return number.toString();
//    }
}