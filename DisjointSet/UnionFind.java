package DisjointSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnionFind<T> {

    private Map<T,T> set_holder;

    private Map<T,Integer> rank;

    public UnionFind()
    {
        set_holder=new HashMap<>();
        rank=new HashMap<>();
    }

    void makeSet(T object)
    {
        if(set_holder.containsKey(object))
            return;

        set_holder.put(object,object);
        rank.put(object,0);
    }


    void remove(T object)
    {
        this.set_holder.remove(object);
    }

    public T find(T object)
    {
        return findByPathCompression(object);
    }

    private T findByPathCompression(T object)
    {
        if(!set_holder.containsKey(object))
            return null;

        if(set_holder.get(object).equals(object))
            return object;


        else {
            T rep= findByPathCompression(set_holder.get(object));
            set_holder.put(object,rep);
            return findByPathCompression(set_holder.get(object));
        }
    }

    public T union(T x,T y)
    {
        return unionByRank(x,y);
    }

    private T unionByRank(T x,T y)
    {
        T x_rep= findByPathCompression(x);
        T y_rep= findByPathCompression(y);

        assert x_rep != null;
        if(x_rep.equals(y_rep))
            return x_rep;
     /*   else
            set_holder.put(y_rep, x_rep);
        return x_rep;
      */



        else
        {
            if(rank.get(x_rep) > rank.get(y_rep))
            {
                set_holder.put(y_rep,x_rep);
                return x_rep;

            }
            else if ((rank.get(x_rep) < rank.get(y_rep)))
            {
                set_holder.put(x_rep,y_rep);
                return y_rep;
            }
            else
            {
                set_holder.put(y_rep,x_rep);
                rank.put(x_rep,rank.get(x_rep)+1);
                return x_rep;
            }
        }
    }

    public Map<T,T> getSetHolder()
    {
        return this.set_holder;
    }

    public List<List<T>> getDisjointSets()
    {
        Map<T,List<T>> disjoint_sets_map=new HashMap<>();

        List<List<T>> disjoint_sets=new ArrayList<>();

        for(Map.Entry<T,T> mapEntry:set_holder.entrySet())
        {
            T value=mapEntry.getValue();
            T key= mapEntry.getKey();

            T rep= findByPathCompression(value);

            if(!disjoint_sets_map.containsKey(rep))
                disjoint_sets_map.put(rep, Stream.of(value).collect(Collectors.toList()));
            else
                disjoint_sets_map.get(rep).add(key);

        }

        for(Map.Entry<T,List<T>> mapEntry:disjoint_sets_map.entrySet())
        {
            List<T> value=mapEntry.getValue();
            disjoint_sets.add(value);
        }

        return disjoint_sets;

    }
}
