package com.example.slidingselectionarea;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by sk080 on 2016/11/25.
 */

public class SlidingView extends RelativeLayout {
    /**
     * menu布局
     */
    private View menu;
    /**
     * viewDragHelper对象
     */
    private ViewDragHelper viewDragHelper;
    /**
     * 控件的宽度
     */
    private int width;
    /**
     * 宽度所拖动的最大范围
     */
    private float dragRange;
    /**
     * 滑动百分比
     */
    private float fraction;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    public SlidingView(Context context) {
        this(context, null);
    }

    public SlidingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /**
         * 初始化
         */
        init();
    }

    /**
     * 当布局加载完成后，就会执行onFinishInFlate方法
     */
    protected void onFinishInflate() {
        super.onFinishInflate();
        /** 获取menu的View */
        menu = getChildAt(1);
    }

    /**
     * 2、通过构造函数，初始化一个ViewDragHelper对象
     */
    private void init() {
        /**
         * 创建ViewDragHelper类，是最新版v4包下
         *
         * 参数一：父类的view对象，此处用this 参数二：敏感度，默认省略为：1.0f 参数三：回调
         */
        viewDragHelper = ViewDragHelper.create(this, callBack);
    }

    /**
     * 此方法在onMeasure方法执行完后执行，用于获取子view的宽高值
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /** menu的宽度 */
        width = menu.getMeasuredWidth();
        /** 宽高所移动的最大距离 */
        dragRange = width;
        /**
         * 获取屏幕的宽度
         */
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        /**
         * 初始化关闭menu
         */
        onClose();
    }

    /**
     * 创建回调接口
     */
    private ViewDragHelper.Callback callBack = new ViewDragHelper.Callback() {
        /**
         * 表示是否捕获当前子view的触摸事件 true：表示捕获 false：不捕获
         */
        public boolean tryCaptureView(View arg0, int arg1) {
            return arg0 == menu;
        }

        /** 水平方向移动的距离 */
        public int getViewHorizontalDragRange(View child) {
            // TODO Auto-generated method stub
            return (int) dragRange;
        }

        /**
         * 控制子控件在水平方向移动
         * 参数一：child：当前的子类控件
         * 参数二：当前子view的left要改变的值
         * 参数三：移动的距离
         * return：子view真正改变成的值
         * */
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //限制menu的移动范围
            if (child == menu) {
                if (left < screenWidth - width) {
                    left = screenWidth - width;
                } else if (left > screenWidth) {
                    left = screenWidth;
                }
            }
            return left;
        }

        /** 当子view位置发生改变的时候执行 */
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            /**
             * 当位置发生改变时，计算滑动的百分比
             * 百分比 = 当前的位置 / 终点位置
             */
            fraction = (screenWidth - menu.getLeft()) / dragRange;
            //如果当前滑动百分比为0，并且不是关闭状态，那么就是执行了关闭操作
            if (fraction == 0 && state != DragState.Close) {
                if (onDragState != null) {
                    //进行判断，如果onDragState不为空，证明进行了回调
                    state = DragState.Close;
                    onDragState.onClose();
                }
            } else if (fraction == 1 && state != DragState.Open) {
                if (onDragState != null) {
                    //进行回调的打开操作
                    state = DragState.Open;
                    onDragState.onOpen();
                }
            }
        }

        /** 当手指抬起时执行此方法 */
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            /**
             * 当手指抬起时，判断menu当前的位置
             */
            if (screenWidth - menu.getLeft() > dragRange / 3) {
                //打开
                onOpen();
            } else {
                //关闭
                onClose();
            }

            /**根据用户的力度，确定打开或关闭菜单，提高用户体验*/
            if (xvel < 200) {
                onOpen();
            } else {
                onClose();
            }
        }
    };

    /**
     * 关闭菜单
     */
    public void onClose() {
        // 滑动到屏幕的最右边，异常
        viewDragHelper.smoothSlideViewTo(menu, screenWidth, menu.getTop());
        // 进行平行滑动
        ViewCompat.postInvalidateOnAnimation(SlidingView.this);
        state = DragState.Close;
    }

    /**
     * 打开菜单
     */
    public void onOpen() {
        /**
         * 滑动到屏幕中，显示
         * 参数一：要移动的view对象 参数二：左边移动的最终位置 参数三：上边移动的最终位置
         * */
        viewDragHelper.smoothSlideViewTo(menu, screenWidth - width,
                menu.getTop());
        // 进行平行滑动
        ViewCompat.postInvalidateOnAnimation(SlidingView.this);
        state = DragState.Open;
    }


    /**
     * 事件分发：将事件分发给ViewDragHelper，让其ViewDragHelper决定是否消费事件
     */
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 事件拦截，将接受到的事件消费掉
     */
    public boolean onTouchEvent(android.view.MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            // 如果平行滑动没有到达指定位置，那么继续回调执行
            ViewCompat.postInvalidateOnAnimation(SlidingView.this);
        }
    }

    // 7、创建接口回调
    public interface OnDragStateChangedListener {
        /**
         * 打开接口的回调
         */
        void onOpen();

        /**
         * 关闭接口的回调
         */
        void onClose();

        /**
         * 正在拖拽的回调
         */
        void onDraging(float fraction);
    }

    //8、创建接口回调监听
    private OnDragStateChangedListener onDragState;

    public void setOnDragStateChangedListener(OnDragStateChangedListener onDragState) {
        this.onDragState = onDragState;
    }


    /**
     * 创建枚举，表示状态
     */
    enum DragState {
        Open, Close
    }

    /**
     * 声明一个状态值，默认关闭状态
     */
    private DragState state = DragState.Close;

    /**
     * 提供一方法，用于得到当前的开关状态值
     *
     * @return 返回当前开关的状态
     */
    public DragState getState() {
        return state;
    }
}
