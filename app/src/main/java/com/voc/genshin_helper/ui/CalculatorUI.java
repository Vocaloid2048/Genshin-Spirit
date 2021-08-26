package com.voc.genshin_helper.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.voc.genshin_helper.R;
import com.voc.genshin_helper.data.Characters;
import com.voc.genshin_helper.data.CharactersAdapter;
import com.voc.genshin_helper.data.Characters_Rss;
import com.voc.genshin_helper.data.ScreenSizeUtils;
import com.voc.genshin_helper.util.NumberPickerDialog;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class CalculatorUI extends AppCompatActivity implements NumberPicker.OnValueChangeListener {


    /** Method of requirements */
    Characters_Rss characters_rss ;
    Characters_Rss css ;
    private ViewPager viewPager;
    private ArrayList<View> viewPager_List;
    BottomNavigationView nav_view;
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences calShared; // Only record CalculatorUI's vars, user can get last time data when restart this page (ALSO CAN USE RESET BTN)
    SharedPreferences.Editor editor;
    NumberPickerDialog npd;

    // Char Page
    RecyclerView mList_char;
    CharactersAdapter mCharAdapter;
    public List<Characters> charactersList = new ArrayList<>();


    public boolean show_pyro = true;
    public boolean show_hydro = true;
    public boolean show_anemo = true;
    public boolean show_dendor = true;
    public boolean show_electro = true;
    public boolean show_cryo = true;
    public boolean show_geo = true;

    public boolean show_sword = true;
    public boolean show_claymore = true;
    public boolean show_polearm = true;
    public boolean show_bow = true;
    public boolean show_catalyst = true;

    public int show_stars = 0;


    /** Method of Char's details' container */
    /** Since String can't be null, so there will have "XPR" for identify is result correct */


    // Main
    String name = "XPR" ;
    String nick = "XPR" ;
    int star = 4;
    String element = "XPR" ;
    int isComing = 0 ;
    JSONObject jsonObject;

    // Battle Talent
    String normal_name = "XPR";
    String element_name = "XPR";
    String final_name = "XPR";

    /** Calculator vars -> Might change to int[] which sort by char update time*/
    int before_lvl = 1;
    int after_lvl = 2;

    String normal_zh ;
    String element_zh ;
    String final_zh ;

    String normal_en ;
    String element_en ;
    String final_en ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_ui);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        nav_view = findViewById(R.id.nav_view_cal);

        css = new Characters_Rss();
        npd = new NumberPickerDialog(this);
        context = this;

        final LayoutInflater mInflater = getLayoutInflater().from(this);
        View viewPager0 = mInflater.inflate(R.layout.fragment_cal_char, null,false);
        View viewPager1 = mInflater.inflate(R.layout.fragment_cal_art, null,false);
        View viewPager2 = mInflater.inflate(R.layout.fragment_cal_weapon, null,false);
        View viewPager3 = mInflater.inflate(R.layout.fragment_result, null,false);

        viewPager_List = new ArrayList<View>();
        viewPager_List.add(viewPager0);
        viewPager_List.add(viewPager1);
        viewPager_List.add(viewPager2);
        viewPager_List.add(viewPager3);

        viewPager.setAdapter(new MyViewPagerAdapter(viewPager_List));
        nav_view.setSelectedItemId(R.id.nav_char);
        sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
        calShared = getSharedPreferences("cal_ui",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String color_hex = sharedPreferences.getString("theme_color_hex","#FF5A5A"); // Must include #

        ColorStateList myList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked},
                },
                new int[] {
                        getResources().getColor(R.color.tv_color),
                        getResources().getColor(R.color.tv_color),
                        Color.parseColor(color_hex)
                }
        );

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(color_hex.toUpperCase().equals("#FFFFFFFF")){
            window.setStatusBarColor(Color.parseColor("#000000"));}
        else {
            window.setStatusBarColor(Color.parseColor(color_hex));
            Log.w("WRF",color_hex);
        }


        viewPager.setCurrentItem(0);
        mList_char = viewPager0.findViewById(R.id.main_list);
        mCharAdapter = new CharactersAdapter(context, charactersList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        LinearLayout.LayoutParams paramsMsg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsMsg.gravity = Gravity.CENTER;
        mList_char.setLayoutManager(mLayoutManager);
        mList_char.setLayoutParams(paramsMsg);
        mList_char.setAdapter(mCharAdapter);
        mList_char.removeAllViewsInLayout();
        char_list_reload();

        nav_view.setItemIconTintList(myList);
        nav_view.setItemTextColor(myList);
        nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                viewPager.setCurrentItem(0);
                if (item.getItemId() == R.id.nav_char){
                    viewPager.setCurrentItem(0);
                }else if (item.getItemId() == R.id.nav_artifacts){
                    viewPager.setCurrentItem(1);
                }else if (item.getItemId() == R.id.nav_weapons){
                    viewPager.setCurrentItem(2);
                }else if (item.getItemId() == R.id.nav_result){
                    viewPager.setCurrentItem(3);
                }
                return false;
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0:
                        viewPagerCharSetup();
                        break;

                    case 1:
                        break;

                    case 2:

                        break;

                    case 3:

                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    public void charQuestion (String CharName_BASE){

        sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        CharName_BASE = CharName_BASE.replace(" ","_");
        characters_rss = new Characters_Rss();

        /**
         * OLD WAY -> Only read added translation char json
         *
         * String lang = sharedPreferences.getString("lang","zh-HK");
         *         AssetManager mg = context.getResources().getAssets();
         *         InputStream is = null;
         *         try {
         *             Log.wtf("ALPHA","db/"+lang+"/"+CharName_BASE+".json");
         *             is = mg.open("db/"+lang+"/"+CharName_BASE+".json");
         *             if (is != null) {
         *                 String result = IOUtils.toString(is, StandardCharsets.UTF_8);
         *                 try {
         *                     jsonObject = new JSONObject(result);
         *                     name = jsonObject.getString("name");
         *                     star = jsonObject.getInt("rare");
         *                     element = jsonObject.getString("element");
         *                     isComing = jsonObject.getBoolean("isComingSoon");
         *                     nick = jsonObject.getString("nick");
         *
         *                     JSONObject battle_talent = jsonObject.getJSONObject("battle_talent");
         *                     normal_name = battle_talent.getString("normal_name");
         *                     element_name = battle_talent.getString("element_name");
         *                     final_name = battle_talent.getString("final_name");
         *                 } catch (JSONException e) {
         *                     e.printStackTrace();
         *                 }
         *                 is.close();
         *             }
         *         } catch (IOException ex) {
         *             if(ex != null) {
         *                 Toast.makeText(context, "暫時沒有他/她的相關資料", Toast.LENGTH_SHORT).show();
         *             }
         *
         *         }
         */



        String json_base = LoadData("db/char_list.json");
        //Get data from JSON
        try {
            JSONArray array = new JSONArray(json_base);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                name = object.getString("name");
                element = object.getString("element");
                star = object.getInt("rare");
                isComing = object.getInt("isComing");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String char_atk_name = LoadData("db/char_atk_name.json");
        //Get data from JSON
        try {
            JSONArray array = new JSONArray(char_atk_name);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                name = object.getString("name");

                normal_zh = object.getString("normal_zh");
                element_zh = object.getString("element_zh");
                final_zh = object.getString("final_zh");

                normal_en = object.getString("normal_en");
                element_en = object.getString("element_en");
                final_en = object.getString("final_en");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog = new Dialog(context, R.style.NormalDialogStyle_N);
        View view = View.inflate(context, R.layout.menu_char_add, null);

        // Function method
        Button cancel = view.findViewById(R.id.menu_cancel);
        Button ok = view.findViewById(R.id.menu_ok);
        TextView menu_title = view.findViewById(R.id.menu_title);
        Button menu_char_lvl_before = view.findViewById(R.id.menu_char_lvl_before);
        Button menu_char_lvl_after = view.findViewById(R.id.menu_char_lvl_after);
        RatingBar menu_break_lvl_before_rating = view.findViewById(R.id.menu_break_lvl_before_rating);
        RatingBar menu_break_lvl_after_rating = view.findViewById(R.id.menu_break_lvl_after_rating);
        TextView menu_skill1_title = view.findViewById(R.id.menu_skill1_title);
        SeekBar menu_skill1_pb = view.findViewById(R.id.menu_skill1_pb);
        TextView menu_skill1_tv = view.findViewById(R.id.menu_skill1_tv);
        TextView menu_skill2_title = view.findViewById(R.id.menu_skill2_title);
        SeekBar menu_skill2_pb = view.findViewById(R.id.menu_skill2_pb);
        TextView menu_skill2_tv = view.findViewById(R.id.menu_skill2_tv);
        TextView menu_skill3_title = view.findViewById(R.id.menu_skill3_title);
        SeekBar menu_skill3_pb = view.findViewById(R.id.menu_skill3_pb);
        TextView menu_skill3_tv = view.findViewById(R.id.menu_skill3_tv);
        Switch menu_not_cal = view.findViewById(R.id.menu_not_cal);

        menu_title.setText("【"+nick+"】 "+getString(characters_rss.getCharByName(name)[1]));

        // Will set to check zh / en later
        menu_skill1_tv.setText(normal_zh);
        menu_skill2_tv.setText(element_zh);
        menu_skill3_tv.setText(final_zh);

        menu_char_lvl_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                npd.setLastValue(before_lvl);
                npd.setMaxValue(90);
                npd.setMinValue(1);
                npd.showDialog("LVL_BEFORE");
            }
        });

        menu_char_lvl_after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                npd.setLastValue(after_lvl);
                npd.setMaxValue(90);
                npd.setMinValue(before_lvl);
                npd.showDialog("LVL_AFTER");
            }
        });
        /**這邊取得自己所設置之模組回調*/
        npd.onDialogRespond = new NumberPickerDialog.OnDialogRespond() {
            @Override
            public void onRespond(int value , String XPR) {
                if(XPR.equals("LVL_BEFORE")){
                    before_lvl = value;
                    menu_char_lvl_before.setText(getString(R.string.curr_lvl)+String.valueOf(before_lvl));
                }else if(XPR.equals("LVL_AFTER")){
                    after_lvl = value;
                    menu_char_lvl_after.setText(getString(R.string.curr_lvl)+String.valueOf(after_lvl));
                }
            }
        };

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        //view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight()));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        dialog.show();


    }

    private void viewPagerCharSetup(){
        mList_char = findViewById(R.id.main_list);
        mCharAdapter = new CharactersAdapter(context, charactersList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 3);
        LinearLayout.LayoutParams paramsMsg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsMsg.gravity = Gravity.CENTER;
        mList_char.setLayoutManager(mLayoutManager);
        mList_char.setLayoutParams(paramsMsg);
        mList_char.setAdapter(mCharAdapter);
        mList_char.removeAllViewsInLayout();
        char_list_reload();
        EditText char_et = findViewById(R.id.char_et);
        char_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<Characters> filteredList = new ArrayList<>();
                int x = 0;
                for (Characters item : charactersList) {
                    String str = String.valueOf(s).toLowerCase();
                    if (item.getName().toLowerCase().contains(String.valueOf(str))||css.LocaleStr(x,context).contains(String.valueOf(s))||css.LocaleStr(x,context).toLowerCase().contains(String.valueOf(s).toLowerCase())) {
                        filteredList.add(item);
                    }
                    x = x +1;
                }
                mCharAdapter.filterList(filteredList);
            }
        });

        ImageView char_filter = findViewById(R.id.char_filter);
        char_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context, R.style.NormalDialogStyle_N);
                View view = View.inflate(context, R.layout.menu_char_filter, null);
                // Elements
                ImageView pyro = view.findViewById(R.id.pyro_ico);
                ImageView hydro = view.findViewById(R.id.hydro_ico);
                ImageView anemo = view.findViewById(R.id.anemo_ico);
                ImageView electro = view.findViewById(R.id.electro_ico);
                ImageView dendor = view.findViewById(R.id.dendor_ico);
                ImageView cryo = view.findViewById(R.id.cryo_ico);
                ImageView geo = view.findViewById(R.id.geo_ico);
                // Weapons
                ImageView ico_sword = view.findViewById(R.id.ico_sword);
                ImageView ico_claymore = view.findViewById(R.id.ico_claymore);
                ImageView ico_polearm = view.findViewById(R.id.ico_polearm);
                ImageView ico_bow = view.findViewById(R.id.ico_bow);
                ImageView ico_catalyst = view.findViewById(R.id.ico_catalyst);
                // Rating
                RatingBar ratingBar = view.findViewById(R.id.menu_rating);
                // Function Buttons
                Button cancel = view.findViewById(R.id.menu_cancel);
                Button reset = view.findViewById(R.id.menu_reset);
                Button ok = view.findViewById(R.id.menu_ok);

                show_pyro = sharedPreferences.getBoolean("show_pyro",true);
                show_hydro = sharedPreferences.getBoolean("show_hydro",true);
                show_anemo = sharedPreferences.getBoolean("show_anemo",true);
                show_electro = sharedPreferences.getBoolean("show_electro",true);
                show_dendor = sharedPreferences.getBoolean("show_dendor",true);
                show_cryo = sharedPreferences.getBoolean("show_cryo",true);
                show_geo = sharedPreferences.getBoolean("show_geo",true);
                show_sword = sharedPreferences.getBoolean("show_sword",true);
                show_claymore = sharedPreferences.getBoolean("show_claymore",true);
                show_polearm = sharedPreferences.getBoolean("show_polearm",true);
                show_bow = sharedPreferences.getBoolean("show_bow",true);
                show_catalyst = sharedPreferences.getBoolean("show_catalyst",true);
                show_catalyst = sharedPreferences.getBoolean("show_catalyst",true);
                show_stars = sharedPreferences.getInt("char_stars",0);

                if(show_pyro){show_pyro = true;pyro.setColorFilter(Color.parseColor("#00000000"));}else{show_pyro = false;pyro.setColorFilter(Color.parseColor("#66313131"));}
                if(show_hydro){show_hydro = true;hydro.setColorFilter(Color.parseColor("#00000000"));}else{show_hydro = false;hydro.setColorFilter(Color.parseColor("#66313131"));}
                if(show_anemo){show_anemo = true;anemo.setColorFilter(Color.parseColor("#00000000"));}else{show_anemo = false;anemo.setColorFilter(Color.parseColor("#66313131"));}
                if(show_electro){show_electro = true;electro.setColorFilter(Color.parseColor("#00000000"));}else{show_electro = false;electro.setColorFilter(Color.parseColor("#66313131"));}
                if(show_dendor){show_dendor = true;dendor.setColorFilter(Color.parseColor("#00000000"));}else{show_dendor = false;dendor.setColorFilter(Color.parseColor("#66313131"));}
                if(show_cryo){show_cryo = true;cryo.setColorFilter(Color.parseColor("#00000000"));}else{show_cryo = false;cryo.setColorFilter(Color.parseColor("#66313131"));}
                if(show_geo){show_geo = true;geo.setColorFilter(Color.parseColor("#00000000"));}else{show_geo = false;geo.setColorFilter(Color.parseColor("#66313131"));}
                if(show_sword){show_sword = true;ico_sword.setColorFilter(Color.parseColor("#00000000"));}else{show_sword = false;ico_sword.setColorFilter(Color.parseColor("#66313131"));}
                if(show_claymore){show_claymore = true;ico_claymore.setColorFilter(Color.parseColor("#00000000"));}else{show_claymore = false;ico_claymore.setColorFilter(Color.parseColor("#66313131"));}
                if(show_polearm){show_polearm = true;ico_polearm.setColorFilter(Color.parseColor("#00000000"));}else{show_polearm = false;ico_polearm.setColorFilter(Color.parseColor("#66313131"));}
                if(show_bow){show_bow = true;ico_bow.setColorFilter(Color.parseColor("#00000000"));}else{show_bow = false;ico_bow.setColorFilter(Color.parseColor("#66313131"));}
                if(show_catalyst){show_catalyst = true;ico_catalyst.setColorFilter(Color.parseColor("#00000000"));}else{show_catalyst = false;ico_catalyst.setColorFilter(Color.parseColor("#66313131"));}
                ratingBar.setNumStars(5);
                ratingBar.setRating(show_stars);

                pyro.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_pyro){show_pyro = false;pyro.setColorFilter(Color.parseColor("#66313131"));}else{show_pyro = true;pyro.setColorFilter(Color.parseColor("#00000000"));}}});
                hydro.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_hydro){show_hydro = false;hydro.setColorFilter(Color.parseColor("#66313131"));}else{show_hydro = true;hydro.setColorFilter(Color.parseColor("#00000000"));}}});
                anemo.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_anemo){show_anemo = false;anemo.setColorFilter(Color.parseColor("#66313131"));}else{show_anemo = true;anemo.setColorFilter(Color.parseColor("#00000000"));}}});
                electro.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_electro){show_electro = false;electro.setColorFilter(Color.parseColor("#66313131"));}else{show_electro = true;electro.setColorFilter(Color.parseColor("#00000000"));}}});
                dendor.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_dendor){show_dendor = false;dendor.setColorFilter(Color.parseColor("#66313131"));}else{show_dendor = true;dendor.setColorFilter(Color.parseColor("#00000000"));}}});
                cryo.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_cryo){show_cryo = false;cryo.setColorFilter(Color.parseColor("#66313131"));}else{show_cryo = true;cryo.setColorFilter(Color.parseColor("#00000000"));}}});
                geo.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_geo){show_geo = false;geo.setColorFilter(Color.parseColor("#66313131"));}else{show_geo = true;geo.setColorFilter(Color.parseColor("#00000000"));}}});
                ico_sword.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_sword){show_sword = false;ico_sword.setColorFilter(Color.parseColor("#66313131"));}else{show_sword = true;ico_sword.setColorFilter(Color.parseColor("#00000000"));}}});
                ico_claymore.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_claymore){show_claymore = false;ico_claymore.setColorFilter(Color.parseColor("#66313131"));}else{show_claymore = true;ico_claymore.setColorFilter(Color.parseColor("#00000000"));}}});
                ico_polearm.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_polearm){show_polearm = false;ico_polearm.setColorFilter(Color.parseColor("#66313131"));}else{show_polearm = true;ico_polearm.setColorFilter(Color.parseColor("#00000000"));}}});
                ico_bow.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_bow){show_bow = false;ico_bow.setColorFilter(Color.parseColor("#66313131"));}else{show_bow = true;ico_bow.setColorFilter(Color.parseColor("#00000000"));}}});
                ico_catalyst.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { if(show_catalyst){show_catalyst = false;ico_catalyst.setColorFilter(Color.parseColor("#66313131"));}else{show_catalyst = true;ico_catalyst.setColorFilter(Color.parseColor("#00000000"));}}});

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show_pyro = true;
                        show_hydro = true;
                        show_anemo = true;
                        show_dendor = true;
                        show_electro = true;
                        show_cryo = true;
                        show_geo = true;

                        show_sword = true;
                        show_claymore = true;
                        show_polearm = true;
                        show_bow = true;
                        show_catalyst = true;

                        ratingBar.setRating(0);

                        editor.putBoolean("show_pyro",show_pyro);
                        editor.putBoolean("show_hydro",show_hydro);
                        editor.putBoolean("show_anemo",show_anemo);
                        editor.putBoolean("show_electro",show_electro);
                        editor.putBoolean("show_dendor",show_dendor);
                        editor.putBoolean("show_cryo",show_cryo);
                        editor.putBoolean("show_geo",show_geo);
                        editor.putBoolean("show_sword",show_sword);
                        editor.putBoolean("show_claymore",show_claymore);
                        editor.putBoolean("show_polearm",show_polearm);
                        editor.putBoolean("show_bow",show_bow);
                        editor.putBoolean("show_catalyst",show_catalyst);
                        editor.putInt("char_stars", (int) ratingBar.getRating());
                        editor.apply();
                        dialog.dismiss();

                        mCharAdapter.filterList(charactersList);

                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Characters> filteredList = new ArrayList<>();
                        for (Characters item : charactersList) {
                            if (item.getElement().toLowerCase().equals("pyro") && show_pyro||item.getElement().toLowerCase().equals("hydro") && show_hydro||item.getElement().toLowerCase().equals("anemo") && show_anemo||item.getElement().toLowerCase().equals("electro") && show_electro||item.getElement().toLowerCase().equals("dendor") && show_dendor||item.getElement().toLowerCase().equals("cryo") && show_cryo||item.getElement().toLowerCase().equals("geo") && show_geo) {
                                if(item.getWeapon().toLowerCase().equals("sword") && show_sword||item.getWeapon().toLowerCase().equals("claymore") && show_claymore||item.getWeapon().toLowerCase().equals("polearm") && show_polearm||item.getWeapon().toLowerCase().equals("bow") && show_bow||item.getWeapon().toLowerCase().equals("catalyst") && show_catalyst){
                                    if(ratingBar.getRating() != 0 && item.getRare() == ratingBar.getRating()){
                                        filteredList.add(item);
                                    }else if (ratingBar.getRating() == 0){
                                        filteredList.add(item);
                                    }
                                }
                            }
                        }

                        mList_char.removeAllViews();
                        mCharAdapter.filterList(filteredList);
                        editor.putBoolean("show_pyro",show_pyro);
                        editor.putBoolean("show_hydro",show_hydro);
                        editor.putBoolean("show_anemo",show_anemo);
                        editor.putBoolean("show_electro",show_electro);
                        editor.putBoolean("show_dendor",show_dendor);
                        editor.putBoolean("show_cryo",show_cryo);
                        editor.putBoolean("show_geo",show_geo);
                        editor.putBoolean("show_sword",show_sword);
                        editor.putBoolean("show_claymore",show_claymore);
                        editor.putBoolean("show_polearm",show_polearm);
                        editor.putBoolean("show_bow",show_bow);
                        editor.putBoolean("show_catalyst",show_catalyst);
                        editor.putInt("char_stars", (int) ratingBar.getRating());
                        editor.apply();
                        dialog.dismiss();
                    }
                });


                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(true);
                //view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight()));
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth());
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                dialogWindow.setAttributes(lp);
                dialog.show();
            }
        });
    }


    private void char_list_reload() {
        Log.wtf("DAAM","YEE");
        String name ,element,weapon,nation,sex;
        int rare,isComing;
        charactersList.clear();

        String json_base = LoadData("db/char_list.json");
        //Get data from JSON
        try {
            JSONArray array = new JSONArray(json_base);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                name = object.getString("name");
                element = object.getString("element");
                weapon = object.getString("weapon");
                nation = object.getString("nation");
                sex = object.getString("sex");
                rare = object.getInt("rare");
                isComing = object.getInt("isComing");

                Characters characters = new Characters();
                characters.setName(name);
                characters.setElement(element);
                characters.setWeapon(weapon);
                characters.setNation(nation);
                characters.setSex(sex);
                characters.setRare(rare);
                characters.setIsComing(isComing);
                charactersList.add(characters);
            }
            mCharAdapter.filterList(charactersList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String LoadData(String inFile) {
        String tContents = "";

        try {
            InputStream stream = getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }

        return tContents;

    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }


    public class MyViewPagerAdapter extends PagerAdapter
    {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews)
        {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View view = mListViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount()
        {
            return  mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

    }
}