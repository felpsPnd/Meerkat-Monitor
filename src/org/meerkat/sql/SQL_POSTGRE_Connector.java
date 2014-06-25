/**
 * Meerkat Monitor - Network Monitor Tool
 * Copyright (C) 2013 Merkat-Monitor
 * mailto: contact AT meerkat-monitor DOT org
 * 
 * Meerkat Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Meerkat Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with Meerkat Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.meerkat.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class SQL_POSTGRE_Connector {

	private static Logger log = Logger.getLogger(SQL_POSTGRE_Connector.class);
	private String dbMachine;
	private String port;
	private String dbname;
	private String username;
	private String password;

	Connection conn;

	/**
	 * SQL_POSTGRE_Connector
	 * 
	 * @param dbMachine
	 * @param port
	 * @param sid
	 * @param username
	 * @param password
	 */
	public SQL_POSTGRE_Connector(String dbMachine, String port, String dbname,
			String username, String password) {
		this.dbMachine = dbMachine;
		this.port = port;
		this.dbname = dbname;
		this.username = username;
		this.password = password;
	}

	/**
	 * executeQuery
	 * 
	 * @param query
	 * @return result
	 */
	public final String executeQuery(String query) {
		String result = "";
		try {
			conn = DriverManager.getConnection(
					   "jdbc:postgresql://" + dbMachine+ ":"+port+"/"+dbname, username, password);
		} catch (SQLException e) {
			log.error("SQL Exception getting connection: " + dbMachine + ":"
					+ port + ":" + dbname + " "+e.getMessage());
			result += e.getMessage();
			return result;
		}

		SQL_Statement_Executor executor = new SQL_Statement_Executor(conn, query);

		return executor.getResultQueryString();
	}

}
