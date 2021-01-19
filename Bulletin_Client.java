
// Code dependencies
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class Bulletin_Client {
    private static Socket socket;
    private static BufferedReader reader;
    private static PrintWriter writer;
    private static final Scanner in = new Scanner(System.in);
    private static final int PORT = 16000;

    // Reads in data from the server
    private static String fetchData() {
        char[] cArr = new char[250];
        try {
            reader.read(cArr);
        }
        // Edge case handling: in case server closes abruptly
        catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Terminating program...");
            System.exit(1);
        }
        // Removes unnecessary leading & trailing whitespace
        return (new String(cArr)).trim();
    }

    public static void main(String[] args) {
        initialize();
    }

    // Checks and establishes socket connection
    private static void initialize() {
        // Gets client's IP address
        System.out.println("Input the IP address:");
        String IP = in.nextLine();

        // Validates socket connection
        try {
            socket = new Socket(IP, PORT);
            System.out.printf("IP Address: %s\tPort Number: %d\n", IP, PORT);
            System.out.println("Connect status: success");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), false);

            exchangeMsgs();
            quit();

        } catch (NullPointerException | IOException e) {
            System.out.println("Connect status: fail");
        }
    }

    // Main crux (client-server communication)
    private static void exchangeMsgs() {
        String userInput;
        while (true) {
            // Stores client's command
            userInput = in.nextLine();
            System.out.println("client: " + userInput);
            // Case-1: client wants to exit
            if (userInput.contentEquals("QUIT")) {
                writer.write(userInput + "\n");
                writer.flush();
                System.out.print("server: " + fetchData());
                break; // Ends the while loop
            }
            // Case-2: client wants to send messages to server
            else if (userInput.contentEquals("POST")) {
                // Stores all the messages in a string buffer
                StringBuilder texts = new StringBuilder();
                texts.append(userInput).append("\n");
                String text;
                do {
                    text = in.nextLine();
                    System.out.println("client: " + text);
                    texts.append(text).append("\n");
                } while (!text.contentEquals("."));

                // Passes the messages altogether to the client as per the task requirements
                writer.print(texts);
                writer.flush();
                System.out.println("server: " + fetchData());
            }
            // Case-3: client wants to retrieve messages from server
            else if (userInput.contentEquals("READ")) {
                writer.write(userInput + "\n");
                writer.flush();
                String msg;
                do {
                    msg = fetchData();
                    System.out.println(msg);
                } while (!msg.contentEquals("."));

            }
            // Case-4: client enters invalid input
            else {
                writer.write(userInput + "\n");
                writer.flush();
                System.out.println(fetchData());
            }
        }
    }

    // Closes all previously established connections
    private static void quit() {
        try {
            socket.close();
            in.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}