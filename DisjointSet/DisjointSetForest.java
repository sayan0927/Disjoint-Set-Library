import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DisjointSetForest<T> implements DisjointSet<T>{

    /**
     *  Holds the set objects where key is the object and value is the representative
     */
    private final Map<T,T> set_holder;

    /**
     *  Holds the Rank of elements in the DisjointSetForest
     */
    private final Map<T,Integer> rank;

    /**
     *  Construct an empty DisjointSetForest Object
     */
    public DisjointSetForest()
    {
        set_holder=new HashMap<>();
        rank=new HashMap<>();
    }

    /**
     *  Construct a new DisjointSetForest Object with the supplied DisjointSetForest object
     * @param disjointSetForest creates a new DisjointSetForest with this
     */
    public DisjointSetForest(DisjointSetForest<T> disjointSetForest)
    {
        set_holder=new HashMap<>(disjointSetForest.set_holder);
        rank=new HashMap<>(disjointSetForest.rank);

    }

    /**
     *  Creates a new DisjointSet with the supplied object
     *  @param object the value of the DisjointSet
     */
    public void makeSet(T object)
    {
        if(set_holder.containsKey(object))
            return;

        set_holder.put(object,object);
        rank.put(object,0);
    }

    /**
     *  Return true if supplied objects belong to same DisjointSet
     *  @param x first object
     *  @param y second object
     */
    public boolean isConnected(T x,T y)
    {
        return set_holder.get(x).equals(set_holder.get(y));
    }



    void remove(T object)
    {


        Set<T> toRemove=new HashSet<>();
        Set<T> toAddAgain=new HashSet<>();

        for(Map.Entry<T,T> mapEntry:set_holder.entrySet()) {
            T parent=mapEntry.getValue();
            T child= mapEntry.getKey();



            //{0=0, 1=0, 2=0, 3=3, 4=3, 5=3, 6=6, 7=6, 8=6, 9=9}
            // suppose our set is like this  where key is object and value is representative
            // now if  we want to remove 6,then we have to remove 6,7,8 first then add 7,8 again


            //7,8 added to toAddAgain
            if(parent.equals(object) && !child.equals(object))
                toAddAgain.add(child);

            // 6,7,8 added to toRemove
            if(parent.equals(object) || child.equals(object))
                toRemove.add(child);

        }

        //removing 6,7,8
        for(T obj:toRemove) {
            set_holder.remove(obj);
            rank.remove(obj);
        }

        //making new set of 7,8
        for(T obj:toAddAgain)
            makeSet(obj);
    }


    /**
     *  Returns the Representative Object of the supplied Object
     *  @param object Finds Representative of this
     */
    public T find(T object)
    {
        return findByPathCompression(object);
    }

    /**
     *  Returns the Representative Object of the supplied Object and uses Path Compression to shorten distance to Representative
     *  @param object Finds Representative of this
     */
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

    /**
     *  Replaces the set containing x and the set containing y with their union.
     *  @param x first object
     *  @param y second object
     */
    public T union(T x,T y)
    {
        return unionByRank(x,y);
    }

    /**
     *  Uses the Union By Rank Scheme to shorten height of the Trees in the Forest
     *  @param x first object
     *  @param y second object
     */
    private T unionByRank(T x,T y)
    {

        T x_rep= findByPathCompression(x);
        T y_rep= findByPathCompression(y);

        assert x_rep != null;
        if(x_rep.equals(y_rep))
            return x_rep;

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

    @Deprecated
    public Map<T,T> getSetHolder()
    {
        return this.set_holder;
    }

    @Deprecated
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

    /**
     *  Returns a String representation of the DisjointSetForest
     */
    @Override
    public String toString()
    {
        return this.set_holder+"\n"+this.rank;
    }
}
