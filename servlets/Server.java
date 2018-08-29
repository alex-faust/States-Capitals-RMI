package servlet;

import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;

public class Server extends UnicastRemoteObject implements ServerInterface
{
    /**
     * Alex
     * a program that allows user to remotely connect to the server
     *	and allows use of these methods.
     */

    private static final long serialVersionUID = 1L;
    private static final String FILENAME = "US_states";
    private static String userName = "DLEWIS36";
    private static String passWord = "*******";
    private static String tableName = "States_Capitals";

    protected Server() throws RemoteException   {   super(); }

    @Override
    public String[] getStates(String state_regex) throws RemoteException, SQLException
    {
        String states[]   = new String[50];
        String capitals[] = new String[50];

        parseUSStates(states, capitals);

        Connection conn = dbConnect(userName, passWord);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE " + tableName);

        String tableCreateQuery = "create table " + tableName 
                + "(state varchar2(15), capital varchar2(15))";
        if(!tableExist(tableName, stmt))
        {
            stmt.execute(tableCreateQuery);
            for(int i = 0; i < states.length; i++)
            {
                String insertIntoTable = "insert into " + tableName 
                        + " ( state, capital ) VALUES ( '" 
                        + states[i] +"' , '" + capitals[i] + "')";

                stmt.execute(insertIntoTable);
            }
        }           

        ArrayList<String> results = new ArrayList<>();
        ResultSet rset = stmt.executeQuery("SELECT state FROM  " 
                + tableName + " WHERE REGEXP_LIKE (state, '"
                + state_regex + "', 'i')");


        while(rset.next())
        {
            results.add(rset.getString(1));
        }      

        rset.close();
        stmt.close();
        conn.close();

        String[] resultsArray = new String[results.size()];
        resultsArray = results.toArray(resultsArray);
        return resultsArray;
    }
    
    @Override
    public String[] getCapitals(String capital_regex) throws RemoteException, SQLException
    {
        String states[]   = new String[50];
        String capitals[] = new String[50];

        parseUSStates(states, capitals);

        Connection conn = dbConnect(userName, passWord);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE " + tableName);

        String tableCreateQuery = "create table " + tableName 
                + "(state varchar2(15), capital varchar2(15))";
        if(!tableExist(tableName, stmt))
        {
            stmt.execute(tableCreateQuery);
            for(int i = 0; i < states.length; i++)
            {
                String insertIntoTable = "insert into " + tableName 
                        + " ( state, capital ) VALUES ( '" 
                        + states[i] +"' , '" + capitals[i] + "')";

                stmt.execute(insertIntoTable);
            }
        }           

        ArrayList<String> results = new ArrayList<>();
        ResultSet rset = stmt.executeQuery("SELECT capital FROM  " 
                + tableName + " WHERE REGEXP_LIKE (capital, '"
                + capital_regex + "', 'i')");


        while(rset.next())
        {
            results.add(rset.getString(1));
        }      

        rset.close();
        stmt.close();
        conn.close();

        String[] resultsArray = new String[results.size()];
        resultsArray = results.toArray(resultsArray);
        return resultsArray;        
    }

    public static Connection dbConnect(String user, String pass) throws SQLException
    {
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        String url = "jdbc:oracle:thin:@//dunes.ccsf.edu:1521/orcl.ccsf.edu"; 
        Connection conn = DriverManager.getConnection(url, user, pass);
        System.out.println("connected");
        return conn;
    }
    public static boolean tableExist(String table, Statement stmt) throws SQLException
    {
        int count = 0;      
        ResultSet rset = stmt.executeQuery("SELECT count(*) FROM  all_tables WHERE OWNER = '" 
                + userName + "' AND table_name = '"+ tableName + "'");
        while(rset.next())
        {
            count = rset.getInt(1);
        }
        if (count != 0)
        {
            return true;
        }
        return false;        
    }


    //****************************parseUSStates()*****************************
    private static void parseUSStates(String states[], String capitals[])
    {
        try
        {
            Scanner sc = new Scanner(new File(FILENAME));
            String line;
            int i = 0;

            sc.nextLine(); sc.nextLine(); 

            while(sc.hasNext())
            {
                line = sc.nextLine();
                String temp[] = line.split("\\s\\s+");
                if(temp.length >= 2)
                {
                    if(temp.length == 2) 
                    {
                        states[i]     = temp[0];
                        capitals[i++] = temp[1];
                    }
                    else
                    {
                        states[i]     = temp[0] + " " + temp[1];
                        capitals[i++] = temp[2];
                    }
                }
            }
        }catch(FileNotFoundException e){System.err.println(e);}
    }

    public static void main(String... args)
    {
        String states[]   = new String[50];
        String capitals[] = new String[50];

        parseUSStates(states, capitals);

        try
        {         
            Server serv = new Server();
            Naming.rebind("New_Server", serv);
            System.out.println("Server is ready!");
        } catch (Exception e) 
        { e.printStackTrace(); 
        }
    }
}
