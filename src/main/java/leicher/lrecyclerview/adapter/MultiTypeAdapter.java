package leicher.lrecyclerview.adapter;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
@SuppressWarnings("unused")
public class MultiTypeAdapter<T> extends LAdapter<T>{

    protected Creator<MultiTypeAdapter<T>> creator;

    protected TypeBinder<MultiTypeAdapter<T>> binder;

    protected TypeGenerator<MultiTypeAdapter<T>> generator;

    LayoutInflater inflater;


    protected SparseIntArray layouts;

    @Override
    public MultiTypeAdapter<T> list(List<T> list) {
        this.list = list;
        return this;
    }

    public MultiTypeAdapter<T> creator(Creator<MultiTypeAdapter<T>> creator){
        this.creator = creator;
        return this;
    }

    public MultiTypeAdapter<T> binder(TypeBinder<MultiTypeAdapter<T>> binder){
        this.binder = binder;
        return this;
    }

    public MultiTypeAdapter<T> generator(TypeGenerator<MultiTypeAdapter<T>> generator){
        this.generator = generator;
        return this;
    }

    /*
        SparseIntArray key is viewType , value is resLayout
     */
    public MultiTypeAdapter<T> layouts(SparseIntArray layouts){
        this.layouts = layouts;
        return this;
    }

    @Override
    public LHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null){
            inflater = LayoutInflater.from(parent.getContext());
        }
        if (creator == null){
            if (layouts == null) {
                throw new IllegalArgumentException("MultiTypeAdapter creator can not be null");
            }else {
                int resLayout = layouts.get(viewType);
                if (resLayout == 0){
                    throw new IllegalArgumentException("can not find layout of viewType = " + viewType);
                }
                return new LHolder(inflater.inflate(resLayout,parent,false));
            }
        }
        return creator.create(this,parent,viewType);
    }

    @Override
    public void onBindViewHolder(LHolder holder, int position) {
        if (binder != null){
            binder.bind(this,holder,position,getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (generator != null){
            return generator.generate(this,position);
        }
        return super.getItemViewType(position);
    }
}
