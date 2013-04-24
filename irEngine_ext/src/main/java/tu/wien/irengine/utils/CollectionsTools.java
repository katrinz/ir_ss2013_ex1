/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 */
public class CollectionsTools {

    public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> orderByValuesAsc(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                        return e1.getValue().compareTo(e2.getValue());
                    }
                });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> orderByValuesDesc(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                        return e1.getValue().compareTo(e2.getValue()) * (-1);
                    }
                });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public static <T> Collection<T> take(Collection<T> coll, int number) {
        List<T> list = new ArrayList<T>();
        Iterator i = coll.iterator();
        int numerator = 0;
        while (i.hasNext() && number > numerator) {
            list.add((T) i.next());
            numerator++;
        }

        return list;
    }

    public static <T> Collection<T> skip(Collection<T> coll, int number) {
        coll.remove(take(coll, number));
        return coll;
    }
}
