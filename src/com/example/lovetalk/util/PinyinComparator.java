package com.example.lovetalk.util;

import java.util.Comparator;

import com.avos.avoscloud.AVUser;

public class PinyinComparator implements Comparator<AVUser> {

	public int compare(AVUser o1, AVUser o2) {
		String o1Letters, o2Letters;
		o1Letters = getSortLetters(o1);
		o2Letters = getSortLetters(o2);
		if (o1Letters.equals("@") || o2Letters.equals("#")) {
			return -1;
		} else if (o1Letters.equals("#") || o2Letters.equals("@")) {
			return 1;
		} else {
			return o1Letters.compareTo(o2Letters);
		}
	}

	private String getSortLetters(AVUser user) {
		String username = user.getUsername();
		if (username != null) {
			String pinyin = CharacterParser.getPingYin(user.getUsername());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if (sortString.matches("[A-Z]")) {
				return sortString.toUpperCase();
			} else {
				return "#";
			}
		} else {
			return "#";
		}
	}
}
