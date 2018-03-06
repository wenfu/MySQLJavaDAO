package com.busap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.busap.bean.AnType;
import com.busap.util.BaseConnection;

public class AnTypeDAO {
	public ArrayList<AnType> getList() {
		ArrayList<AnType> ar = new ArrayList<AnType>();
		Connection conn = BaseConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from antype";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				AnType an = new AnType();
				an.setAnId(rs.getInt("anid"));
				an.setAnName(rs.getString("anname"));
				ar.add(an);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseConnection.closeRes(conn, ps, rs);
		}
		return ar;
	}

}
