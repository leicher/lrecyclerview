package leicher.lrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@SuppressWarnings("all")
public abstract class EmptyWrapper extends LinearLayout {

    public static final int STATE_IDLE = 1 << 1;

    public static final int STATE_LOADING = 1 << 2;

    public static final int STATE_FAIL = 1 << 3;

    private int state = STATE_IDLE;

    private View empty;

    public EmptyWrapper(Context context) {
        this(context,null);
    }

    public EmptyWrapper(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EmptyWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setOrientation(VERTICAL);
        final View empty = initEmpty(context);
        if (empty != null && empty != this) {
            setEmpty(empty);
        }
    }

    public void setEmpty(View empty){
        if (empty == null){
            throw new NullPointerException("empty can not be null");
        }
        if (empty != this.empty){
            this.empty = empty;
            if (getChildCount() > 0) {
                removeAllViewsInLayout();
            }
            if (empty.getParent() != null) {
                ViewGroup parent = (ViewGroup) empty.getParent();
                parent.removeView(empty);
            }
            ViewGroup.LayoutParams params = empty.getLayoutParams();
            if (params == null) {
                params = generateDefaultLayoutParams();
                if (params == null) {
                    throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
                }
            }
            addViewInLayout(empty,0,params);
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            setLayoutParams(params);
        }else requestLayout();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    protected void stateChanged(int state){
        onStateChanged(empty, this.state, state);
        this.state = state;
    }

    public int getState(){
        return this.state;
    }

    protected abstract View initEmpty(Context context);

    protected abstract void onStateChanged(View empty,int oldState,int newState);

}
