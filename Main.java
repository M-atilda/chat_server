import java.io.IOException;

import src.*;


public class Main
{
    public static void main(String[] args) throws IOException
    {
        ChatServer.data_manager = DataManager.dmFactory();
        new ChatServer();
    }
}
