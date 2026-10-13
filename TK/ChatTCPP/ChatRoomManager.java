import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomManager {
    // Lưu trữ phòng: Tên phòng -> Danh sách các client trong phòng đó
    private Map<String, List<ClientInfo>> rooms;

    public ChatRoomManager() {
        rooms = new HashMap<>();
        rooms.put("Study", new ArrayList<>());
        rooms.put("Game", new ArrayList<>());
        rooms.put("TraiNghiem", new ArrayList<>());
    }

    // Lấy danh sách tên phòng phân cách bằng dấu phẩy
    public synchronized String getRoomListString() {
        return String.join(",", rooms.keySet());
    }

    public synchronized boolean roomExists(String roomName) {
        return rooms.containsKey(roomName);
    }

    public synchronized void addClient(String roomName, PrintWriter writer, String clientName) {
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).add(new ClientInfo(writer, clientName));
        }
    }

    public synchronized void removeClient(String roomName, PrintWriter writer, String clientName) {
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).removeIf(c -> c.writer == writer);
        }
    }

    // Gửi tin nhắn tới mọi người trong phòng (trừ người gửi nếu cần)
    public synchronized void broadcast(String roomName, String message, PrintWriter senderWriter) {
        if (rooms.containsKey(roomName)) {
            for (ClientInfo client : rooms.get(roomName)) {
                if (client.writer != senderWriter) {
                    client.writer.println(message);
                }
            }
        }
    }

    // Lớp phụ để lưu thông tin client trong phòng
    private static class ClientInfo {
        PrintWriter writer;
        String name;

        public ClientInfo(PrintWriter writer, String name) {
            this.writer = writer;
            this.name = name;
        }
    }
}
