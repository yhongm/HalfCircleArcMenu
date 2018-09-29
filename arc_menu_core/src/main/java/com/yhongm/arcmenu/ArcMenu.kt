package com.yhongm.arcmenu

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import java.util.ArrayList


class ArcMenu constructor(mContext: Context, attrs: AttributeSet? = null) : ViewGroup(mContext, attrs), OnClickListener {
    /**
     * 当前添加view的顺序
     */
    private var currentAddViewIndex = 1
    /**
     * 菜单显示的半径
     */
    private val mRadius = 180

    /**
     * 用户点击的按钮
     */
    private var mButton: View? = null
    /**
     * 当前ArcMenu的状态
     */
    /**
     * 获取当前菜单状态
     *
     * @return
     */
    /**
     * 设置当前菜单状态
     *
     * @param mCurrentStatus
     */
    var currentStatus = Status.CLOSE
    /**
     * 回调接口
     */
    private var onMenuItemClickListener: OnMenuItemClickListener? = null


    /**
     * 中间主按钮
     */
    lateinit var mMainButton: View
    private var showMenuBtnNum = 5//第一排显示的扇形菜单子数量
    private val mChildMenuViews = ArrayList<View>()

    init {
        val a = mContext.obtainStyledAttributes(attrs,
                R.styleable.ArcMenu)
        a.getResourceId(R.styleable.ArcMenu_main_btn_img, R.drawable.main_btn)
        a.recycle()
        addMainButton()
    }


    /**
     * 添加主按钮
     */
    private fun addMainButton() {
        val linearLayout = LinearLayout(context)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val iv = ImageView(context)
        val ivLayoutParams = LinearLayout.LayoutParams(120, 120)
        iv.setImageResource(R.drawable.main_btn)
        linearLayout.addView(iv, ivLayoutParams)
        addView(linearLayout, 0, layoutParams)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            childView.measure(0, 0)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 设置子菜单按钮显示的数量
     *
     * @param num
     */
    fun setShowMenuBtnNum(num: Int) {
        if (num < 1 || num > 9) {
            throw IllegalArgumentException("菜单按钮的数量只能在1到9之间")
        }
        this.showMenuBtnNum = num
        if (currentStatus == Status.OPEN) {
            layoutChildButtonToggleMenu(300)
            for (i in 1 until childCount) {
                val childView = getChildAt(i)
                childView.visibility = View.GONE
            }
        }

    }

    /**
     * 初始化子菜单
     */
    fun layoutChildMenu() {
        clearOldChildMenuViews()
        for (childMenuView in mChildMenuViews) {
            addViewInLayout(childMenuView, currentAddViewIndex, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            currentAddViewIndex++
        }
        invalidate()
        requestLayout()
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.i("ArcMenu", "onLayout,")// yuhongmiao 2017/6/6 下午7:49

        if (changed) {
            layoutMainButton()
            layoutChildButtonToggleMenu(300)

        }

    }

    /**
     * 添加菜单按钮
     *
     * @param id        按钮资源id
     * @param title     按钮标题
     * @param extraInfo 额外信息
     */
    fun addChildArcMenu(id: Int, title: String, extraInfo: String) {
        val childMenuView = getChildMenuView(id, title, extraInfo)

        mChildMenuViews.add(childMenuView)

    }

    /**
     * 获取菜单view
     *
     * @param resoureceId
     * @param title
     * @param extraInfo
     * @return
     */
    private fun getChildMenuView(resoureceId: Int, title: String, extraInfo: String): View {
        val childMenu = LinearLayout(context)
        childMenu.orientation = LinearLayout.VERTICAL
        childMenu.gravity = Gravity.CENTER
        val ivIcon = ImageView(context)
        ivIcon.setImageResource(resoureceId)
        val ivIconLayoutParams = LinearLayout.LayoutParams(80, 80)

        val tvTitle = TextView(context)
        tvTitle.text = title
        tvTitle.textSize = 10f
        tvTitle.setTextColor(Color.parseColor("#ffffff"))
        val tvTitleLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        childMenu.addView(ivIcon, ivIconLayoutParams)
        tvTitleLayoutParams.topMargin = 4
        childMenu.addView(tvTitle, tvTitleLayoutParams)
        childMenu.tag = extraInfo
        return childMenu
    }

    /**
     * 摆放主按钮，设置主按钮的点击事件
     */
    private fun layoutMainButton() {

        mMainButton = getChildAt(0)

        mMainButton.setOnClickListener(this)

        var l = 0
        var t = 0
        val width = mMainButton.measuredWidth
        val height = mMainButton.measuredHeight

        l = measuredWidth / 2 - width / 2
        t = measuredHeight - 100 - height / 2
        mMainButton.layout(l, t, l + width, t + height)
    }

    /**
     * 为按钮添加点击事件
     */
    override fun onClick(v: View) {
        if (mButton == null) {
            mButton = getChildAt(0)
        }
        layoutChildButtonToggleMenu(300)
    }

    /**
     * 按钮子按钮的布局与动画
     *
     * @param durationMillis
     */
    private fun layoutChildButtonToggleMenu(durationMillis: Int) {
        setBackgroundColor(Color.parseColor("#66111111"))
        for (i in 0 until showMenuBtnNum) {

            val childView = getChildAt(i + 1)
            childView.visibility = View.VISIBLE
            val cWidth = childView.measuredWidth
            val cHeight = childView.measuredHeight
            val centerX = measuredWidth / 2 - cWidth / 2
            val centerY = measuredHeight - 100 - cHeight / 2
            var cl = 0
            var ct = 0
            var angle: Double? = null
            if (i < showMenuBtnNum) {
                var verticalNum = (showMenuBtnNum / 2).toFloat()
                if (showMenuBtnNum % 2 == 0) {
                    verticalNum = verticalNum - 0.5f
                    if (i < verticalNum) {
                        val currntAngle = Math.PI / 2.0 / verticalNum.toDouble() * (verticalNum - i)
                        angle = currntAngle
                        cl = (mRadius * Math.sin(angle!!)).toInt()
                        ct = (mRadius * Math.cos(angle!!)).toInt()
                        cl = centerX - cl
                        ct = centerY - ct
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight)

                    } else if (i > verticalNum) {
                        val currentAngle = Math.PI / 2.0 / verticalNum.toDouble() * (i - verticalNum)
                        angle = currentAngle
                        cl = (mRadius * Math.sin(angle!!)).toInt()
                        ct = (mRadius * Math.cos(angle!!)).toInt()
                        cl += centerX
                        ct = centerY - ct
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight)

                    }
                } else {
                    if (i < verticalNum) {
                        val currntAngle = Math.PI / 2.0 / verticalNum.toDouble() * (verticalNum - i)
                        angle = currntAngle
                        cl = (mRadius * Math.sin(angle!!)).toInt()
                        ct = (mRadius * Math.cos(angle!!)).toInt()
                        cl = centerX - cl
                        ct = centerY - ct
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight)

                    } else if (i.toFloat() == verticalNum) {
                        var currentAngle = 0.0
                        if (verticalNum == 0f) {
                            currentAngle = 0.0
                        } else {
                            currentAngle = Math.PI / 2.0 / verticalNum.toDouble() * (verticalNum - i)
                        }
                        angle = currentAngle
                        cl = (mRadius * Math.sin(angle)).toInt()
                        ct = (mRadius * Math.cos(angle)).toInt()
                        cl = centerX - cl
                        ct = centerY - ct
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight)

                    } else if (i > verticalNum && i < showMenuBtnNum) {
                        val currentAngle = Math.PI / 2.0 / verticalNum.toDouble() * (i - verticalNum)
                        angle = currentAngle
                        cl = (mRadius * Math.sin(angle!!)).toInt()
                        ct = (mRadius * Math.cos(angle!!)).toInt()
                        cl += centerX
                        ct = centerY - ct
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight)

                    }
                }


            }
            val centrePointX = (getChildAt(0).x + getChildAt(0).width / 2 - childView.width / 2).toInt()
            val centrePointY = (getChildAt(0).y + getChildAt(0).height / 2 - childView.height / 2).toInt()
            val viewX = (centrePointX - cl).toFloat()
            val viewY = (centrePointY - ct).toFloat()
            toggleMenuAnim(durationMillis, i, childView, viewX, viewY)

        }
        changeMenuStatus()
    }

    /**
     * 切换菜单动画
     *
     * @param durationMillis 动画执行时间
     * @param i              第几个按钮
     * @param childView      按钮view
     * @param viewX          x坐标
     * @param viewY          y坐标
     */
    private fun toggleMenuAnim(durationMillis: Int, i: Int, childView: View, viewX: Float, viewY: Float) {
        val animset = AnimationSet(true)
        animset.interpolator = AccelerateDecelerateInterpolator()
        val rotateAnimation = RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.duration = durationMillis.toLong()
        rotateAnimation.fillAfter = true
        var translateAnimation: Animation? = null
        if (currentStatus == Status.OPEN) {
            translateAnimation = TranslateAnimation(0f, viewX, 0f, viewY)
            childView.isClickable = true
            childView.isFocusable = true
        } else if (currentStatus == Status.CLOSE) {
            translateAnimation = TranslateAnimation(viewX, 0f, viewY, 0f)
            childView.isClickable = false
            childView.isFocusable = false
        }
        translateAnimation!!.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                if (currentStatus == Status.CLOSE)
                    childView.visibility = View.GONE

            }
        })

        translateAnimation.fillAfter = true
        translateAnimation.duration = durationMillis.toLong()

        animset.addAnimation(rotateAnimation)
        animset.addAnimation(translateAnimation)
        childView.startAnimation(animset)
        childView.setOnClickListener {
            if (onMenuItemClickListener != null)
                onMenuItemClickListener!!.onClickMenu(childView, i, childView.tag as String)
            setBackgroundColor(Color.TRANSPARENT)
            menuItemAnin(i)
            changeMenuStatus()
        }
    }

    /**
     * 修改菜单开关的状态
     */
    private fun changeMenuStatus() {
        currentStatus = if (currentStatus == Status.CLOSE)
            Status.OPEN
        else
            Status.CLOSE
    }

    /**
     * 开始菜单动画，点击的MenuItem放大消失，其他的缩小消失
     *
     * @param item 点击的第几个按钮
     */
    private fun menuItemAnin(item: Int) {
        for (i in 0 until showMenuBtnNum) {
            val childView = getChildAt(i + 1)
            if (i == item) {
                childView.startAnimation(scaleAnim(300, false))
            } else {
                childView.startAnimation(scaleAnim(300, true))
            }
            childView.isClickable = false
            childView.isFocusable = false

        }

    }

    /**
     * 缩放动画
     *
     * @param durationMillis 动画执行时间
     * @param isSmall        缩小或者放大 true为缩小，false为放大
     * @return
     */
    private fun scaleAnim(durationMillis: Int, isSmall: Boolean): Animation {
        if (isSmall) {
            val anim = ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
            anim.duration = durationMillis.toLong()
            anim.fillAfter = true
            return anim
        } else {
            val anims = AnimationSet(true)

            val anim = ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
            val alphaAnimation = AlphaAnimation(1f, 0f)
            anims.addAnimation(anim)
            anims.addAnimation(alphaAnimation)
            anims.duration = durationMillis.toLong()
            anims.fillAfter = true
            return anims
        }
    }

    /**
     * 删除出了除第一个子按钮的方法
     */
    private fun clearOldChildMenuViews() {
        for (i in childCount - 1 downTo 1) {
            removeView(getChildAt(i))
        }
        currentAddViewIndex = 1
    }

    /**
     * 设置菜单点击监听
     *
     * @param onMenuItemClickListener
     */
    fun setOnMenuItemClickListener(
            onMenuItemClickListener: OnMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener
    }


    interface OnMenuItemClickListener {
        /**
         * @param view      点击的view
         * @param pos       点击的位置
         * @param extraInfo 点击的额外信息
         */
        fun onClickMenu(view: View, pos: Int, extraInfo: String)
    }


    /**
     * 菜单状态的枚举类
     */
    enum class Status {
        OPEN, CLOSE
    }


}
