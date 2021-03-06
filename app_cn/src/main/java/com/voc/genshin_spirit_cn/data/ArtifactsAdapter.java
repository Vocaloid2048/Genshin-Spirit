package com.voc.genshin_spirit_cn.data;/*
 * Package com.voc.genshin_spirit_cn.data.ArtifactsAdapter was
 * Created by Voc-夜芷冰 , Programmer of Xectorda
 * Copyright © 2021 Xectorda 版權所有
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.voc.genshin_spirit_cn.R;
import com.voc.genshin_spirit_cn.ui.CalculatorUI;
import com.voc.genshin_spirit_cn.ui.MainActivity;
import com.voc.genshin_spirit_cn.util.CustomToast;
import com.voc.genshin_spirit_cn.util.FileLoader;
import com.voc.genshin_spirit_cn.util.RoundedCornersTransformation;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/*
 * Project Genshin Spirit (原神小幫手) was
 * Created & Develop by Voc-夜芷冰 , Programmer of Xectorda
 * Copyright © 2022 Xectorda 版權所有
 */

/* loaded from: classes.dex */
public class ArtifactsAdapter extends RecyclerView.Adapter<ArtifactsAdapter.ViewHolder> {
    private List<Artifacts> artifactsList;
    private Context context;
    private AdapterView.OnItemClickListener mListener;
    private WebView webView;
    private Activity activity;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public ArtifactsAdapter(Context context, List<Artifacts> list,Activity activity) {
        this.context = context;
        this.artifactsList = list;
        this.activity = activity;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = this.context;
        if (context instanceof MainActivity) {
            View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artifact_ico, viewGroup, false);
            if (((MainActivity) this.context).sharedPreferences.getString("curr_ui_grid", "2").equals("2")) {
                inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artifact_ico, viewGroup, false);
            } else if (((MainActivity) this.context).sharedPreferences.getString("curr_ui_grid", "2").equals("3")) {
                inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artifact_ico_square, viewGroup, false);
            }
            return new ViewHolder(inflate, (OnItemClickListener) this.mListener);
        } else if (context instanceof CalculatorUI) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artifact_ico_square, viewGroup, false), (OnItemClickListener) this.mListener);
        } else {
            return null;
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int width = 0, height = 0;
        Artifacts artifacts = this.artifactsList.get(i);
        ItemRss itemRss = new ItemRss();
        RoundedCornersTransformation roundedCornersTransformation = new RoundedCornersTransformation(25, 0, RoundedCornersTransformation.CornerType.TOP);
        viewHolder.str = artifacts.getName();
        viewHolder.artifact_name.setText(artifacts.getName());
        viewHolder.artifact_base_name.setText(artifacts.getBaseName());
        if (artifacts.getIsComing() == 0) {
            viewHolder.artifact_isComing.setVisibility(View.GONE);
        }
        if (artifacts.getIsComing() == 1) {
            viewHolder.artifact_isComing.setVisibility(View.VISIBLE);
        }
        if (artifacts.getRare() > 0 && artifacts.getRare() < 6) {
            viewHolder.artifact_star.setNumStars(artifacts.getRare());
            viewHolder.artifact_star.setRating((float) artifacts.getRare());
        }
        Context context = this.context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height_curr = displayMetrics.heightPixels;
        int width_curr = displayMetrics.widthPixels;
        int curr = width_curr;

        Drawable drawable = context.getResources().getDrawable(R.drawable.item_selected_circle_effect);
        viewHolder.artifact_icon.setForeground(drawable);
        int size_per_img = width_curr/2;
        int size_per_img_sq = width_curr/3;

        if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            size_per_img = 480;
            size_per_img_sq = 360;
        }

        if(context instanceof MainActivity){

            if (((MainActivity) this.context).sharedPreferences.getString("curr_ui_grid", "2").equals("2")) {
                if(width_curr / ((int)width_curr/size_per_img+1) > size_per_img*0.75){
                    width = (width_curr) / ((int)width_curr/size_per_img+1);
                    height = (width * 58) / 32;
                }else{
                    width = size_per_img;
                    height = (width * 58) / 32;
                }
            } else if (((MainActivity) this.context).sharedPreferences.getString("curr_ui_grid", "2").equals("3")) {
                if(width_curr / ((int)width_curr/size_per_img_sq+1) > size_per_img_sq*0.75){
                    width = (width_curr) / ((int)width_curr/size_per_img_sq+1);
                    height = (width_curr) / ((int)width_curr/size_per_img_sq+1);
                }else{
                    width = size_per_img_sq;
                    height = size_per_img_sq;
                }
            }


        }else if(context instanceof CalculatorUI){
            if(width_curr / ((int)width_curr/size_per_img_sq+1) > size_per_img_sq*0.75){
                width = (width_curr) / ((int)width_curr/size_per_img_sq+1);
                height = (width_curr) / ((int)width_curr/size_per_img_sq+1);
            }else{
                width = size_per_img_sq;
                height = size_per_img_sq;
            }
        }
        int one_curr = height;
        if(height > width){
            one_curr = width;
        }
        viewHolder.artifact_bg.setBackgroundResource(itemRss.getRareColorByName(artifacts.getRare())[0]);
        viewHolder.artifact_nl.setBackgroundResource(itemRss.getRareColorByName(artifacts.getRare())[1]);
        viewHolder.artifact_icon.getLayoutParams().width = width;
        viewHolder.artifact_icon.getLayoutParams().height = height;
        Context context2 = this.context;
        if (context2 instanceof MainActivity) {
            if (((MainActivity) context2).sharedPreferences.getString("curr_ui_grid", ExifInterface.GPS_MEASUREMENT_2D).equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                Picasso.get().load(itemRss.getArtifactByName(artifacts.getName())[1]).fit().centerInside().transform(roundedCornersTransformation).error(R.drawable.paimon_full).into(viewHolder.artifact_icon);
            } else if (((MainActivity) this.context).sharedPreferences.getString("curr_ui_grid", ExifInterface.GPS_MEASUREMENT_3D).equals(ExifInterface.GPS_MEASUREMENT_3D)) {
                Picasso.get().load(itemRss.getArtifactByName(artifacts.getName())[1]).resize(one_curr,one_curr).transform(roundedCornersTransformation).error(R.drawable.paimon_full).into(viewHolder.artifact_icon);
            }
        } else if (context2 instanceof CalculatorUI) {
            Picasso.get().load(itemRss.getArtifactByName(artifacts.getName())[1]).resize(one_curr,one_curr).transform(roundedCornersTransformation).error(R.drawable.paimon_full).into(viewHolder.artifact_icon);
        }
        viewHolder.artifact_name.setText(itemRss.getArtifactByName(artifacts.getName())[0]);
        new ColorStateList(new int[][]{new int[]{16842919}, new int[]{-16842912}, new int[]{16842912}}, new int[]{this.context.getResources().getColor(R.color.tv_color), this.context.getResources().getColor(R.color.tv_color), Color.parseColor(this.context.getSharedPreferences("user_info", 0).getString("theme_color_hex", "#FF5A5A"))});
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.artifactsList.size();
    }

    /* loaded from: classes.dex */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView artifact_base_name;
        public ConstraintLayout artifact_bg;
        public ImageView artifact_element;
        public ImageView artifact_icon;
        public ImageView artifact_isComing;
        public TextView artifact_name;
        public LinearLayout artifact_nl;
        public RatingBar artifact_star;
        public TextView artifact_stat1;
        public TextView artifact_stat2;
        public String str;

        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            this.artifact_icon = (ImageView) view.findViewById(R.id.artifact_img);
            this.artifact_name = (TextView) view.findViewById(R.id.artifact_name);
            this.artifact_star = (RatingBar) view.findViewById(R.id.artifact_star);
            this.artifact_isComing = (ImageView) view.findViewById(R.id.artifact_is_coming);
            this.artifact_base_name = (TextView) view.findViewById(R.id.artifact_base_name);
            this.artifact_nl = (LinearLayout) view.findViewById(R.id.artifact_nl);
            this.artifact_bg = (ConstraintLayout) view.findViewById(R.id.artifact_bg);
            this.artifact_icon.setOnClickListener(new View$OnClickListenerC10641(ArtifactsAdapter.this));
        }

        /* renamed from: com.voc.genshin_spirit_cn.data.ArtifactsAdapter$ViewHolder$1 */
        /* loaded from: classes.dex */
        class View$OnClickListenerC10641 implements View.OnClickListener {
            final /* synthetic */ ArtifactsAdapter val$this$0;

            View$OnClickListenerC10641(ArtifactsAdapter artifactsAdapter) {
                this.val$this$0 = artifactsAdapter;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ImageView imageView;
                Log.wtf("is context instanceof MainActivity ?", ArtifactsAdapter.this.context.getPackageName());
                if (ArtifactsAdapter.this.context instanceof MainActivity) {
                    Log.wtf("YES", "IT's");
                    Log.wtf("BETA", String.valueOf(ViewHolder.this.artifact_base_name.getText()));
                    String valueOf = String.valueOf(ViewHolder.this.artifact_base_name.getText());
                    SharedPreferences sharedPreferences = ArtifactsAdapter.this.context.getSharedPreferences("user_info", 0);
                    new ItemRss();
                    String string = sharedPreferences.getString("curr_lang", "zh-HK");
                    //AssetManager assets = ArtifactsAdapter.this.context.getAssets();
                    Log.wtf("CharName_BASE", valueOf);
                    String replace = valueOf.replace("'", "_");
                    Log.wtf("lang", string);
                    String str = LoadData("db/artifacts/" + string + "/" + replace + ".json");

                    if (str == null) {
                        str = LoadData("db/artifacts/en-US/" + replace + ".json");
                    }
                    if (str != null) {
                        try {
                            JSONObject jSONObject = new JSONObject(str);
                            String str4 = ViewHolder.this.str;
                            String str5 = (String) ViewHolder.this.artifact_name.getText();
                            JSONArray jSONArray = jSONObject.getJSONArray("rarity");
                            int parseInt = Integer.parseInt((String) jSONArray.get(jSONArray.length() - 1));
                            Dialog dialog = new Dialog(ArtifactsAdapter.this.context, R.style.NormalDialogStyle_N);
                            View inflate = View.inflate(ArtifactsAdapter.this.context, R.layout.item_artifact_info, null);
                            ItemRss itemRss = new ItemRss();
                            RoundedCornersTransformation roundedCornersTransformation = new RoundedCornersTransformation(25, 0, RoundedCornersTransformation.CornerType.TOP);
                            ImageView imageView2 = (ImageView) inflate.findViewById(R.id.item_img);
                            LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.item_nl);
                            RatingBar ratingBar = (RatingBar) inflate.findViewById(R.id.item_star);
                            ImageView imageView3 = (ImageView) inflate.findViewById(R.id.item_is_coming);
                            TextView textView = (TextView) inflate.findViewById(R.id.item_2pcs);
                            TextView textView2 = (TextView) inflate.findViewById(R.id.item_4pcs);
                            ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.item_bg);
                            ImageView imageView4 = (ImageView) inflate.findViewById(R.id.info_item1);
                            ImageView imageView5 = (ImageView) inflate.findViewById(R.id.info_item2);
                            ImageView imageView6 = (ImageView) inflate.findViewById(R.id.info_item3);
                            ImageView imageView7 = (ImageView) inflate.findViewById(R.id.info_item4);
                            ImageView imageView8 = (ImageView) inflate.findViewById(R.id.info_item5);
                            Picasso.get().load(itemRss.getArtifactByName(str4)[1]).fit().centerInside().transform(roundedCornersTransformation).error(R.drawable.paimon_lost).into(imageView2);
                            ((TextView) inflate.findViewById(R.id.item_name)).setText(ViewHolder.this.artifact_name.getText());
                            if (jSONObject.has("1pc")) {
                                StringBuilder sb = new StringBuilder();
                                imageView = imageView4;
                                sb.append(ArtifactsAdapter.this.context.getString(R.string.artifact_stat1));
                                sb.append(" : ");
                                sb.append(jSONObject.getString("1pc"));
                                textView.setText(sb.toString());
                                textView2.setVisibility(View.GONE);
                            } else {
                                imageView = imageView4;
                                textView.setText(ArtifactsAdapter.this.context.getString(R.string.artifact_stat2) + " : " + jSONObject.getString("2pc"));
                                textView2.setText(ArtifactsAdapter.this.context.getString(R.string.artifact_stat4) + " : " + jSONObject.getString("4pc"));
                            }
                            ratingBar.setNumStars(parseInt);
                            ratingBar.setRating((float) parseInt);
                            imageView2.setBackgroundResource(itemRss.getRareColorByName(parseInt)[0]);
                            linearLayout.setBackgroundResource(itemRss.getRareColorByName(parseInt)[1]);
                            Picasso.get().load(itemRss.getArtifactByName(str4)[1]).fit().centerInside().transform(roundedCornersTransformation).error(R.drawable.paimon_lost).into(imageView);
                            imageView.setBackgroundResource(itemRss.getRareColorByName(parseInt)[0]);
                            if (itemRss.getArtifactByName(str4).length > 2) {
                                imageView5.setVisibility(View.VISIBLE);
                                imageView6.setVisibility(View.VISIBLE);
                                imageView7.setVisibility(View.VISIBLE);
                                imageView8.setVisibility(View.VISIBLE);
                                Picasso.get().load(itemRss.getArtifactByName(str4)[2]).fit().centerInside().transform(roundedCornersTransformation).error(R.drawable.paimon_lost).into(imageView5);
                                imageView5.setBackgroundResource(itemRss.getRareColorByName(parseInt)[0]);
                                Picasso.get().load(itemRss.getArtifactByName(str4)[3]).fit().centerInside().transform(roundedCornersTransformation).error(R.drawable.paimon_lost).into(imageView6);
                                imageView6.setBackgroundResource(itemRss.getRareColorByName(parseInt)[0]);
                                Picasso.get().load(itemRss.getArtifactByName(str4)[4]).fit().centerInside().transform(roundedCornersTransformation).error(R.drawable.paimon_lost).into(imageView7);
                                imageView7.setBackgroundResource(itemRss.getRareColorByName(parseInt)[0]);
                                Picasso.get().load(itemRss.getArtifactByName(str4)[5]).fit().centerInside().transform(roundedCornersTransformation).error(R.drawable.paimon_lost).into(imageView8);
                                imageView8.setBackgroundResource(itemRss.getRareColorByName(parseInt)[0]);
                            }
                            if (ViewHolder.this.artifact_isComing.getVisibility() == View.VISIBLE) {
                                imageView3.setVisibility(View.VISIBLE);
                            } else {
                                imageView3.setVisibility(View.GONE);
                            }
                            dialog.setContentView(inflate);
                            dialog.setCanceledOnTouchOutside(true);
                            Window window = dialog.getWindow();
                            WindowManager.LayoutParams attributes = window.getAttributes();
                            attributes.width = ScreenSizeUtils.getInstance(ArtifactsAdapter.this.context).getScreenWidth();
                            attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            attributes.gravity = Gravity.BOTTOM;
                            window.setAttributes(attributes);
                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //Toast.makeText(ArtifactsAdapter.this.context, ArtifactsAdapter.this.context.getString(R.string.none_info), Toast.LENGTH_SHORT).show();
                        CustomToast.toast(context,view,context.getString(R.string.none_info));
                    }
                } else if (ArtifactsAdapter.this.context instanceof CalculatorUI) {
                    Log.wtf("YES", "IT's");
                    if (!((CalculatorUI) ArtifactsAdapter.this.context).checkNameList().contains(String.valueOf(ViewHolder.this.artifact_base_name.getText()).replace("_", StringUtils.SPACE))) {
                        Log.w("WEAPON_BASE", (String) ViewHolder.this.artifact_base_name.getText());
                        ((CalculatorUI) context).artifactQuestion(String.valueOf(artifact_base_name.getText()), "ADD", 0, (int) artifact_star.getRating());
                        return;
                    } else {
                        //Toast.makeText((CalculatorUI) ArtifactsAdapter.this.context, ArtifactsAdapter.this.context.getString(R.string.cal_choosed_already), Toast.LENGTH_SHORT).show();
                        CustomToast.toast(context,view,context.getString(R.string.cal_choosed_already));
                    }
                }
            }
        }
    }

    public void filterList(List<Artifacts> list) {
        this.artifactsList = list;
        notifyDataSetChanged();
    }

    public String LoadData(String inFile) {
        String tContents = "";

        try {
            InputStream stream = context.getAssets().open(inFile);

            int size = stream.available();
            System.out.println("size"+ size);
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }

        return tContents;

    }
}