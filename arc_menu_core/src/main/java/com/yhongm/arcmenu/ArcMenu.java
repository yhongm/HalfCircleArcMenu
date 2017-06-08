package com.yhongm.arcmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class ArcMenu extends ViewGroup implements OnClickListener {
    /**
     * 当前添加view的顺序
     */
    private int currentAddViewIndex = 1;
    /**
     * 菜单显示的半径
     */
    private int mRadius = 180;

    /**
     * 用户点击的按钮
     */
    private View mButton;
    /**
     * 当前ArcMenu的状态
     */
    private Status mCurrentMenuStatus = Status.CLOSE;
    /**
     * 回调接口
     */
    private OnMenuItemClickListener onMenuItemClickListener;


    /**
     * 中间主按钮
     */
    View cButton;
    Context mContext;
    private int showMenuBtnNum = 5;//第一排显示的扇形菜单子数量
    private ArrayList<View> mChildMenuViews = new ArrayList<>();

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ArcMenu);
        a.getResourceId(R.styleable.ArcMenu_main_btn_img, R.drawable.main_btn);
        a.recycle();
        mContext = context;
        addMainButton();
    }


    /**
     * 添加主按钮
     */
    private void addMainButton() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ImageView iv = new ImageView(getContext());
        LinearLayout.LayoutParams ivLayoutParams = new LinearLayout.LayoutParams(120, 120);
        iv.setImageResource(R.drawable.main_btn);
        linearLayout.addView(iv, ivLayoutParams);
        addView(linearLayout, 0, layoutParams);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(0, 0);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置子菜单按钮显示的数量
     *
     * @param num
     */
    public void setShowMenuBtnNum(int num) {
        if (num < 1 || num > 9) {
            throw new IllegalArgumentException("菜单按钮的数量只能在1到9之间");
        }
        this.showMenuBtnNum = num;
        if (mCurrentMenuStatus == Status.OPEN) {
            toggleMenu(300);
            for (int i = 1; i <= getChildCount() - 1; i++) {
                View childView = getChildAt(i);
                childView.setVisibility(GONE);
            }
        }

    }

    /**
     * 初始化子菜单
     */
    public void layoutChildMenu() {
        clearOldChildMenuViews();
        for (View childMenuView :
                mChildMenuViews) {
            addViewInLayout(childMenuView, currentAddViewIndex, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            currentAddViewIndex++;
        }
        invalidate();
        requestLayout();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i("ArcMenu", "onLayout,");// yuhongmiao 2017/6/6 下午7:49

        if (changed) {
            layoutButton();
            toggleMenu(300);

        }

    }

    /**
     * 添加菜单按钮
     *
     * @param id        按钮资源id
     * @param title     按钮标题
     * @param extraInfo 额外信息
     */
    public void addChildArcMenu(int id, String title, String extraInfo) {
        View childMenuView = getChildMenuView(id, title, extraInfo);

        mChildMenuViews.add(childMenuView);

    }

    /**
     * 获取菜单view
     *
     * @param resoureceId
     * @param title
     * @param extraInfo
     * @return
     */
    public View getChildMenuView(int resoureceId, String title, String extraInfo) {
        LinearLayout childMenu = new LinearLayout(getContext());
        childMenu.setOrientation(LinearLayout.VERTICAL);
        childMenu.setGravity(Gravity.CENTER);
        ImageView ivIcon = new ImageView(getContext());
        ivIcon.setImageResource(resoureceId);
        LinearLayout.LayoutParams ivIconLayoutParams = new LinearLayout.LayoutParams(80, 80);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(title);
        tvTitle.setTextSize(10);
        tvTitle.setTextColor(Color.parseColor("#ffffff"));
        LinearLayout.LayoutParams tvTitleLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        childMenu.addView(ivIcon, ivIconLayoutParams);
        tvTitleLayoutParams.topMargin = 4;
        childMenu.addView(tvTitle, tvTitleLayoutParams);
        childMenu.setTag(extraInfo);
        return childMenu;
    }

    /**
     * 第一个子view为按钮，初始化子按钮
     */
    private void layoutButton() {

        cButton = getChildAt(0);

        cButton.setOnClickListener(this);

        int l = 0;
        int t = 0;
        int width = cButton.getMeasuredWidth();
        int height = cButton.getMeasuredHeight();

        l = getMeasuredWidth() / 2 - width / 2;
        t = (getMeasuredHeight() - 100) - height / 2;
        cButton.layout(l, t, l + width, t + height);
    }

    /**
     * 为按钮添加点击事件
     */
    @Override
    public void onClick(View v) {
        if (mButton == null) {
            mButton = getChildAt(0);
        }
        toggleMenu(300);
    }

    /**
     * 按钮子按钮的布局与动画
     *
     * @param durationMillis
     */
    private void toggleMenu(int durationMillis) {
        setBackgroundColor(Color.parseColor("#66111111"));
        for (int i = 0; i < showMenuBtnNum; i++) {

            View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);
            int childIndex = i;
            int cWidth = childView.getMeasuredWidth();
            int cHeight = childView.getMeasuredHeight();
            int centerX = getMeasuredWidth() / 2 - cWidth / 2;
            int centerY = (getMeasuredHeight() - 100) - cHeight / 2;
            int cl = 0;
            int ct = 0;
            Double angle = null;
            if (childIndex < showMenuBtnNum) {
                float verticalNum = showMenuBtnNum / 2;
                if (showMenuBtnNum % 2 == 0) {
                    verticalNum = verticalNum - 0.5f;
                    if (childIndex < verticalNum) {
                        double currntAngle = (Math.PI / 2 / (verticalNum) * (verticalNum - childIndex));
                        angle = currntAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX - cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);

                    } else if (childIndex > verticalNum) {
                        double currentAngle = (Math.PI / 2 / (verticalNum) * (childIndex - verticalNum));
                        angle = currentAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX + cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);

                    }
                } else {
                    if (childIndex < verticalNum) {
                        double currntAngle = (Math.PI / 2 / (verticalNum) * (verticalNum - childIndex));
                        angle = currntAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX - cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);

                    } else if (childIndex == verticalNum) {
                        double currentAngle = 0;
                        if (verticalNum == 0) {
                            currentAngle = 0;
                        } else {
                            currentAngle = ((Math.PI / 2 / (verticalNum)) * (verticalNum - childIndex));
                        }
                        angle = currentAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX - cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);

                    } else if (childIndex > verticalNum && childIndex < showMenuBtnNum) {
                        double currentAngle = (Math.PI / 2 / (verticalNum) * (childIndex - verticalNum));
                        angle = currentAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX + cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);

                    }
                }


            }
            int centrePointX = (int) (getChildAt(0).getX() + getChildAt(0).getWidth() / 2 - childView.getWidth() / 2);
            int centrePointY = (int) (getChildAt(0).getY() + getChildAt(0).getHeight() / 2 - childView.getHeight() / 2);
            float viewX = centrePointX - cl;
            float viewY = centrePointY - ct;
            toggleMenuAnim(durationMillis, i, childView, viewX, viewY);

        }
        changeMenuStatus();
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
    private void toggleMenuAnim(int durationMillis, final int i, final View childView, float viewX, float viewY) {
        AnimationSet animset = new AnimationSet(true);
        animset.setInterpolator(new AccelerateDecelerateInterpolator());
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(durationMillis);
        rotateAnimation.setFillAfter(true);
        Animation translateAnimation = null;
        if (mCurrentMenuStatus == Status.OPEN) {
            translateAnimation = new TranslateAnimation(0f, viewX, 0f, viewY);
            childView.setClickable(true);
            childView.setFocusable(true);
        } else if (mCurrentMenuStatus == Status.CLOSE) {
            translateAnimation = new TranslateAnimation(viewX, 0f, viewY, 0f);
            childView.setClickable(false);
            childView.setFocusable(false);
        }
        translateAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (mCurrentMenuStatus == Status.CLOSE)
                    childView.setVisibility(View.GONE);

            }
        });

        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(durationMillis);

        animset.addAnimation(rotateAnimation);
        animset.addAnimation(translateAnimation);
        childView.startAnimation(animset);
        childView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onMenuItemClickListener != null)
                    onMenuItemClickListener.onClickMenu(childView, i, (String) childView.getTag());
                setBackgroundColor(Color.TRANSPARENT);
                menuItemAnin(i);
                changeMenuStatus();
            }
        });
    }

    /**
     * 修改菜单开关的状态
     */
    private void changeMenuStatus() {
        mCurrentMenuStatus = (mCurrentMenuStatus == Status.CLOSE ? Status.OPEN
                : Status.CLOSE);
    }

    /**
     * 开始菜单动画，点击的MenuItem放大消失，其他的缩小消失
     *
     * @param item 点击的第几个按钮
     */
    private void menuItemAnin(int item) {
        for (int i = 0; i < showMenuBtnNum; i++) {
            View childView = getChildAt(i + 1);
            if (i == item) {
                childView.startAnimation(scaleAnim(300, false));
            } else {
                childView.startAnimation(scaleAnim(300, true));
            }
            childView.setClickable(false);
            childView.setFocusable(false);

        }

    }

    /**
     * 缩放动画
     *
     * @param durationMillis 动画执行时间
     * @param isSmall        缩小或者放大 true为缩小，false为放大
     * @return
     */
    private Animation scaleAnim(int durationMillis, boolean isSmall) {
        if (isSmall) {
            Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            anim.setDuration(durationMillis);
            anim.setFillAfter(true);
            return anim;
        } else {
            AnimationSet animationset = new AnimationSet(true);

            Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            Animation alphaAnimation = new AlphaAnimation(1, 0);
            animationset.addAnimation(anim);
            animationset.addAnimation(alphaAnimation);
            animationset.setDuration(durationMillis);
            animationset.setFillAfter(true);
            return animationset;
        }
    }

    /**
     * 获取当前菜单状态
     *
     * @return
     */
    public Status getCurrentStatus() {
        return mCurrentMenuStatus;
    }

    /**
     * 设置当前菜单状态
     *
     * @param mCurrentStatus
     */
    public void setCurrentStatus(Status mCurrentStatus) {
        this.mCurrentMenuStatus = mCurrentStatus;
    }

    /**
     * 删除出了除第一个子按钮的方法
     */
    private void clearOldChildMenuViews() {
        for (int i = getChildCount() - 1; i > 0; i--) {
            removeView(getChildAt(i));
        }
        currentAddViewIndex = 1;
    }

    /**
     * 设置菜单点击监听
     *
     * @param onMenuItemClickListener
     */
    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }


    public interface OnMenuItemClickListener {
        /**
         * @param view      点击的view
         * @param pos       点击的位置
         * @param extraInfo 点击的额外信息
         */
        void onClickMenu(View view, int pos, String extraInfo);
    }

    /**
     * 菜单状态的枚举类
     */
    public enum Status {
        OPEN, CLOSE
    }


}
