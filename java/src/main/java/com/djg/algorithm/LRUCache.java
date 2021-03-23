package com.djg.algorithm;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

/*
 * @Author International Dai
 * @Date 10:44 2021-03-09
 * @Description
 *
 *
 * 技巧：hashmap + 双端队列
 * 其中，hashmap通过key指向双端队列中的node，加快get查询速度。可以看作双写问题，每一步操作双端队列，都要考虑map
 *
 * https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E7%B3%BB%E5%88%97/LRU%E7%AE%97%E6%B3%95.md
 *
 */


class Node {
    int key, val;
    Node(int k, int v) {
        this.key = k;
        this.val = v;
    }
}

public class LRUCache {
    // key -> Node(key, val)
    private HashMap<Integer, Node> map;
    // Node(k1, v1) <-> Node(k2, v2)...
    private Deque cache;
    // 最大容量
    private int cap;

    public LRUCache(int capacity) {
        this.cap = capacity;
        map = new HashMap<>();
        cache = new LinkedList();
    }

    public int get(int key) {
        if (!map.containsKey(key))
            return -1;
        int val = map.get(key).val;
        // 利用 put 方法把该数据提前
        put(key, val);
        return val;
    }

    public void put(int key, int val) {
        // 先把新节点 x 做出来
        Node x = new Node(key, val);

        if (map.containsKey(key)) {
            // 删除旧的节点，新的插到头部
            cache.remove(map.get(key));
            cache.addFirst(x);
            // 更新 map 中对应的数据
            map.put(key, x);
        } else {
            if (cap == cache.size()) {
                // 删除链表最后一个数据
                Node last = (Node) cache.removeLast();
                map.remove(last.key);
            }
            // 直接添加到头部
            cache.addFirst(x);
            map.put(key, x);
        }
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(2);
        lruCache.put(1, 2);
        lruCache.put(2, 3);
        lruCache.put(3, 4);

        System.out.println(lruCache.get(2));



    }
}