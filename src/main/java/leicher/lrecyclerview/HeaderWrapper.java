package leicher.lrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;


public abstract class HeaderWrapper extends WrapperParent {


    /*
        real header
     */
    private View header;

    public HeaderWrapper(Context context) {
        this(context,null);
    }

    public HeaderWrapper(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeaderWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        final View header =  initHeader(context);
        if (header != null && header != this) {
            setHeader(header);
        }
    }

    public final void setHeader(View header) {
        if (header == null){
            throw new NullPointerException("this header can not be null");
        }
        if (this.header != header){
            this.header = header;
            if (getChildCount() > 0){
                removeAllViewsInLayout();
            }
            if (header.getParent() != null){
                ViewGroup parent = (ViewGroup) header.getParent();
                parent.removeView(header);
            }

            ViewGroup.LayoutParams params = header.getLayoutParams();
            if (params == null) {
                params = generateDefaultLayoutParams();
                if (params == null) {
                    throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
                }
            }
            addViewInLayout(header,0,params);
            setGravity(gravity());
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    idleHeight());
            setLayoutParams(params);
        }else requestLayout();
    }


    public final View getHeaderView(){
        return this.header;
    }


    public final void refresh(LRecyclerView recycler){
        smoothScrollTo(refreshHeight());
        setCurrentState(REFRESHING);
        onRelease(recycler,header);
    }

    final void sliding(LRecyclerView recycler,final int off){
        int height = getVisibleHeight();

        height += off;

        setVisibleHeight(height);

        if (height >= releaseHeight()){
            setCurrentState(RELEASE_TO_REFRESH);
        }else {
            setCurrentState(SLIDING);
        }

        onSliding(recycler, header, height, off);

    }

    final void release(LRecyclerView recycler){
        if (refresh()){
            smoothScrollTo(refreshHeight());
            setCurrentState(REFRESHING);
            List<LRecyclerView.RefreshListener> listeners = recycler.getRefreshListeners();
            if (listeners != null)
                for (LRecyclerView.RefreshListener l:listeners)
                    if (l != null) l.onRefresh();
        }else {
            smoothScrollTo(idleHeight());
            setCurrentState(IDLE);
        }
        onRelease(recycler,header);
    }

    final void complete(LRecyclerView recycler){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(idleHeight());
            }
        },idleDelayed());
        setCurrentState(IDLE);
        onComplete(recycler , header);
    }

    final void fail(LRecyclerView recycler){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(failHeight());
            }
        },idleDelayed());
        setCurrentState(FAIL);
        onFail(recycler,header);
    }

    /*
        Override these method
     */

    protected int gravity(){
        return Gravity.CENTER|Gravity.BOTTOM;
    }

    protected int refreshHeight(){
        return releaseHeight();
    }


    /*
        init header
     */
    protected abstract View initHeader(Context context);

    protected abstract void onSliding(LRecyclerView recycler , View header , int height , int off);

    protected abstract void onRelease(LRecyclerView recycler, View header);

    protected abstract void onComplete(LRecyclerView recycler,View header);

    protected abstract void onFail(LRecyclerView recycler,View header);

    protected abstract int releaseHeight();


}
