package com.djg.algorithm.string;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * 有效的括号
 *
 * 思路：
 * 1. 左括号到来，入栈
 * 2. 右括号到来，弹出栈顶元素，看看是否是右括号对应的左括号
 * 3. 最后，如果栈空，则表明字符串有效
 *
 * https://github.com/jiajunhua/labuladong-fucking-algorithm/blob/master/%E9%AB%98%E9%A2%91%E9%9D%A2%E8%AF%95%E7%B3%BB%E5%88%97/%E5%90%88%E6%B3%95%E6%8B%AC%E5%8F%B7%E5%88%A4%E5%AE%9A.md
 * https://leetcode-cn.com/problems/valid-parentheses/solution/valid-parentheses-fu-zhu-zhan-fa-by-jin407891080/
 *
 */


public class ValidBrackets {
    private static final Map<Character, Character> map = new HashMap<Character, Character>() {{
        put('{', '}');
        put('[', ']');
        put('(', ')');
    }};

    public boolean isValid(String s) {
        // 边界，左括号不在范围内
        if (s.length() > 0 && !map.containsKey(s.charAt(0)))
            return false;

        LinkedList<Character> stack = new LinkedList<Character>();

        for (Character c : s.toCharArray()) {
            // 左括号到来，入栈
            if (map.containsKey(c))
                stack.addLast(c);
            // 右括号到来，弹出栈顶元素，看是否是与右括号匹配的左括号
            else if (map.get(stack.removeLast()) != c)
                return false;
        }

        // 栈空，则表明字符串有效
        return stack.size() == 0;
    }
}

