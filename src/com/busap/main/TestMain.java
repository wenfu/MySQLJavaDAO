package com.busap.main;

import java.util.ArrayList;

import com.busap.bean.AnType;
import com.busap.bean.Animals;
import com.busap.dao.AnTypeDAO;
import com.busap.dao.AnimalsDAO;

public class TestMain {
	public static void main(String[] args) {
		AnimalsDAO ad = new AnimalsDAO();
		ArrayList<Animals> ar = ad.getList();
		for (Animals an : ar) {
			System.out.println(an.getName());
		}
		AnTypeDAO atd = new AnTypeDAO();
		ArrayList<AnType> arr = atd.getList();
		for (AnType an : arr) {
			System.out.println(an.getAnName());
		}

	}
}
