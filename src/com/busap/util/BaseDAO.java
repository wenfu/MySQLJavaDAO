package com.busap.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.busap.bean.Animals;

public class BaseDAO {
	/**
	 * 查询表的全部数据方法
	 * 
	 * @param cl
	 * @return
	 */
	public ArrayList<Object> getList(Class<?> cl) {
		ArrayList<Object> ar = new ArrayList<Object>();
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select *  from " + cl.getSimpleName();
		Field[] fi = cl.getDeclaredFields();
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Object ob = cl.newInstance();// 实例化类对象
				for (Field ff : fi) {
					ff.setAccessible(true);
					ff.set(ob, rs.getObject(ff.getName()));
				}
				ar.add(ob);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps, rs);
		}

		return ar;
	}

	/**
	 * 根据表的主键查询其对象的方法
	 * 
	 * @param cl
	 * @param id
	 * @return
	 */
	public Object getObById(Class<?> cl, int id) {
		Object ob = null;
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Field[] fi = cl.getDeclaredFields();
		String sql = "select * from " + cl.getSimpleName() + " where " + fi[0].getName() + " = " + id;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				ob = cl.newInstance();
				for (Field ff : fi) {
					ff.setAccessible(true);
					ff.set(ob, rs.getObject(ff.getName()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps, rs);
		}
		return ob;
	}

	/**
	 * 根据一个未知的列名以及列的值查询表内容方法
	 * 
	 * @param cl
	 * @param name
	 * @param value
	 * @return
	 */
	public ArrayList<Object> getListBySome(Class<?> cl, String name, Object value) {
		ArrayList<Object> ar = new ArrayList<Object>();
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Field[] fi = cl.getDeclaredFields();
		String sql = "select * from " + cl.getSimpleName() + " where " + name + " = '" + value + "'";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Object ob = cl.newInstance();
				for (Field ff : fi) {
					ff.setAccessible(true);
					ff.set(ob, rs.getObject(ff.getName()));
				}
				ar.add(ob);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps, rs);
		}
		return ar;

	}

	/**
	 * 比较浪费系统内容的万能插入数据库方法
	 * 
	 * @param ob
	 * @return
	 */
	public boolean insertOrg(Object ob) {
		boolean b = false;
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		Class<? extends Object> cl = ob.getClass();
		Field[] fi = cl.getDeclaredFields();
		// insert into animals (name,age,anid) values(?,?,?)
		String sql = "insert into " + cl.getSimpleName() + " (";
		for (int i = 1; i < fi.length; i++) {
			sql = sql + fi[i].getName();
			if (i != fi.length - 1) {
				sql = sql + " , ";
			}
		}
		sql = sql + ") values (";
		for (int i = 1; i < fi.length; i++) {
			sql = sql + " ? ";
			if (i != fi.length - 1) {
				sql = sql + " , ";
			}
		}
		sql = sql + ")";
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 1; i < fi.length; i++) {
				fi[i].setAccessible(true);
				ps.setObject(i, fi[i].get(ob));
			}
			int a = ps.executeUpdate();
			if (a > 0) {
				b = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps);
		}
		return b;
	}

	/**
	 * 改良后的万能插入数据库数据的方法，采用StringBuffer及其append方法，节约系统资源。
	 * 
	 * @param ob
	 * @return
	 */
	public boolean insert(Object ob) {
		boolean b = false;
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		Class<? extends Object> cl = ob.getClass();
		Field[] fi = cl.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		// insert into animals (name,age,anid) values(?,?,?)
		sb.append("insert into ");
		sb.append(cl.getSimpleName());
		sb.append(" (");
		for (int i = 1; i < fi.length; i++) {
			sb.append(fi[i].getName());
			if (i != fi.length - 1) {
				sb.append(" , ");
			}
		}
		sb.append(") values (");
		for (int i = 1; i < fi.length; i++) {
			sb.append(" ? ");
			if (i != fi.length - 1) {
				sb.append(" , ");
			}
		}
		sb.append(" ) ");
		try {
			ps = conn.prepareStatement(sb.toString());
			for (int i = 1; i < fi.length; i++) {
				fi[i].setAccessible(true);
				ps.setObject(i, fi[i].get(ob));
			}
			int a = ps.executeUpdate();
			if (a > 0) {
				b = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps);
		}
		return b;
	}

	/**
	 * 万能更新数据库方法
	 * 
	 * @param ob
	 * @return
	 */
	public boolean update(Object ob) {
		boolean b = false;
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		Class<? extends Object> cl = ob.getClass();
		Field[] fi = cl.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		// update animals set name = ?,age = ?,anid = ? where id = ?
		sb.append(" update ");
		sb.append(cl.getSimpleName());
		sb.append(" set ");
		for (int i = 1; i < fi.length; i++) {
			fi[i].setAccessible(true);
			sb.append(fi[i].getName());
			sb.append(" = ? ");
			if (i != fi.length - 1) {
				sb.append(" , ");
			}
		}
		sb.append(" where ");
		sb.append(fi[0].getName());
		sb.append("=?");

		try {
			ps = conn.prepareStatement(sb.toString());
			for (int i = 1; i < fi.length; i++) {
				fi[i].setAccessible(true);
				ps.setObject(i, fi[i].get(ob));
			}
			fi[0].setAccessible(true);
			ps.setObject(fi.length, fi[0].get(ob));
			int a = ps.executeUpdate();
			if (a > 0) {
				b = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps);
		}
		return b;
	}

	/**
	 * 万能的删除数据方法，通过表的主键id来删除
	 * 
	 * @param cl
	 * @param id
	 * @return
	 */
	public boolean delete(Class<?> cl, int id) {
		boolean b = false;
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		Field[] fi = cl.getDeclaredFields();
		String sql = "delete from " + cl.getSimpleName() + " where " + fi[0].getName() + " = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, id);
			int a = ps.executeUpdate();
			if (a > 0) {
				b = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps);
		}
		return b;

	}

	/**
	 * 根据某列的值来删除数据
	 * 
	 * @param cl
	 * @param name
	 * @param value
	 * @return
	 */
	public boolean deleteBySome(Class<Animals> cl, String name, Object value) {
		boolean b = false;
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		// Field[] fi = cl.getDeclaredFields();
		String sql = "delete from " + cl.getSimpleName() + " where " + name + " = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1, value);
			int a = ps.executeUpdate();
			if (a > 0) {
				b = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps);
		}
		return b;

	}

	// 用于测试以上各个方法
	public static void main(String[] args) {
		BaseDAO bd = new BaseDAO();
		// Animals an = new Animals();
		// an.setName("金三胖");
		// an.setAge(15);
		// an.setAnId(1);
		// an.setId(3);
		// boolean b = bd.update(an);

		bd.deleteBySome(Animals.class, "name", "金九胖");
		// bd.delete(Animals.class, 5);
		// ArrayList<Animals> ar = bd.getList(Animals.class);
		// for(Animals an : ar){
		// System.out.println("编号： "+an.getId()+"名字："+an.getName()+"年龄: "+an.getAge());
		// }
		// ArrayList<AnType> arr = bd.getList(AnType.class);
		// for(AnType an : arr){
		// System.out.println("编号："+an.getAnId()+"名字："+an.getAnName());
		// }
		// Animals an = (Animals)bd.getObById(Animals.class, 2);
		// System.out.println(an.getName());
		// ArrayList<Animals> ar = bd.getListBySome(Animals.class, "age", "13");
		// for(Animals an : ar){
		// System.out.println("编号："+an.getId()+"名字："+an.getName()+"年龄:"+an.getAge());
		// }
	}
}
