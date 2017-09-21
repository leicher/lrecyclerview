package leicher.lrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

class WrapperParent extends LinearLayout implements LState{

    protected static int TOUCH_SLOP = -1;

    private ValueAnimator animator;

    /*
        state
     */
    private int currentState = IDLE;

    private int previousState = IDLE;

    public WrapperParent(Context context) {
        this(context,null);
    }

    public WrapperParent(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WrapperParent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        if (TOUCH_SLOP != -1){
            TOUCH_SLOP = ViewConfiguration.get(context).getScaledTouchSlop();
        }
    }

    @Override
    public final void setCurrentState(int state) {
        this.previousState = this.currentState;
        this.currentState = state;
    }

    public final int getPreviousState() {
        return previousState;
    }

    @Override
    public final int getCurrentState() {
        return currentState;
    }

    /*
        current view height
     */
    protected final int getVisibleHeight() {
        ViewGroup.LayoutParams lp = getLayoutParams();
        return lp.height;
    }

    protected final void setVisibleHeight(int height) {
        if (height < 0) return;
        ViewGroup.LayoutParams lp =  getLayoutParams();
        lp.height = height;
        setLayoutParams(lp);
    }

    final boolean refreshing(){
        return getCurrentState() == REFRESHING;
    }

    final boolean refresh(){
        return getCurrentState() == RELEASE_TO_REFRESH;
    }

    final boolean fingerRelease(){
        return refresh() || getCurrentState() == SLIDING;
    }

    protected final void smoothScrollTo(int targetHeight) {
        if (animator != null && (animator.isStarted() || animator.isRunning())){
            animator.cancel();
        }
        int height = getVisibleHeight();
        if (height == targetHeight) return;
        animator = new ValueAnimator();
        animator.setIntValues(height,targetHeight);
        animator.setTarget(targetHeight);
        animator.setDuration(duration());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int target = animation.getValue();
                if (target != animation.getTarget()) {
                    final int height = getVisibleHeight();
                    final int diff = height > target ? height - target : target - height;

                    if (diff >= TOUCH_SLOP ) {
                        setVisibleHeight(target);
                    }
                }else {
                    setVisibleHeight(target);
                }
            }
        });
        animator.start();
    }

    protected long duration(){return 300;}

    protected long idleDelayed(){
        return 0;
    }

    /*
        Default height
     */
    protected int idleHeight(){
        return 0;
    }

    protected int failHeight(){
        return idleHeight();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null && (animator.isStarted() || animator.isRunning())){
            animator.cancel();
        }
    }
}
