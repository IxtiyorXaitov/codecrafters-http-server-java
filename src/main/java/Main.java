import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            clientSocket = serverSocket.accept();

            InputStream inputStream = clientSocket.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            inputStream, StandardCharsets.UTF_8
                    )
            );
            String readLine = bufferedReader.readLine();
            String[] strings = readLine.split(" ");

            String method = strings[0];
            String path = strings[1];
            String httpVersion = strings[2];

            System.out.println("method -> " + method);
            System.out.println("path -> " + path);
            System.out.println("httpVersion -> " + httpVersion);

            String response = getResponse(path);

            clientSocket
                    .getOutputStream()
                    .write(response.getBytes(StandardCharsets.UTF_8));

            clientSocket.close();
            System.out.println("accepted new connection");

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static String getResponse(String path) {
        String response;
        if (path.startsWith("/echo")) {
            String substring = path.substring(6);
            response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + substring.length() + "\r\n\r\n" + substring + "\n";
        } else {
            if (Objects.equals(path, "/")) {
                response = "HTTP/1.1 200 OK\r\n\r\n";
            } else {
                response = "HTTP/1.1 404 Not Found\r\n\r\n";
            }
        }
        return response;
    }
}
