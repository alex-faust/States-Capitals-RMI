package servlet;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Client
{

    public static void main(String[] args)
    {
        try
        {
            String host = "127.0.0.1";
            Registry registry = LocateRegistry.getRegistry(host);
            ServerInterface si = (ServerInterface) registry.lookup("New_Server");

            String[] states = si.getCapitals("^B");
            if(states.length != 0)
            {
                for(String s : states)
                {
                    System.out.println(s);
                }
            } else System.out.println("No results found!");

        } catch (Exception e) {e.printStackTrace(); }
    }

}
