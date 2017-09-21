package leicher.lrecyclerview.adapter;


import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("unused")
public class SingleTypeAdapter<T> extends  LAdapter <T>{

    @LayoutRes
    protected int layout;

    LayoutInflater inflater;

    private Binder<SingleTypeAdapter<T>> binder;

    public SingleTypeAdapter<T> binder(Binder<SingleTypeAdapter<T>> binder){
        this.binder = binder;
        return this;
    }

    @Override
    public SingleTypeAdapter<T> list(List<T> list) {
        this.list = list;
        return this;
    }

    public SingleTypeAdapter<T> layout(@LayoutRes int layout){
        this.layout = layout;
        return this;
    }

    public @LayoutRes int layout(){
        return layout;
    }

    @Override
    public LHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new LHolder(inflater.inflate(layout,parent,false));
    }

    @Override
    public void onBindViewHolder(LHolder holder, int position) {
        if (binder != null){
            binder.bind(this,holder,position);
        }
    }

}
