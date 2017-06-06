package com.yhongm.arcmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
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
    private int cuurentAddViewIndex = 1;
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
    private Status mCurrentStatus = Status.CLOSE;
    /**
     * 回调接口
     */
    private OnMenuItemClickListener onMenuItemClickListener;


    /**
     * 第一子view为中间按钮
     */
    View cButton;//
    Context mContext;
    private int showMenuBtnNum = 5;//第一排显示的扇形菜单子数量
    private ArrayList<View> mChildMenuViews=new ArrayList<>();
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
     * do转为px
     *
     * @param dp
     * @return
     */
    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    //添加主按钮
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
        layoutChildMenu();


        Log.i("ArcMenu","setShowMenuBtnNum,num:"+showMenuBtnNum);// yuhongmiao 2017/6/6 下午7:49

//        requestLayout();

    }

    public void layoutChildMenu() {
        clearOldChildMenuViews();
        for (View childMenuView :
                mChildMenuViews) {
            addViewInLayout(childMenuView, cuurentAddViewIndex, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            cuurentAddViewIndex++;
        }
        Log.i("ArcMenu","layoutChildMenu,count:"+getChildCount());// yuhongmiao 2017/6/6 下午9:23
        invalidate();
        requestLayout();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i("ArcMenu","onLayout,");// yuhongmiao 2017/6/6 下午7:49

        if (changed) {
            Log.i("ArcMenu","onLayout,changle");// yuhongmiao 2017/6/6 下午8:20

            layoutButton();
            toggleMenu(380);

        }

    }

    /**
     * 添加菜单按钮
     * @param id
     * @param title
     * @param extraInfo
     */
    public void addChildArcMenu(int id, String title,String extraInfo) {
        View childMenuView = getChildMenuView(id, title,extraInfo);

        mChildMenuViews.add(childMenuView);

    }

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
        rotateView(mButton, 0f, 270f, 300);
        toggleMenu(380);
    }

    /**
     * 按钮的旋转动画
     *
     * @param view
     * @param fromDegrees
     * @param toDegrees
     * @param durationMillis
     */
    public static void rotateView(View view, float fromDegrees,
                                  float toDegrees, int durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    /**
     * 按钮布局与动画
     * @param durationMillis
     */
    private void toggleMenu(int durationMillis) {
        setBackgroundColor(Color.parseColor("#66111111"));
        int count = getChildCount();
        for (int i = 0; i < showMenuBtnNum; i++) {
            Log.i("ArcMenu","toggleMenu,i:"+i);// yuhongmiao 2017/6/6 下午10:56

            View childView = getChildAt(i+1);
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
                float verticalNum =showMenuBtnNum/2;
                if (showMenuBtnNum % 2 == 0) {
                    verticalNum=verticalNum-0.5f;
                    if (childIndex<verticalNum){
                        double currntAngle = (Math.PI / 2 / (verticalNum) * (verticalNum-childIndex));
                        angle = currntAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX - cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);
                        Log.i("ArcMenu","toggleMenu,2<");// yuhongmiao 2017/6/6 下午8:44

                    }else if (childIndex>verticalNum) {
                        double currentAngle = (Math.PI / 2 / (verticalNum) * (childIndex - verticalNum));
                        angle = currentAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX + cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);
                        Log.i("ArcMenu","toggleMenu,2>");// yuhongmiao 2017/6/6 下午8:42

                    }
                } else {
                    if (childIndex <verticalNum) {
                        double currntAngle = (Math.PI / 2 / (verticalNum) * (verticalNum-childIndex));
                        angle = currntAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX - cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);
                        Log.i("ArcMenu","toggleMenu,<");// yuhongmiao 2017/6/6 下午8:44

                    } else if (childIndex == verticalNum) {
                        double currentAngle=0;
                        if (verticalNum==0){
                            currentAngle = 0;
                        }else {
                            currentAngle = ((Math.PI / 2 / (verticalNum)) * (verticalNum-childIndex));
                        }
                        angle = currentAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX - cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);
                        Log.i("ArcMenu","toggleMenu,=");// yuhongmiao 2017/6/6 下午8:37

                    } else if (childIndex > verticalNum && childIndex <showMenuBtnNum) {
                        double currentAngle = (Math.PI / 2 / (verticalNum) * (childIndex - verticalNum));
                        angle = currentAngle;
                        cl = (int) (mRadius * Math.sin(angle));
                        ct = (int) (mRadius * Math.cos(angle));
                        cl = centerX + cl;
                        ct = centerY - ct;
                        childView.layout(cl, ct, cl + cWidth, ct + cHeight);
                        Log.i("ArcMenu","toggleMenu,>");// yuhongmiao 2017/6/6 下午8:37

                    }
                }


            }
           int centrePointX=(int) (getChildAt(0).getX()+getChildAt(0).getWidth()/2-childView.getWidth()/2);
           int centrePointY= (int) (getChildAt(0).getY()+getChildAt(0).getHeight()/2-childView.getHeight()/2);
            float viewX = centrePointX -cl;
            float viewY = centrePointY -ct;
            toggleMenuAnim(durationMillis, count, i, childView, viewX, viewY);

        }
        changeStatus();
    }

    /**
     * 切换菜单动画
     *
     * @param durationMillis
     * @param viewIndex
     * @param i
     * @param childView
     * @param viewX
     * @param viewY
     */
    private void toggleMenuAnim(int durationMillis, int viewIndex, int i, final View childView, float viewX, float viewY) {
        AnimationSet animset = new AnimationSet(true);
        Animation animation = null;
        if (mCurrentStatus == Status.OPEN) {
            animset.setInterpolator(new AccelerateDecelerateInterpolator());
            animation = new TranslateAnimation(0f, viewX, 0f, viewY);
            childView.setClickable(true);
            childView.setFocusable(true);
        } else {
            animation = new TranslateAnimation(viewX, 0f, viewY, 0f);
            childView.setClickable(false);
            childView.setFocusable(false);
        }
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (mCurrentStatus == Status.CLOSE)
                    childView.setVisibility(View.GONE);

            }
        });

        animation.setFillAfter(true);
        animation.setDuration(durationMillis);
        RotateAnimation rotate = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        animset.addAnimation(rotate);
        animset.addAnimation(animation);
        childView.startAnimation(animset);
        final int index = i + 1;
        childView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onMenuItemClickListener != null)
                    onMenuItemClickListener.onClickMenu(childView, index - 1, (String) childView.getTag());
                setBackgroundColor(Color.TRANSPARENT);
                menuItemAnin(index - 1);
                changeStatus();

            }
        });
    }

    /**
     * 修改菜单开关的状态
     */
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
                : Status.CLOSE);
    }

    /**
     * 开始菜单动画，点击的MenuItem放大消失，其他的缩小消失
     *
     * @param item
     */
    private void menuItemAnin(int item) {
        for (int i = 0; i < showMenuBtnNum; i++) {
            View childView = getChildAt(i + 1);
            if (i == item) {
                childView.startAnimation(scaleAnim(300,false));
            } else {
                childView.startAnimation(scaleAnim(300,true));
            }
            childView.setClickable(false);
            childView.setFocusable(false);

        }

    }

    /**
     * 缩小消失
     *
     * @param durationMillis 动画执行时间
     * @param isSmall 缩小或者放大 true为缩小，false为放大
     * @return
     */
    private Animation scaleAnim(int durationMillis,boolean isSmall) {
        if (isSmall){
        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);
        return anim;
        }else {
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

    public Status getCurrentStatus() {
        return mCurrentStatus;
    }

    public void setCurrentStatus(Status mCurrentStatus) {
        this.mCurrentStatus = mCurrentStatus;
    }

    /**
     * 删除出了除第一个子按钮的方法
     */
    private void clearOldChildMenuViews() {
        for (int i = getChildCount() - 1; i > 0; i--) {
            removeView(getChildAt(i));
        }
        cuurentAddViewIndex = 1;
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }


    public interface OnMenuItemClickListener {
        void onClickMenu(View view, int pos,String extraInfo);
    }

    /**
     * 状态的枚举类
     */
    public enum Status {
        OPEN, CLOSE
    }


}
