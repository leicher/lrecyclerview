package leicher.lrecyclerview.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("unused")
public class LHolder extends RecyclerView.ViewHolder {

    SparseArray<View> children;

    Object tag;

    public LHolder(View itemView) {
        super(itemView);
        children = new SparseArray<>();
    }

    @SuppressWarnings("unchecked")
    public final <T extends View> T getView(@IdRes int resId){
        View view = children.get(resId);
        if (view == null){
            view = itemView.findViewById(resId);
            if (view != null){
                children.put(resId,view);
            }
        }
        return (T) view;
    }

    public Object getTag() {
        return tag;
    }

    public LHolder setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public LHolder setViewTag(@IdRes int resId, Object tag){
        final View view = getView(resId);
        if (view != null){
            view.setTag(tag);
        }
        return this;
    }

    public LHolder setItemViewTag(Object tag){
        itemView.setTag(tag);
        return this;
    }

    public LHolder setText(@IdRes int resId, CharSequence cs){
        final View view = getView(resId);
        if (view != null && view instanceof TextView){
            ((TextView) view).setText(cs);
        }
        return this;
    }

    public LHolder setImage(@IdRes int resId, @DrawableRes int drawableId){
        final View view = getView(resId);
        if (view != null && view instanceof ImageView){
            ((ImageView) view).setImageResource(drawableId);
        }
        return this;
    }


    public LHolder setOnItemClickListener(View.OnClickListener listener){
        itemView.setOnClickListener(listener);
        return this;
    }

    public LHolder cleanOnItemClickListener(){
        itemView.setOnClickListener(null);
        return this;
    }


    public LHolder setOnViewClickListener(@IdRes int resId, View.OnClickListener listener){
        final View view = getView(resId);
        if (view != null){
            view.setOnClickListener(listener);
        }
        return this;
    }

    public LHolder cleanOnViewClickListener(@IdRes int resId){
        final View view = getView(resId);
        if (view != null){
            view.setOnClickListener(null);
        }
        return this;
    }

    public LHolder setOnItemLongClickListener(View.OnLongClickListener listener){
        itemView.setOnLongClickListener(listener);
        return this;
    }

    public LHolder setOnViewLongClickListener(@IdRes int resId, View.OnLongClickListener listener){
        final View view = getView(resId);
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
        return this;
    }

    public LHolder cleanOnItemLongClickListener(){
        itemView.setOnLongClickListener(null);
        return this;
    }

    public LHolder cleanOnViewLongClickListener(@IdRes int resId){
        final View view = getView(resId);
        if (view != null) {
            view.setOnLongClickListener(null);
        }
        return this;
    }

}
