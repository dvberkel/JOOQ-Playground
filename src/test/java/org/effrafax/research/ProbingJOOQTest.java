package org.effrafax.research;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.effrafax.research.tables.Posts;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This test expects the following
 * <ol>
 * <li>A running Postgres Database with the following properties
 * <ol>
 * <li>a name of <code>jooq</code></li>
 * <li>accesable to a user <code>jooq</code> with password <code>jooq</code></li>
 * </ol>
 * </li>
 * <li>A table with the following properties
 * <ol>
 * <li>a name of <code>posts</code></li>
 * <li>at least one entry</li>
 * </ol>
 * </li>
 * </ol>
 * 
 * @author dvberkel
 * 
 */
public class ProbingJOOQTest {
	private static Connection connection;

	@BeforeClass
	public static void createConnection() {
		connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/jooq", "jooq", "jooq");
		} catch (SQLException e) {
			// The connection test will check the occurence of this problem.
		}
	}

	private PublicFactory factory;

	@Before
	public void createPublicFactory() {
		factory = new PublicFactory(connection);
	}

	@Test
	public void connectionShouldExist() {
		assertNotNull(connection);
	}

	@Test
	public void factoryShouldBeAvailable() {
		assertNotNull(factory);
	}

	@Test
	public void factoryShouldProvideSelectMethod() throws SQLException {
		assertNotNull(factory.select().from(Posts.POSTS).fetch());
	}

	@Test
	public void resultHasASize() throws SQLException {
		Result<Record> result = factory.select().from(Posts.POSTS).fetch();

		int numberOfRecords = result.size();

		assertTrue(numberOfRecords > 0);
	}

	@Test
	public void resultIsIterable() throws SQLException {
		Result<Record> result = factory.select().from(Posts.POSTS).fetch();
		List<Integer> ids = new ArrayList<Integer>();

		for (Record record : result) {
			ids.add(record.getValue(Posts.POSTS.ID));
		}

		assertTrue(ids.contains(0));
	}

	@AfterClass
	public static void destroyConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// Cannot do anything about this problem.
			}
		}
	}
}
