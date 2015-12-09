package com.gu.xiongdilian.utils;

import com.gu.xiongdilian.pojo.Account;

import java.util.Comparator;

/**
 * @author nate
 * @ClassName: PinyinComparator
 * @Description: 汉子拼音管理类
 * @date 2015-6-5 下午3:37:29
 */
public class PinyinComparator implements Comparator<Account> {
    public int compare(Account o1, Account o2) {
        if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}