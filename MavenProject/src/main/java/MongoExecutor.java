import java.sql.*;

/**
 * Created by trannguyen on 11/14/14.
 */
public class MongoExecutor implements QueryExecutor {
	private Connection conn;
	private String driverLink = "jdbc:mongo://localhost/test";
	
	public MongoExecutor() throws Exception{
		if(!connect()){
			throw new Exception("Could not initiate the the Mongo Executor");
		}
	}
	
	public MongoExecutor(String driverLink) throws Exception{
		this.driverLink = driverLink;
		if(!connect()){
			throw new Exception("Could not initiate the the Mongo Executor");
		}
	}
	
    /**
     * Each implementation of this will take in
     * the raw sql query and execute in its underlying database
     * returning how long it takes to execute that statement
     *
     * @param rawSQLQuery SQL statement
     * @return time it takes to excute those in mili seconds
     */
    public long executeQuery(String rawSQLQuery) {
    	long fStart = 0;
    	long fEnd = 0;
    	
	    ResultSet rs = null;
	    Statement stmt = null;
    	try {
    	    fStart = System.currentTimeMillis();
    	    stmt = conn.createStatement();
    	    if (stmt.execute(rawSQLQuery)) {
    	        rs = stmt.getResultSet();
    	        
    	    }
    	    int i=0;
    	    ResultSetMetaData meta = rs.getMetaData();
    	 // Print out a row of column headers
 			System.out.println("Total columns: " + meta.getColumnCount());
 			System.out.print(meta.getColumnName(1));
 			for (int j = 2; j <= meta.getColumnCount(); j++)
 				System.out.print(", " + meta.getColumnName(j));
 			System.out.println();
    	 // Print out all rows in the ResultSet
			while (rs.next()) 
			{
				System.out.print(rs.getObject(1));
				for (int j = 2; j <= meta.getColumnCount(); j++)
					System.out.print(", " + rs.getObject(j));
				System.out.println();
				i++;
			}		
    	    System.out.println(fEnd - fStart);
    	    return (fEnd - fStart);
    	    
    	}
    	catch (SQLException ex){
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
    	    return (fEnd - fStart);
    	}
    	finally {
    	    if (rs != null) {
    	        try {
    	            rs.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        rs = null;
    	    }

    	    if (stmt != null) {
    	        try {
    	            stmt.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        stmt = null;
    	    }
    	}
    }

    public boolean connect(){
    	try {
    	   Class.forName("mongodb.jdbc.MongoDriver");
 		   this.conn =DriverManager.getConnection(driverLink);
 		   System.out.println("Connected!");
 		   return true;
 		} catch (SQLException ex){
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
    	    return false;
    	} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
    }

	public void cleanUP() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	
	public static void main( String args[] ){
		MongoExecutor me;
		try {
			me = new MongoExecutor();
			me.executeQuery("SELECT * from test");
			me.cleanUP();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
