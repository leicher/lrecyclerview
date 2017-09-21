package leicher.lrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;



public abstract class FooterWrapper extends WrapperParent {

    private static final String TAG = "FooterView";

    private View footer;


    public FooterWrapper(Context context) {
        this(context,null);
    }

    public FooterWrapper(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FooterWrapper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        final View footer = initFooter(context);
        if (footer != null && footer != this) {
            setFooter(footer);
        }
    }


    public final View getFooter() {
        return footer;
    }


    public final void setFooter(View footer) {
        if (footer == null){
            throw new NullPointerException("this footerView can not is null");
        }
        if (this.footer != footer) {
            this.footer = footer;
            if (getChildCount() > 0) {
                removeAllViewsInLayout();
            }
            if (footer.getParent() != null) {
                ViewGroup parent = (ViewGroup) footer.getParent();
                parent.removeView(footer);
            }
            ViewGroup.LayoutParams params = footer.getLayoutParams();
            if (params == null) {
                params = generateDefaultLayoutParams();
                if (params == null) {
                    throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
                }
            }
            addViewInLayout(footer,0,params);
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    idleHeight());
            setGravity(gravity());
            setLayoutParams(params);
        }else requestLayout();
    }


    final void sliding(LRecyclerView recycler,int off){
        int height = getVisibleHeight();

        height += -off;

        setVisibleHeight(height);
        if (height >= releaseHeight()){
            setCurrentState(RELEASE_TO_REFRESH);
        }else {
            setCurrentState(SLIDING);
        }
        onSliding(recycler, footer, height, off);
    }

    final void load(LRecyclerView recycler){
        setCurrentState(REFRESHING);
        List<LRecyclerView.RefreshListener> listeners = recycler.getRefreshListeners();
        if (listeners != null)
            for (LRecyclerView.RefreshListener l:listeners)
                if (l != null) l.onLoadMore();
        onRelease(recycler,footer);
    }

    final void release(LRecyclerView recycler){
        if (refresh()){
            smoothScrollTo(loadHeight());
            setCurrentState(REFRESHING);
            List<LRecyclerView.RefreshListener> listeners = recycler.getRefreshListeners();
            if (listeners != null)
                for (LRecyclerView.RefreshListener l:listeners)
                    if (l != null) l.onLoadMore();
        }else {
            smoothScrollTo(idleHeight());
            setCurrentState(IDLE);
        }
        onRelease(recycler,footer);
    }

    final void complete(LRecyclerView recycler){

        postDelayed(new Runnable() {
                @Override
                public void run() {
                    smoothScrollTo(idleHeight());
                }
            },idleDelayed());
        setCurrentState(IDLE);
        onComplete(recycler , footer);
    }

    final void fail(LRecyclerView recycler){
        postDelayed(new Runnable() {
                @Override
                public void run() {
                    smoothScrollTo(failHeight());
                }
            },idleDelayed());
        setCurrentState(FAIL);
        onFail(recycler,footer);
    }
    /*
        Override these method
     */

    protected int gravity(){
        return Gravity.CENTER;
    }

    protected abstract View initFooter(Context context);

    protected abstract void onSliding(LRecyclerView recycler , View header , int height , int off);

    protected abstract void onRelease(LRecyclerView recycler, View header);

    protected abstract void onComplete(LRecyclerView recycler,View header);

    protected abstract void onFail(LRecyclerView recycler,View header);



    /**
     * @return
     */
    protected abstract int releaseHeight();

    /**
     *
     * @return
     */
    protected int loadHeight(){
        return releaseHeight();
    }


}
