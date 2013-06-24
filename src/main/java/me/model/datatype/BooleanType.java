package me.model.datatype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import orangescape.exception.SessionException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.compare.EqualsHelper;
import org.hibernate.usertype.UserType;

/**
 * @author VivekMadurai
 *
 */
public class BooleanType implements UserType{

	private static final int[] SQL_TYPES = { Types.BOOLEAN };
	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	@Override
	public Class returnedClass() {
		return Double.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return EqualsHelper.equals(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {

		Double result = null;
		Boolean dbVal;
		try{
			dbVal = rs.getBoolean(names[0]);
		}catch (ClassCastException e) {
			String colName = names[0];
			String tabName = rs.getMetaData().getTableName(rs.findColumn(colName));
			throw new SessionException("Session1003", e, colName, tabName);
		}
		if (!rs.wasNull()) {
			result = dbVal == false ? 0.0 : 1.0;
		}
		return result;
	
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {

		if (value == null) {
			st.setBoolean(index, false);
		} else if((Double)value == 1.0){
			st.setBoolean(index, true);
		} else {
			st.setBoolean(index, false);
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		 return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

}
