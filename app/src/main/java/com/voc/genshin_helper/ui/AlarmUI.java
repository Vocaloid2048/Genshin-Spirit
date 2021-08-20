package com.voc.genshin_helper.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.voc.genshin_helper.R;
import com.voc.genshin_helper.data.ScreenSizeUtils;
import com.voc.genshin_helper.util.ReminderBroadcast;
import com.voc.genshin_helper.util.SampleBootReceiver;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmUI extends AppCompatActivity {

    ImageView alarm_add ;
    Context context ;
    // In View
    Spinner type_spinner;
    Spinner ph_spinner; //派遣
    Spinner coin_lvl_spinner; //信任等階
    Spinner coin_grade_spinner; //洞天仙力
    Spinner jb_spinner; //周本
    EditText count_et,title_et,info_et;
    Switch use_switch;
    Switch cd_switch;
    Button alarm_cancel , alarm_ok ;
    LinearLayout alarm_ph_ll , alarm_dtbc_ll , alarm_jb_ll,alarm_custom_ll;
    TextView alarm_time_picker;

    // Var
    String[] typeList ;
    String[] timeJbList ;
    String[] timePhList ;
    String[] coin_lvl_grade = {"1","2","3","4","5","6","7","8","9","10"};
    String[] coin_grade_grade = {"1","2","3","4","5","6","7","8","9","10"};

    int[] typeListINT = {R.string.alarm_t1,R.string.alarm_t2,R.string.alarm_t3,R.string.alarm_t4,R.string.alarm_t5};
    int[] timePhListINT = {R.string.alarm_time1,R.string.alarm_time2,R.string.alarm_time3,R.string.alarm_time4};
    int[] timeJbListINT = {R.string.sunday,R.string.monday,R.string.tuesday,R.string.wednesday,R.string.thursday,R.string.friday,R.string.saturday};
    int[] coin_lvl_gradeINT = {1,2,3,4,5,6,7,8,9,10};
    int[] coin_grade_gradeINT = {1,2,3,4,5,6,7,8,9,10};

    int[] coin_lvl_max = {300,600,900,1200,1400,1600,1800,2000,2200,2400};
    int[] coin_grade_max = {4,8,12,16,20,22,24,26,28,30};
    int[] timePhList_max = {4,8,12,20};

    int new_choose_type = 0;
    int new_choose_jb = 0;
    int new_choose_ph = 0;
    int new_choose_coin_lvl = 0;
    int new_choose_coin_grade = 0;
    int new_count = 0;
    String new_alarm_title ;
    String new_alarm_info ;
    boolean new_alarm_noti = false;
    Date time_temp ;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ui);
        createNotificationChannel();
        typeList = new String[]{getString(R.string.alarm_t1), getString(R.string.alarm_t2), getString(R.string.alarm_t3), getString(R.string.alarm_t4), getString(R.string.alarm_t5)};
        timePhList = new String[]{getString(R.string.alarm_time1), getString(R.string.alarm_time2), getString(R.string.alarm_time3), getString(R.string.alarm_time4)};
        timeJbList = new String[]{getString(R.string.sunday), getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday), getString(R.string.thursday), getString(R.string.friday), getString(R.string.saturday)};
        context = this;
        sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
        alarm_add = findViewById(R.id.alarm_add);
        alarm_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_add();
            }
        });
        new_alarm_title = getString(R.string.app_name);
        new_alarm_info = getString(R.string.app_name);

        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void type_add (){
        final Dialog dialog = new Dialog(context, R.style.NormalDialogStyle_D);
        View view = View.inflate(context, R.layout.menu_alarm_setup, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        ArrayAdapter type_aa = new ArrayAdapter(this,R.layout.spinner_item,typeList);
        type_aa.setDropDownViewResource(R.layout.spinner_dropdown_item);

        ArrayAdapter time_ph_aa = new ArrayAdapter(this,R.layout.spinner_item,timePhList);
        time_ph_aa.setDropDownViewResource(R.layout.spinner_dropdown_item);

        ArrayAdapter time_jb_aa = new ArrayAdapter(this,R.layout.spinner_item,timeJbList);
        time_jb_aa.setDropDownViewResource(R.layout.spinner_dropdown_item);

        ArrayAdapter coin_lvl_aa = new ArrayAdapter(this,R.layout.spinner_item,coin_lvl_grade);
        coin_lvl_aa.setDropDownViewResource(R.layout.spinner_dropdown_item);

        ArrayAdapter coin_grade_aa = new ArrayAdapter(this,R.layout.spinner_item,coin_grade_grade);
        coin_grade_aa.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Method
        alarm_ph_ll = view.findViewById(R.id.alarm_ph_ll);
        alarm_dtbc_ll = view.findViewById(R.id.alarm_dtbc_ll);
        alarm_jb_ll = view.findViewById(R.id.alarm_jb_ll);
        alarm_custom_ll = view.findViewById(R.id.alarm_custom_ll);

        type_spinner = view.findViewById(R.id.alarm_type_tv);
        ph_spinner = view.findViewById(R.id.alarm_ph_time_tv);
        coin_lvl_spinner = view.findViewById(R.id.alarm_coin_tv);
        coin_grade_spinner = view.findViewById(R.id.alarm_coin_tv2);
        jb_spinner = view.findViewById(R.id.alarm_jb_time_tv);

        count_et = view.findViewById(R.id.alarm_count_et);
        title_et = view.findViewById(R.id.alarm_title_et);
        info_et = view.findViewById(R.id.alarm_info_et);
        alarm_time_picker = view.findViewById(R.id.alarm_time_picker);

        use_switch = view.findViewById(R.id.alarm_use_switch);
        cd_switch = view.findViewById(R.id.alarm_25_switch);

        alarm_ok = view.findViewById(R.id.alarm_ok);
        alarm_cancel = view.findViewById(R.id.alarm_cancel);

        count_et.setVisibility(View.GONE);
        alarm_dtbc_ll.setVisibility(View.GONE);
        alarm_ph_ll.setVisibility(View.GONE);
        alarm_jb_ll.setVisibility(View.GONE);
        alarm_custom_ll.setVisibility(View.GONE);
        cd_switch.setVisibility(View.INVISIBLE);

        type_spinner.setAdapter(type_aa);
        ph_spinner.setAdapter(time_ph_aa);
        coin_lvl_spinner.setAdapter(coin_lvl_aa);
        coin_grade_spinner.setAdapter(coin_grade_aa);
        jb_spinner.setAdapter(time_jb_aa);

        title_et.setText(new_alarm_title);
        info_et.setText(new_alarm_info);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        alarm_time_picker.setText(sdf.format(System.currentTimeMillis()));
        alarm_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(alarm_time_picker);
            }
        });

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_choose_type = typeListINT[position];
                if(position == 0){
                    count_et.setVisibility(View.GONE);
                    alarm_dtbc_ll.setVisibility(View.GONE);
                    alarm_ph_ll.setVisibility(View.GONE);
                    alarm_jb_ll.setVisibility(View.GONE);
                    alarm_custom_ll.setVisibility(View.GONE);
                    cd_switch.setVisibility(View.INVISIBLE);
                    count_et.setVisibility(View.GONE);
                    alarm_jb_ll.setVisibility(View.VISIBLE);
                }else if(position == 1){
                    count_et.setVisibility(View.GONE);
                    alarm_dtbc_ll.setVisibility(View.GONE);
                    alarm_ph_ll.setVisibility(View.GONE);
                    alarm_jb_ll.setVisibility(View.GONE);
                    alarm_custom_ll.setVisibility(View.GONE);
                    cd_switch.setVisibility(View.INVISIBLE);
                    count_et.setVisibility(View.VISIBLE);
                }else if(position == 2){
                    count_et.setVisibility(View.GONE);
                    alarm_dtbc_ll.setVisibility(View.GONE);
                    alarm_ph_ll.setVisibility(View.GONE);
                    alarm_jb_ll.setVisibility(View.GONE);
                    alarm_custom_ll.setVisibility(View.GONE);
                    cd_switch.setVisibility(View.INVISIBLE);
                    count_et.setVisibility(View.VISIBLE);
                    alarm_dtbc_ll.setVisibility(View.VISIBLE);
                }else if(position == 3){
                    count_et.setVisibility(View.GONE);
                    alarm_dtbc_ll.setVisibility(View.GONE);
                    alarm_ph_ll.setVisibility(View.GONE);
                    alarm_jb_ll.setVisibility(View.GONE);
                    cd_switch.setVisibility(View.INVISIBLE);
                    count_et.setVisibility(View.VISIBLE);
                    alarm_ph_ll.setVisibility(View.VISIBLE);
                    alarm_custom_ll.setVisibility(View.GONE);
                }else {
                    count_et.setVisibility(View.GONE);
                    alarm_dtbc_ll.setVisibility(View.GONE);
                    alarm_ph_ll.setVisibility(View.GONE);
                    alarm_jb_ll.setVisibility(View.GONE);
                    cd_switch.setVisibility(View.INVISIBLE);
                    alarm_custom_ll.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                new_choose_type = typeListINT[0];
            }
        });

        ph_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_choose_ph = timePhListINT[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                new_choose_ph = timePhListINT[0];
            }
        });

        jb_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_choose_jb = timeJbListINT[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                new_choose_jb = timeJbListINT[0];
            }
        });

        coin_lvl_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_choose_coin_lvl = coin_lvl_gradeINT[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                new_choose_coin_lvl = coin_lvl_gradeINT[0];
            }
        });

        coin_grade_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new_choose_coin_grade = coin_grade_gradeINT[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                new_choose_coin_grade = coin_grade_gradeINT[0];
            }
        });

        alarm_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        alarm_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean title_ok = false;
                boolean info_ok = false;
                boolean count_ok = false;

                if (title_et.getText().toString().equals(" ")|title_et.getText().toString().equals("")| title_et.getText() == null){
                    Toast.makeText(context, "請輸入標題", Toast.LENGTH_SHORT).show();
                }else {new_alarm_title = title_et.getText().toString();title_ok = true;}

                if (info_et.getText().toString().equals(" ")|info_et.getText().toString().equals("")| info_et.getText() == null){
                    Toast.makeText(context, "請輸入內容", Toast.LENGTH_SHORT).show();
                }else {new_alarm_info = info_et.getText().toString();info_ok = true;}

                if(new_choose_type == R.string.alarm_t2 | new_choose_type == R.string.alarm_t3) {
                    if (count_et.getText().toString().equals(" ") | count_et.getText().toString().equals("") | count_et.getText() == null) {
                        Toast.makeText(context, "請輸入數目", Toast.LENGTH_SHORT).show();
                    } else {
                        new_count = Integer.parseInt(count_et.getText().toString());

                        if (new_choose_type == R.string.alarm_t2){
                            if(Integer.parseInt(count_et.getText().toString()) > 159){
                                Toast.makeText(context, "樹脂數目不得多於159個", Toast.LENGTH_SHORT).show();
                            }else {new_count = Integer.parseInt(count_et.getText().toString());count_ok = true;}
                        }else if (new_choose_type == R.string.alarm_t3){
                            if(Integer.parseInt(count_et.getText().toString()) > coin_lvl_max[new_choose_coin_lvl]){
                                Toast.makeText(context, "本等級的洞天寶錢數目不得多於"+String.valueOf(coin_lvl_max[new_choose_coin_lvl])+"個", Toast.LENGTH_SHORT).show();
                            }else {new_count = Integer.parseInt(count_et.getText().toString());count_ok = true;}
                        }else {new_count = 0;count_ok = true;}
                    }
                }else {new_count = 0;count_ok = true;}


                if(use_switch.isChecked()){
                    new_alarm_noti = true;
                }else {new_alarm_noti = false;}

                if (title_ok && info_ok && count_ok){
                    dialog.dismiss();
                    // ADD ALARM

                    Intent intent = new Intent(context, ReminderBroadcast.class);
                    intent.putExtra("title",new_alarm_title);
                    intent.putExtra("info",new_alarm_info);
                    intent.putExtra("type",new_choose_type);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    long finish_time = 0;
                    long require_time = 0;

                    if(new_choose_type == R.string.alarm_t1){
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.DAY_OF_WEEK,new_choose_jb+1);
                        calendar.set(Calendar.HOUR_OF_DAY, 6);
                        require_time = calendar.getTimeInMillis() - System.currentTimeMillis();
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7, pendingIntent);
                    }
                    else if(new_choose_type == R.string.alarm_t2){
                        require_time = (160-new_count)*8*60*1000;
                        finish_time = System.currentTimeMillis()+require_time;
                        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+require_time, pendingIntent);
                    }
                    else if(new_choose_type == R.string.alarm_t3){
                        float vars = (coin_lvl_max[new_choose_coin_lvl]-new_count)/coin_grade_max[new_choose_coin_grade];
                        require_time = (int) vars*60*60*1000;
                        finish_time = System.currentTimeMillis()+require_time;
                        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+require_time, pendingIntent);
                    }
                    else if(new_choose_type == R.string.alarm_t4){
                        require_time = timePhList_max[new_choose_ph]*60*60*1000;
                        finish_time = System.currentTimeMillis()+require_time;
                        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+require_time, pendingIntent);
                    }
                    else if(new_choose_type == R.string.alarm_t5){
                        finish_time = time_temp.getTime();
                        require_time = time_temp.getTime() - System.currentTimeMillis();
                        alarmManager.set(AlarmManager.RTC_WAKEUP,time_temp.getTime(), pendingIntent);
                    }

                    // Set SharedPreference
                    long alarm_count = sharedPreferences.getLong("alarm_cnt",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("alarm"+String.valueOf(alarm_count)+"_title",new_alarm_title);
                    editor.putString("alarm"+String.valueOf(alarm_count)+"_info",new_alarm_info);
                    editor.putInt("alarm"+String.valueOf(alarm_count)+"_type",new_choose_type);
                    editor.putLong("alarm"+String.valueOf(alarm_count)+"_finish_time",finish_time);
                    editor.putLong("alarm"+String.valueOf(alarm_count)+"_remain_time",require_time);
                    editor.putInt("alarm"+String.valueOf(alarm_count)+"_count",new_count);
                    editor.putLong("alarm_count",alarm_count+1);

                    // Function set
                    editor.putInt("alarm"+String.valueOf(alarm_count)+"_jb_time",new_choose_jb);
                    editor.putInt("alarm"+String.valueOf(alarm_count)+"_ph_time",new_choose_ph);
                    editor.putInt("alarm"+String.valueOf(alarm_count)+"_lvl",new_choose_coin_lvl);//信任等階
                    editor.putInt("alarm"+String.valueOf(alarm_count)+"_grade",new_choose_coin_grade);//洞天仙力
                }
            }
        });

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    //https://github.com/hackstarsj/AndroidDatetime_Picker_Dialog/blob/master/app/src/main/java/com/example/androiddatetimepickerdialog/MainActivity.java
    private void showDateTimeDialog(final TextView date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        time_temp = calendar.getTime();
                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(AlarmUI.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
            }
        };

        new DatePickerDialog(AlarmUI.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "GenshinHelper";
            String desc = "Channel for Genshin Helper's Reminder function";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyGenshinHelper",name,importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}