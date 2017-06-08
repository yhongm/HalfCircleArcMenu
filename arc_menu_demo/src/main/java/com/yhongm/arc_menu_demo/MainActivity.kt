package com.yhongm.arc_menu_demo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import com.yhongm.arcmenu.ArcMenu
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by yuhongmiao on 2017/6/6.
 */

class MainActivity : Activity(), ArcMenu.OnMenuItemClickListener {
    internal var colorButton = intArrayOf(R.drawable.one_btn, R.drawable.second_btn, R.drawable.three_btn, R.drawable.four_btn, R.drawable.five_btn)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arcMenu!!.setOnMenuItemClickListener(this)
        arcMenu!!.currentStatus = ArcMenu.Status.CLOSE
        seekBar!!.max = 9
        seekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (progress == 0) {
                        seekBar.progress = 1
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.i("MainActivity", "onStopTrackingTouch,seekBar:" + seekBar.progress)// yuhongmiao 2017/6/6 下午9:03

                tvDescribe!!.text = "当前设置的菜单按钮数量:" + seekBar.progress
                arcMenu!!.setShowMenuBtnNum(seekBar.progress)
            }
        })

        for (i in 0..9) {
            arcMenu!!.addChildArcMenu(colorButton[i % 5], "测试:" + i, "额外信息:" + i)
        }
        arcMenu!!.layoutChildMenu()
        seekBar!!.progress = 3
    }

    override fun onClickMenu(view: View, pos: Int, extraInfo: String) {//HalfCircleArcMenu 一半
        Toast.makeText(this, "pos:$pos,extraInfo:$extraInfo", Toast.LENGTH_SHORT).show()
    }
}
