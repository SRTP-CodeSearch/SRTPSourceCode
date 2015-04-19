package cn.edu.seu.ise.common.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.NonNull;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

/**
 * A helper class used to improve the use of class {@link Map}. 
 * 
 * @author Dong Qiu
 *
 */
public class MapHelper {
	
	/**
	 * Updates the integer value of the map. 
	 * 
	 * @param map  the map to be updated
	 * @param k  the key 
	 * @param value  the added value
	 */
	public static <K> void updateValue(@NonNull Map<K, MutableInt> map, @NonNull K k, int value) {
		if(!map.containsKey(k)) {
			map.put(k, new MutableInt(0));
		}
		map.get(k).add(value);
	}
	
	/**
	 * Updates the long value of the map. 
	 * 
	 * @param map  the map to be updated
	 * @param k  the key 
	 * @param value  the added value
	 */
	public static <K> void updateValue(@NonNull Map<K, MutableLong> map, @NonNull K k, long value) {
		if(!map.containsKey(k)) {
			map.put(k, new MutableLong(0));
		}
		map.get(k).add(value);
	}
	
	/**
	 * Increments the value of corresponding key. 
	 * 
	 * @param map  the map
	 * @param k  the key
	 */
	public static <K> void updateValue(@NonNull Map<K, MutableInt> map, @NonNull K k) {
		updateValue(map, k, 1);
	}
	
	/**
	 * Updates the map list. 
	 * @param map  the map where the value is the list
	 * @param k  the key to be added
	 * @param v  the value to be added
	 */
	public static <K, V> void updateListMap(@NonNull Map<K, List<V>> map, @NonNull K k, @NonNull V v) {
		if(!map.containsKey(k)) {
			map.put(k, new ArrayList<V>());
		}
		map.get(k).add(v);
	}
	
	
	/**
	 * Parses the values in the map from String into Integer. 
	 * 
	 * @param map  the map to be parsed
	 * @return  the parsed map
	 */
	public static <K> Map<K, Integer> parseIntValue(@NonNull Map<K, String> map) {
		Map<K, Integer> parsedMap = new HashMap<>();
		for(Map.Entry<K, String> entry : map.entrySet()) {
			parsedMap.put(entry.getKey(), Integer.parseInt(entry.getValue()));
		}
		return parsedMap;
	}
	
	/**
	 * Parses the values in the map from String into Double. 
	 * 
	 * @param map  the map to be parsed
	 * @return  the parsed map
	 */
	public static <K> Map<K, Double> parseDoubleValue(@NonNull Map<K, String> map) {
		Map<K, Double> parsedMap = new HashMap<>();
		for(Map.Entry<K, String> entry : map.entrySet()) {
			parsedMap.put(entry.getKey(), Double.parseDouble(entry.getValue()));
		}
		return parsedMap;
	}
	
	/**
	 * Transform the values in the map into percentage. 
	 * 
	 * @param map  the map to be transformed
	 * @param decimal  the decimal length of the percentage value
	 * @return  the transformed map
	 */
	public static <K, V extends Number> Map<K, Double> pctMap(@NonNull Map<K, V> map, int decimal) {
		if(decimal < 0) {
			throw new IllegalArgumentException("The decimal length must be larger than 0");
		}
		MutableDouble total = new MutableDouble(0);
		for(Map.Entry<K, V> entry : map.entrySet()) {
			total.add(entry.getValue().doubleValue());
		}
		Map<K, Double> pctMap = new HashMap<>();
		
		for(Map.Entry<K, V> entry : map.entrySet()) {
			double pctValue = NumberHelper.round(entry.getValue().doubleValue() 
					/ total.doubleValue(), decimal);
			pctMap.put(entry.getKey(), pctValue);
		}
		return pctMap;
	}
	
	/**
	 * Transform the values in the map into percentage. 
	 * 
	 * @param map  the map to be transformed
	 * @return  the transformed map
	 */
	public static <K, V extends Number> Map<K, Double> pctMap(@NonNull Map<K, V> map) {
		return pctMap(map, 8);
	}
	
	/**
	 * Transform the values in the inner map of the nested map into percentage. 
	 * 
	 * @param map  the map to be transformed
	 * @param decimal  the decimal length of the percentage value
	 * @return  the transformed map
	 */
	public static <K, V> Map<K, Map<V, Double>> pctNestedMap(@NonNull Map<K, Map<V, MutableLong>> map, int decimal) {
		if(decimal < 0) {
			throw new IllegalArgumentException("The decimal length must be larger than 0");
		}
		Map<K, Map<V, Double>> pctMap = new HashMap<>();
		Map<V, MutableLong> mergedMap = mergeNestedMap(map);
		for(Map.Entry<K, Map<V, MutableLong>> entry : map.entrySet()) {
			Map<V, Double> pct = new HashMap<>();
			for(Map.Entry<V, MutableLong> innerEntry : entry.getValue().entrySet()) {
				double pctValue = NumberHelper.round(innerEntry.getValue().doubleValue() 
						/ mergedMap.get(innerEntry.getKey()).doubleValue(), decimal);
				pct.put(innerEntry.getKey(), pctValue);
			}
			pctMap.put(entry.getKey(), pct);
		}
		return pctMap;
	}
	
	/**
	 * Transform the values in the inner map of the nested map into percentage. 
	 * 
	 * @param map  the map to be transformed
	 * @return  the transformed map
	 */
	public static <K, V> Map<K, Map<V, Double>> pctNestedMap(@NonNull Map<K, Map<V, MutableLong>> map) {
		return pctNestedMap(map, 8);
	}
	
	/**
	 * Transform the values in the inner map of the nested map into percentage. 
	 * 
	 * @param map  the nested map where the value is a inner map in which 
	 * the key is the index, and the value is the number
	 * @param sum  the map that used to calculate the percentage
	 * @param decimal  the decimal length of the percentage value
	 *  
	 * @return  the transformed map
	 */
	public static <K, I, V extends Number, U extends Number> Map<K, Map<I, Double>> 
		pctNestedMap(@NonNull Map<K, Map<I, V>> map, @NonNull Map<I, U> sum, int decimal) {
		if(decimal < 0) {
			throw new IllegalArgumentException("The decimal length must be larger than 0");
		}
		Map<K, Map<I, Double>> pctMap = new HashMap<>();
		for(Map.Entry<K, Map<I, V>> entry : map.entrySet()) {
			Map<I, Double> pctItem = new HashMap<>();
			for(Map.Entry<I, V> innerEntry : entry.getValue().entrySet()) {
				if(sum.containsKey(innerEntry.getKey())) {
					pctItem.put(innerEntry.getKey(), innerEntry.getValue().doubleValue() 
							/ sum.get(innerEntry.getKey()).doubleValue());
				} else {
					pctItem.put(innerEntry.getKey(), Double.NaN);
				}
			}
			pctMap.put(entry.getKey(), pctItem);
		}
		return pctMap;
	}
	
	/**
	 * Transform the values in the inner map of the nested map into percentage. 
	 * 
	 * @param map  the nested map where the value is a inner map in which 
	 * the key is the index, and the value is the number
	 * @param sum  the map that used to calculate the percentage 
	 * @return  the transformed map
	 */
	public static <K, I, V extends Number, U extends Number> Map<K, Map<I, Double>> 
		pctNestedMap(@NonNull Map<K, Map<I, V>> map, @NonNull Map<I, U> sum) {
		return pctNestedMap(map, sum, 8);
	}
	
	
	/**
	 * Merges a nested map by given categories. 
	 * 
	 * @param map  the nested map to be merged
	 * @param categories  the category to merge the map
	 * @return  the merged map
	 */
	public static <P, I, K> Map<K, Map<I, MutableLong>> mergeNestedMap(@NonNull Map<P, Map<I, MutableLong>> map, 
			@NonNull Map<K, Set<P>> categories) {
		Map<K, Map<I, MutableLong>> mergedMap = new HashMap<>();
		for(Map.Entry<K, Set<P>> entry : categories.entrySet()) {
			Map<I, MutableLong> mergedValue = new HashMap<>();
			for(P p : entry.getValue()) {
				merge(mergedValue, map.get(p));
			}
			mergedMap.put(entry.getKey(), mergedValue);
		}
		return mergedMap;
	}
	
	/**
	 * Merges a nested map by the key of the inner map. 
	 * 
	 * @param map  the map to be merged. 
	 * @return  the merged map.
	 */
	public static <P, I> Map<I, MutableLong> mergeNestedMap(@NonNull Map<P, Map<I, MutableLong>> map) {
		Map<I, MutableLong> mergedMap = new HashMap<>();
		for(Map.Entry<P, Map<I, MutableLong>> entry : map.entrySet()) {
			merge(mergedMap, entry.getValue());
		}
		return mergedMap;
	}
	
	/**
	 * Merges a map by the given categories. 
	 * 
	 * @param map  the map to be merged
	 * @param categories  the categories to merge the map
	 * @return  the merged map
	 */
	public static <P, K> Map<K, MutableLong> mergeMap(@NonNull Map<P, MutableLong> map, 
			@NonNull Map<K, Set<P>> categories) {
		Map<K, MutableLong> mergedMap = new HashMap<>();
		for(Map.Entry<K, Set<P>> entry : categories.entrySet()) {
			MutableLong mergedSize = new MutableLong(0);
			for(P p : entry.getValue()) {
				if(map.containsKey(p)) {
					mergedSize.add(map.get(p).longValue());
				}
			}
			mergedMap.put(entry.getKey(), mergedSize);
		}
		return mergedMap;
	}
	
	/**
	 * Merges the second map into the first map according to the key. 
	 * 
	 * @param firstMap  the first map
	 * @param secondMap  the second map
	 */
	public static <K, V extends Mutable<Number>> void merge(@NonNull Map<K, V> first, 
			@NonNull Map<K, V> second) {
		for(Map.Entry<K,V> entry : second.entrySet()) {
			if(first.containsKey(entry.getKey())) {
				first.get(entry.getKey()).setValue(first.get(entry.getKey()).getValue().doubleValue() 
						+ entry.getValue().getValue().doubleValue());
			} else {
				first.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/**
	 * Sorts map by values with given order. 
	 * 
	 * @param map  the map to be sorted
	 * @param asc  {@code true} if the map is ordered by value in ascending order;
	 * {@code false} otherwise
	 * @return  the sorted map
	 */
	public static <K,V extends Comparable<V>> Map<K,V> sortByValues(@NonNull Map<K,V> map, 
			final boolean asc) {
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {
			@Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
            	int compareValue = o1.getValue().compareTo(o2.getValue());
                return asc ? compareValue : -compareValue;
            }
        });
      
        // LinkedHashMap will keep the keys in the order they are inserted
        // which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
	
	/**
	 * Orders the map by the order of the key. 
	 * 
	 * @param map  the map to be ordered
	 * @param order  the key order
	 * @return  the ordered map by given key order
	 */
	public static <K,V> LinkedHashMap<K, V> orderByKey(@NonNull Map<K,V> map, List<K> order) {
		LinkedHashMap<K, V> ordered = new LinkedHashMap<>();
		for(K k : order) {
			if(map.containsKey(k)) {
				ordered.put(k, map.get(k));
			}
		}
		return ordered;
	}
    
    /**
     * Gets top K key-value pairs based on the values.
     * 
     * @param map  the map 
     * @param size  the K value
     * @return  the map with K key-value pairs
     */
	public static <K, V extends Comparable<V>> Map<K,V> mostByValues(@NonNull Map<K,V> map, int size) {
		if(size < 0) {
			throw new IllegalArgumentException("The size must be larger than 0");
		}
    	Map<K,V> sortedMap = sortByValues(map, false);
    	// keeps the order
    	Map<K,V> selectMap = new LinkedHashMap<>();
    	int index = 0;
    	for(Map.Entry<K, V> entry : sortedMap.entrySet()) {
    		selectMap.put(entry.getKey(), entry.getValue());
    		index ++;
    		if(index >= size) {
    			break;
    		}
    	}
    	return selectMap;
    }
}
