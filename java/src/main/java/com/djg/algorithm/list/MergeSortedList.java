package com.djg.algorithm.list;

/**
 * 合并有序链表
 *
 * 思路：
 * 1. 双指针法，找到l1和l2中值较小的，追加到结果链表后面，然后指针往前走
 *
 * https://leetcode-cn.com/problems/he-bing-liang-ge-pai-xu-de-lian-biao-lcof/solution/mian-shi-ti-25-he-bing-liang-ge-pai-xu-de-lian-b-2/
 *
 */

class MergeSortedList {
    // 非递归实现
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dum = new ListNode(0);
        ListNode cur = dum;
        while (l1 != null && l2 != null) {
            // 如果l1小，将l1追加到结果链表后
            if (l1.val < l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                // 如果l2小，将l2追加到结果链表后
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        // l1和l2中非空的链表，追加到结果链表后
        cur.next = l1 != null ? l1 : l2;
        return dum.next;
    }


    // 递归实现

    public ListNode mergeTwoLists2(ListNode l1, ListNode l2) {
        //递归实现
        return recur(l1, l2);
    }

    public ListNode recur(ListNode l1, ListNode l2) {
        // 基线条件
        if (l1 == null && l2 == null) return null;
        if (l1 == null) return l2;
        if (l2 == null) return l1;

        //新建头结点
        ListNode head = null;

        //如果l1.val <= l2.val，那么头结点的值为l1.head的值，然后开始递归
        if (l1.val <= l2.val) {
            head = new ListNode(l1.val);
            head.next = recur(l1.next, l2);
        } else {
            //如果l2.val <= l1.val，头结点的值为l2.head的值，然后开始递归
            head = new ListNode(l2.val);
            head.next = recur(l1, l2.next);
        }

        //返回该链表
        return head;
    }
}

