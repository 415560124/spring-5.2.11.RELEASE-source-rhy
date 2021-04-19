package com.rhy;

/**
 * @author: Herion Lemon
 * @date: 2021/3/21 17:21
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: 其他测试用的mainstart
 */
public class OtherMainStart {
	public static void main(String[] args) {
		OtherMainStart otherMainStart = new OtherMainStart();
		System.out.println(otherMainStart.test());
	}
	public int test(){
		try{
			System.out.println(1);
			return 2;
		}finally {
			System.out.println(3);
		}
	}
}
