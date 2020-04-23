package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadFile {
	public static void main(String[] args) {
		new File(".").exists();
		System.out.println(new File(".").getAbsolutePath());
	}
	public static String toArrayByFileReader(String name) {
		// 使用ArrayList来存储每行读取到的字符串
		ArrayList<String> arrayList = new ArrayList<>();
		try {
			FileReader fr = new FileReader(name);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			// 按行读取字符串
			while ((str = bf.readLine()) != null) {
				arrayList.add(str);
			}
			bf.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
	for(String s:arrayList) {
		sb.append(s);
	}
		// 返回数组
		return sb.toString();
	}
	
	public static ArrayList<String> toArrayArrayByFileReader(String name) {
		// 使用ArrayList来存储每行读取到的字符串
		ArrayList<String> arrayList = new ArrayList<>();
		try {
			 InputStreamReader isr = new InputStreamReader(new FileInputStream(name), "UTF-8"); //或GB2312,GB18030
	          BufferedReader read = new BufferedReader(isr);
			String str;
			// 按行读取字符串
			while ((str = read.readLine()) != null) {
				arrayList.add(str);
			}
			read.close();
			isr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 返回数组
		return arrayList;
	}
	
	public static ArrayList<ArrayList<Integer>> toArrayNumbersByFileReader(String name,String split) {
		// 使用ArrayList来存储每行读取到的字符串
		ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();
		try {
			FileReader fr = new FileReader(name);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			// 按行读取字符串
			while ((str = bf.readLine()) != null) {
				String[] nums = str.split(" ");
				ArrayList<Integer> listNums = new ArrayList<>();
				for(String strNum:nums) {
					listNums.add(Integer.parseInt(strNum));
				}
				if(listNums.size() > 0)
					arrayList.add(listNums);
			}
			bf.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 返回数组
		return arrayList;
	}
}
