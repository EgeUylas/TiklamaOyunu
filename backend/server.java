import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.java-websocket.server.WebSocketServer;
import org.java-websocket.WebSocket;

public class Server extends WebSocketServer {

    private final Map<WebSocket, Integer> players = new ConcurrentHashMap<>();
    private boolean gameStarted = false;

    public Server(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn) {
        if (players.size() < 2) {
            players.put(conn, 0);
            conn.send("{\"type\":\"info\", \"message\":\"Bağlandı!\"}");
        } else {
            conn.send("{\"type\":\"error\", \"message\":\"Oyun dolu!\"}");
            conn.close();
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (message.contains("\"type\":\"start\"") && players.size() == 2) {
            gameStarted = true;
            broadcast("{\"type\":\"start\"}");
        } else if (message.contains("\"type\":\"click\"") && gameStarted) {
            players.put(conn, players.get(conn) + 1);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        players.remove(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Sunucu başlatıldı!");
    }

    public void endGame() {
        gameStarted = false;
        WebSocket winner = Collections.max(players.entrySet(), Map.Entry.comparingByValue()).getKey();
        broadcast("{\"type\":\"result\", \"winner\":\"Player\", \"clicks\":" + players.get(winner) + "}");
        players.clear();
    }

    public static void main(String[] args) {
        Server server = new Server(new InetSocketAddress("localhost", 8080));
        server.start();
    }
}
