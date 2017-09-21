package leicher.lrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("unused")
public abstract class LAdapter<T> extends RecyclerView.Adapter<LHolder>{


    protected List<T> list;

    public abstract LAdapter<T> list(List<T> list);

    public List<T> list(){
        return list;
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void clear(boolean notify){
        if (list != null){
            list.clear();
            if (notify){
                notifyDataSetChanged();
            }
        }
    }
    public void clear(){
        clear(true);
    }


    public interface Binder<T extends LAdapter> {
        void bind(T adapter,LHolder holder,int position);
    }

    public interface TypeBinder<T extends LAdapter> {
        void bind(T adapter,LHolder holder,int position,int viewType);
    }

    public interface Creator<T extends LAdapter>{
        LHolder create(T adapter,ViewGroup parent,int viewType);
    }

    public interface TypeGenerator<T extends LAdapter>{
        int generate(T adapter,int position);
    }

}
