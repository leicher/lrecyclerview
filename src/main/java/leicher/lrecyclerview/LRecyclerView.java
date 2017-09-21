package leicher.lrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class LRecyclerView extends RecyclerView {

    private static final String TAG = "LRecyclerView";

    private static final int TYPE_HEADER = Integer.MAX_VALUE >> 1;

    private static final int TYPE_FOOTER = TYPE_HEADER + 1;

    private static final int TYPE_EMPTY = TYPE_FOOTER + 1;

    /*
     * rate
     */
    private static float REFRESH_DRAG_RATE = 2f;

    public static void setRefreshDragRate(float refreshDragRate) {
        REFRESH_DRAG_RATE = refreshDragRate;
    }

    private static float DRAG_RATE = 1f;

    public static void setDragRate(float dragRate) {
        DRAG_RATE = dragRate;
    }

    public static final int LOAD_MODE_PULL = 0xFFE;

    public static final int LOAD_MODE_DEFAULT = 0xFFF;


    private boolean refreshEnable = false;
    private boolean loadEnable = false;
    private final Adapter mAdapter;
    private DataObserver dataObserver ;
    private int mode = LOAD_MODE_DEFAULT;
    private List<RefreshListener> mListeners;
    private int TOUCH_SLOP ;
    private boolean horizontal = false;
    private int computeBottom = 1;
    private boolean allInvalid = false;

    public LRecyclerView(Context context) {
        this(context,(AttributeSet) null);
    }

    public LRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAdapter = new Adapter();
        init(context);
    }

    private void init(Context context){
        dataObserver = new DataObserver(mAdapter);
        mAdapter.emptyState = EmptyWrapper.STATE_IDLE;
        super.setAdapter(mAdapter);
        TOUCH_SLOP = 4;
    }

    protected void setTouchSlop(int touchSlop){
        this.TOUCH_SLOP = touchSlop;
    }

    public int getMode() {
        return mode;
    }

    public LRecyclerView setMode(int mode) {
        this.mode = mode;
        if (mode != LOAD_MODE_PULL)
          this.mode = LOAD_MODE_DEFAULT;
        return this;
    }

    public int getComputeBottom() {
        return computeBottom;
    }

    public LRecyclerView setComputeBottom(int computeBottom) {
        this.computeBottom = computeBottom;
        return this;
    }

    public LRecyclerView addRefreshListener(RefreshListener listener){
        if (mListeners == null){
            mListeners = new ArrayList<>();
        }
        if (listener != null)
            mListeners.add(listener);
        return this;
    }

    public List<RefreshListener> getRefreshListeners(){
        return mListeners;
    }

    public LRecyclerView removeRefreshListener(RefreshListener listener){
        if (mListeners != null && listener != null){
            mListeners.remove(listener);
        }
        return this;
    }

    public LRecyclerView removeAllRefreshListeners(RefreshListener listener){
        if (mListeners != null){
            mListeners.clear();
        }
        return this;
    }

    public HeaderWrapper getHeader() {
        return mAdapter.header ;
    }

    public LRecyclerView setHeader(HeaderWrapper header) {
        mAdapter.header = header;
        return this;
    }

    public FooterWrapper getFooter() {
        return mAdapter.footer ;
    }

    public LRecyclerView setFooter(FooterWrapper footerView) {
        mAdapter.footer = footerView;
        return this;
    }

    public LRecyclerView setEmpty(EmptyWrapper empty){
        mAdapter.empty = empty;
        return this;
    }

    public LRecyclerView setEmptyState(int state){
        if (state != mAdapter.emptyState) {
            mAdapter.emptyState = state;
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    public EmptyWrapper getEmpty(){
        return mAdapter.empty;
    }

    public boolean isRefreshEnable() {
        return refreshEnable;
    }

    public boolean isLoadEnable() {
        return loadEnable;
    }

    public LRecyclerView setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
        return this;
    }

    public LRecyclerView setLoadEnable(boolean loadEnable) {
        this.loadEnable = loadEnable;
        return this;
    }

    protected float lastY = -1;

    protected float lastX = -1;

    protected int count = 0;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isAllInvalid()) return super.onTouchEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (horizontal){
                    lastX = e.getRawX();
                } else lastY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (horizontal)
                    return doMoveHorizontal(e);
                else
                    return doMoveVertical(e);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                count = 0;
                if (horizontal)
                   return doUpHorizontal(e);
                else return doUpVertical(e);
        }
        return super.onTouchEvent(e);
    }


    @Override
    public void onScrolled(int dx, int dy) {
        if (isLoadEnable() && mAdapter.footer() &&
                !mAdapter.footer.refreshing() &&
                getMode() == LOAD_MODE_DEFAULT && isOnBottom(dy > 0)){
            mAdapter.footer.load(this);
        }
    }

    protected boolean doMoveHorizontal(MotionEvent e){
        /*
            do something
         */
        return super.onTouchEvent(e);
    }

    protected boolean doMoveVertical(MotionEvent e){
        final float rawY = e.getRawY();
        if ( ++count > getIgnoreCount()) {
            float moveY = rawY - lastY;
            if (isRefreshEnable() && mAdapter.header()
                    && !mAdapter.header.refreshing() && isOnTop()) {
                moveY = moveY / REFRESH_DRAG_RATE;
                if (moveY >= TOUCH_SLOP || moveY <= -TOUCH_SLOP) {
                    mAdapter.header.sliding(this, (int) moveY);
                    lastY = rawY;
                }
                return false;
            } else if (isLoadEnable() && mAdapter.footer() &&
                    !mAdapter.footer.refreshing()  && isOnBottom(moveY < 0)) {
                moveY = moveY / DRAG_RATE;
                if (getMode() == LOAD_MODE_PULL) {
                    if (moveY >= TOUCH_SLOP || moveY <= -TOUCH_SLOP) {
                        mAdapter.footer.sliding(this, (int) moveY);
                        lastY = rawY;
                    }
                }else {
                    mAdapter.footer.load(this);
                }
                return false;
            }
            lastY = rawY;
        }else lastY = rawY;
        return super.onTouchEvent(e);
    }

    protected boolean doUpVertical(MotionEvent e){
        if (isRefreshEnable() && mAdapter.header() && mAdapter.header.fingerRelease()){
            mAdapter.header.release(this);
        }else if (isLoadEnable() && mAdapter.footer() && mAdapter.footer.fingerRelease()){
            if (getMode() == LOAD_MODE_PULL && isWrap()){
                mAdapter.footer.release(this);
            }else {
                mAdapter.footer.load(this);
            }

        }
        return super.onTouchEvent(e);
    }

    protected boolean doUpHorizontal(MotionEvent e){
        return super.onTouchEvent(e);
    }


    protected int getIgnoreCount(){
        return 2;
    }

    public void onLoadComplete(){
        if (mAdapter.footer()){
            mAdapter.footer.complete(this);
        }
    }

    public void onLoadFail(){
        if (mAdapter.footer()){
            mAdapter.footer.fail(this);
        }
    }

    public void onRefreshComplete(){
        if (mAdapter.header()){
            mAdapter.header.complete(this);
        }
    }

    public void onRefreshFail(){
        if (mAdapter.header()){
            mAdapter.header.fail(this);
        }
    }

    private boolean isOnTop(){
        return computeVerticalScrollOffset() == 0;
    }

    private boolean isHorizontal(){
        return (getLayoutManager() instanceof LinearLayoutManager &&
                ((LinearLayoutManager) getLayoutManager()).getOrientation()
                        == LinearLayoutManager.HORIZONTAL) || (getLayoutManager() instanceof StaggeredGridLayoutManager &&
                ((StaggeredGridLayoutManager) getLayoutManager()) .getOrientation() == StaggeredGridLayoutManager.HORIZONTAL);
    }

    @Override
    public int computeVerticalScrollOffset() {
        LayoutManager manager = getLayoutManager();
        if (manager instanceof LinearLayoutManager){
            LinearLayoutManager ma = (LinearLayoutManager) manager;
            int position = ma.findFirstCompletelyVisibleItemPosition();
            return position == 0 ? 0 : super.computeVerticalScrollOffset();
        }else if (manager instanceof StaggeredGridLayoutManager){
            StaggeredGridLayoutManager ma = (StaggeredGridLayoutManager) manager;
            int[] into = null;
            into = ma.findFirstCompletelyVisibleItemPositions(into);
            for (int i = 0 ; into != null && i < into.length ; i++) {
                if (into[i] == 0 || into[i] == 1) {
                    return 0;
                }
            }
        }
        return super.computeVerticalScrollOffset();
    }

    private boolean isOnBottom(boolean isUpward){
        int count = 1;

        final boolean computeWrap = !(getMode() == LOAD_MODE_DEFAULT);

        if (!computeWrap && !isUpward){
            return false;
        }

        if (!computeWrap) {
            count = computeBottom;
        }

        if (count < 1){
            count = 1;
        }
        LayoutManager manager = getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager ma = (LinearLayoutManager) manager;
            int position = ma.findLastCompletelyVisibleItemPosition();
            boolean b = computePositonIsOnBottom(ma,count,position);
            return computeWrap ? b && isWrap() : b;
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager ma = (StaggeredGridLayoutManager) manager;
            int[] into = null;
            into = ma.findLastCompletelyVisibleItemPositions(into);
            for (int i = 0 ; into != null && i < into.length ; i++)
                if (into[i] == ma.getItemCount() - count)
                    return !computeWrap || isWrap() ;
        }
        return false;
    }

    protected boolean computePositonIsOnBottom(LinearLayoutManager ma,final int count ,final int position){
        int last = -1;
        if (getMode() == LOAD_MODE_DEFAULT) {
            if (ma instanceof GridLayoutManager) {
                GridLayoutManager ga = (GridLayoutManager) ma;
                last = ga.findLastVisibleItemPosition();
            }
        }
        int limit = mAdapter.getItemCount() - count;
        return last <= position ? limit == position : limit >= position && limit <= last;
    }

    private boolean isWrap(){
        int extent = computeVerticalScrollExtent();
        return extent >= getHeight() || extent >= getMeasuredHeight();
    }


    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null){
            throw new NullPointerException("this adapter can not be null");
        }
        initAdapter(adapter);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return mAdapter.getAdapter();
    }


    private void initAdapter(RecyclerView.Adapter adapter){
        if (adapter != mAdapter.getAdapter()) {
            RecyclerView.Adapter<RecyclerView.ViewHolder> old = mAdapter.getAdapter();
            if (old != null){
                old.unregisterAdapterDataObserver(dataObserver);
            }
            mAdapter.setAdapter(adapter);
            adapter.registerAdapterDataObserver(dataObserver);
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        layout = initLayoutManager(layout);
        super.setLayoutManager(layout);
    }


    public LayoutManager initLayoutManager(LayoutManager layout){
        setAllInvalid(false);
        if (layout != null && layout instanceof GridLayoutManager) {
            final GridLayoutManager manager = ((GridLayoutManager) layout);
            final GridLayoutManager.SpanSizeLookup span = manager.getSpanSizeLookup();
            manager.setSpanSizeLookup(new SpanSizeLookupWrapper(span,mAdapter,manager));
        }else if (layout instanceof StaggeredGridLayoutManager){
            setAllInvalid(true);
        }
        this.horizontal = isHorizontal();
        return layout;
    }

    protected boolean isAllInvalid(){
        return allInvalid;
    }

    protected void setAllInvalid(boolean allInvalid){
        this.allInvalid = allInvalid;
    }

    public void refresh(){
        if (mAdapter.header != null){
            mAdapter.header.refresh(this);
        }
    }

    public interface RefreshListener{
        /*
         * onRefresh
         */
        void onRefresh();

        /*
         * onLoadMore
         */
        void onLoadMore();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{
        private ViewHolder(View itemView) {
            super(itemView);
        }

    }

    private static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        HeaderWrapper header;

        FooterWrapper footer;

        EmptyWrapper empty;

        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

        boolean isEmpty = false;

        int emptyState;

        Adapter() {}

        void setAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter){
            if (adapter == null){
                throw new NullPointerException("this adapter can not be null");
            }
            this.adapter = adapter;
        }

        boolean header(){
            return header != null;
        }

        boolean footer(){
            return footer != null;
        }

        boolean isFooter(int position){
            return footer() && position == getItemCount() - 1;
        }

        boolean isHeader(int position){
            return header() && position == 0;
        }

        boolean empty(){
            return empty != null;
        }

        private boolean footerOrHeader(int position){
            return isFooter(position) || isHeader(position);
        }


        RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
            return adapter;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case TYPE_HEADER:
                    return new ViewHolder(header);
                case TYPE_FOOTER:
                    return new ViewHolder(footer);
                case TYPE_EMPTY:
                    return new ViewHolder(empty);
            }
            return adapter.createViewHolder(parent,viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (isEmpty){
                empty.stateChanged(emptyState);
                return;
            }
            if (isHeader(position) || isFooter(position)){
                return;
            }
            int adjPosition = position;
            if (header() && position > 0){
                adjPosition --;
            }
            if (adjPosition < adapter.getItemCount()) {
                adapter.onBindViewHolder(holder,adjPosition);
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                int count = adapter.getItemCount();

                if (count == 0) {
                    isEmpty = true;
                    return empty() ? 1 : 0;
                }
                isEmpty = false;
                if (header()) {
                    count += 1;
                }
                if (footer()) {
                    count += 1;
                }
                return count;
            }
            isEmpty = true;
            return 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            onBindViewHolder(holder, position);
        }

        @Override
        public int getItemViewType(int position) {
            if (isEmpty){
                return TYPE_EMPTY;
            }
            if (isHeader(position)){
                return TYPE_HEADER;
            }
            if (isFooter(position)){
                return TYPE_FOOTER;
            }
            int adjPosition = position;
            if (header() && position > 0){
                adjPosition --;
            }
            if (adjPosition < adapter.getItemCount()) {
                return adapter.getItemViewType(adjPosition);
            }else {
                return super.getItemViewType(position);
            }
        }

        @Override
        public void setHasStableIds(boolean hasStableIds) {
            adapter.setHasStableIds(hasStableIds);
        }

        @Override
        public long getItemId(int position) {
            if (header() && position > 0){
                int adjPosition = position - 1;
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (adapter != null)
                adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return adapter != null && adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (adapter != null)
                adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (adapter != null)
                adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            if (adapter != null)
                adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            if (adapter != null)
                adapter.onDetachedFromRecyclerView(recyclerView);
        }

    }

    private static class DataObserver extends AdapterDataObserver {

        private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

        private DataObserver(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
            if (adapter == null){
                throw new NullPointerException("this adapter can not be null");
            }
            this.adapter = adapter;
        }

        @Override
        public void onChanged() {
            adapter.notifyDataSetChanged();
        }


        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            adapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            adapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            adapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    private static class SpanSizeLookupWrapper extends GridLayoutManager.SpanSizeLookup{

        private final GridLayoutManager.SpanSizeLookup realSpan;

        private final Adapter mAdapter;

        private final GridLayoutManager manager;

        public SpanSizeLookupWrapper(GridLayoutManager.SpanSizeLookup realSpan,
                                     Adapter mAdapter,GridLayoutManager manager) {
            this.realSpan = realSpan;
            this.mAdapter = mAdapter;
            this.manager = manager;
        }

        @Override
        public int getSpanSize(int position) {
            return mAdapter.isEmpty && mAdapter.empty() ? manager.getSpanCount() : (mAdapter.footerOrHeader(position) ? manager.getSpanCount():
                    realSpan != null ? realSpan.getSpanSize(mAdapter.header() && position > 0
                            ? -- position : position) : 1);
        }
    }
}
