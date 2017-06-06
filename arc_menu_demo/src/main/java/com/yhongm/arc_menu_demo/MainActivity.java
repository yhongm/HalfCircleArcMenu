package com.yhongm.arc_menu_demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yhongm.arcmenu.ArcMenu;

/**
 * Created by yuhongmiao on 2017/6/6.
 */

public class MainActivity extends Activity implements ArcMenu.OnMenuItemClickListener {
    private ArcMenu arcMenu;
    private SeekBar seekBar;
    TextView tvDescribe;//描述
    int[] colorButton = new int[]{R.drawable.one_btn, R.drawable.second_btn, R.drawable.three_btn, R.drawable.four_btn, R.drawable.five_btn};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arcMenu = (ArcMenu) findViewById(R.id.arcMenu);
        seekBar= (SeekBar) findViewById(R.id.seekBar);
        tvDescribe= (TextView) findViewById(R.id.tvDescribe);
        arcMenu.setOnMenuItemClickListener(this);
        arcMenu.setCurrentStatus(ArcMenu.Status.CLOSE);
        seekBar.setMax(9);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    if (progress==0){
                        seekBar.setProgress(1);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("MainActivity","onStopTrackingTouch,seekBar:"+seekBar.getProgress());// yuhongmiao 2017/6/6 下午9:03

                tvDescribe.setText("当前设置的菜单按钮数量:"+seekBar.getProgress());
                arcMenu.setShowMenuBtnNum(seekBar.getProgress());
            }
        });

        for (int i = 0; i < 10; i++) {
            arcMenu.addChildArcMenu(colorButton[i % 5], "测试:" + i,"额外信息:"+i);
        }
        arcMenu.layoutChildMenu();
        seekBar.setProgress(3);
    }

    @Override
    public void onClickMenu(View view, int pos, String extraInfo) {//HalfCircleArcMenu 一半
        Toast.makeText(this, "pos:" + pos+",extraInfo:"+extraInfo, Toast.LENGTH_SHORT).show();
    }
}
