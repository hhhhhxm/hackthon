package com.kezaihui.faq.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 00026
 * @date 2021-02-22 11:31
 * @descript
 */
public class ListEditDistance {
    private static final WeakHashMap<AbstractMap.SimpleEntry<List<String>, List<String>>, Integer> CACHE = new WeakHashMap();
    private int[] cost;
    private int[] back;
    private final List<String> a;
    private final List<String> b;

    public static int editDistance(List<String> a, List<String> b) {
//        AbstractMap.SimpleEntry<List<String>, List<String>> entry = new AbstractMap.SimpleEntry(a, b);
//        Integer result = null;
//        if (CACHE.containsKey(entry)) {
//            result = (Integer) CACHE.get(entry);
//        }
//
//        if (result == null) {
//            result = (new ListEditDistance(a, b)).calc();
//            CACHE.put(entry, result);
//        }

        return editDistance2(a,b);
    }

    public static int editDistance2(List<String> a, List<String> b) {

        return (new ListEditDistance(a, b)).calc();
    }

    public static List<String> findNearest(List<String> key, List<String> group) {
        return findNearest(key, (Collection) group);
    }

    public static List<String> findNearest(List<String> key, Collection<String> group) {
        int c = 2147483647;
        List<String> r = null;
        Iterator<String> i$ = group.iterator();

        while (i$.hasNext()) {
            List<String> s = new ArrayList<>();
            s.add(i$.next());
            int ed = editDistance(key, s);
            if (c > ed) {
                c = ed;
                r = s;
            }
        }

        return r;
    }

    private ListEditDistance(List<String> a, List<String> b) {
        this.a = a;
        this.b = b;
        this.cost = new int[a.size() + 1];
        this.back = new int[a.size() + 1];

        for (int i = 0; i <= a.size(); this.cost[i] = i++) {
            ;
        }

    }

    private void flip() {
        int[] t = this.cost;
        this.cost = this.back;
        this.back = t;
    }

    private int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    private int calc() {
        for (int j = 0; j < this.b.size(); ++j) {
            this.flip();
            this.cost[0] = j + 1;

            for (int i = 0; i < this.a.size(); ++i) {
                int match = Objects.equals(this.a.get(i), this.b.get(j)) ? 0 : 1;
                this.cost[i + 1] = this.min(this.back[i] + match, this.cost[i] + 1, this.back[i + 1] + 1);
            }
        }

        return this.cost[this.a.size()];
    }
}
