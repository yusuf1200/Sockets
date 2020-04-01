import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static String output = "";
    private static String eor = "[EOR]"; // a code for end-of-response
    private static String username = "Mixie";
    private static String password = "meow";
    
    // establishing a connection
    private static void setup() throws IOException {
        
        serverSocket = new ServerSocket(0);
        toConsole("Server port is " + serverSocket.getLocalPort());
        
        clientSocket = serverSocket.accept();

        // get the input stream and attach to a buffered reader
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        // get the output stream and attach to a printwriter
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        toConsole("Accepted connection from "
                 + clientSocket.getInetAddress() + " at port "
                 + clientSocket.getPort());
            
        sendGreeting();
    }
    
    // the initial message sent from server to client
    private static void sendGreeting()
    {
        appendOutput("Welcome to Catnet!\n");
        // appendOutput("Welcome to BetterServer!\n");
        // appendOutput("Submit text and I will convert it to upper case:");
        // sendOutput();
    }

    private static boolean checkUsername(String user) throws IOException {
        if(user.equals(username)){
            return true;
        }
        return false;
    }

    private static boolean checkPassword(String pass) throws IOException {
        if(pass.equals(password)){
            return true;
        }
        return false;
    }
    
    // what happens while client and server are connected
    private static void talk() throws IOException {
        /* placing echo functionality into a separate private method allows it to be easily swapped for a different behaviour */
        int counter = 0, ctr = 0;
        while(counter < 3){
            toConsole("Username requested");
            if(ctr == 0){
                appendOutput("Enter Username:");
                sendOutput();
                ctr++;
            }
            String user = in.readLine();
            toConsole("Name entered: " + user);
            if(checkUsername(user)){
                toConsole("Password requested");
                appendOutput("Enter Password:");
                sendOutput();
                String pass = in.readLine();
                toConsole("Password entered: " + pass);
                if(checkPassword(pass)){
                    toConsole("Username and password verified");
                    appendOutput("Welcome Mixie\n");
                    appendOutput("Type a message and i will echo it:");
                    sendOutput();
                    echoClient();
                    break;
                }
                else{
                    toConsole("Password not recognised");
                    appendOutput("Password not recognised\n");
                    appendOutput("Enter Username:");
                    sendOutput();
                }
            }
            else{
                toConsole("Username not recognised");
                appendOutput("Username not recognised\n");
                appendOutput("Enter Username:");
                sendOutput();
            }
            counter++;
        }
        disconnect();
    }
    
    // repeatedly take input from client and send back in upper case
    private static void echoClient() throws IOException
    {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            appendOutput(inputLine.toUpperCase());
            sendOutput();
            toConsole(inputLine);
        }
    }
    
    private static void disconnect() throws IOException {
        out.close();
        toConsole("Disconnected.");
        System.exit(0);
    }
    
    // add a line to the next message to be sent to the client
    private static void appendOutput(String line) {
        output += line + "\r";
    }
    
    // send next message to client
    private static void sendOutput() {
        out.println( output + "[EOR]");
        out.flush();
        output = "";
    }
    
    // because it makes life easier!
    private static void toConsole(String message) {
        System.out.println(message);
    }
    
    public static void main(String[] args) {
        try {
            setup();
            talk();
        }
        catch( IOException ioex ) {
            toConsole("Error: " + ioex );
        }
    }
}
