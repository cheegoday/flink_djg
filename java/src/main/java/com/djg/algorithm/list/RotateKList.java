package com.djg.algorithm.list;

// 旋转链表
// https://leetcode-cn.com/problems/rotate-list/solution/xuan-zhuan-lian-biao-by-leetcode-solutio-woq1/

// 思路：新链表的最后一个节点，是旧链表的第(n−1)−(k mod n)个节点，这是一个观察得到的规律。
// 将链表尾结点的next指向头结点，形成环。然后从(n−1)−(k mod n)对应的节点后面断开


class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}


public class RotateKList {
    public ListNode rotateRight(ListNode head, int k) {
        if (k == 0 || head == null || head.next == null) {
            return head;
        }
        int n = 1;
        ListNode iter = head;
        // 遍历链表，找到尾结点
        while (iter.next != null) {
            iter = iter.next;
            n++;
        }
        int add = n - k % n;
        if (add == n) {
            return head;
        }
        // 尾结点next指向头结点，形成环
        iter.next = head;

        // 找到(n−1)−(k mod n)在旧链表中对应的node
        while (add-- > 0) {
            iter = iter.next;
        }
        ListNode ret = iter.next;
        // 截断，形成新的链表
        iter.next = null;
        return ret;
    }
}
