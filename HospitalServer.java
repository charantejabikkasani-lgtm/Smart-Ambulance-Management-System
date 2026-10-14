
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HospitalServer {

    public static void main(String[] args) {

        int port = 5000;

        System.out.println("======================================");
        System.out.println("       HOSPITAL EMERGENCY DASHBOARD");
        System.out.println("======================================");
        System.out.println("Server status: RUNNING");
        System.out.println("Waiting for ambulance alerts...");
        System.out.println();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                try (
                    Socket socket = serverSocket.accept();
                    BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                    )
                ) {
                    String message = input.readLine();

                    if (message != null && !message.isBlank()) {
                        displayAlert(message);
                    }

                } catch (Exception e) {
                    System.out.println(
                        "Connection error: " + e.getMessage()
                    );
                }
            }

        } catch (Exception e) {
            System.out.println(
                "Could not start server: " + e.getMessage()
            );
            System.out.println(
                "Check whether another server is using port " + port + "."
            );
        }
    }

    static void displayAlert(String message) {

        String time = LocalTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss")
        );

        System.out.println();
        System.out.println("+--------------------------------------+");
        System.out.println("|         INCOMING AMBULANCE ALERT     |");
        System.out.println("+--------------------------------------+");
        System.out.println("Received at : " + time);

        String[] fields = message.split("\\s*\\|\\s*");

        for (String field : fields) {
            System.out.println(field);
        }

        System.out.println("+--------------------------------------+");
        System.out.println("Alert received successfully.");
        System.out.println();
    }
}
