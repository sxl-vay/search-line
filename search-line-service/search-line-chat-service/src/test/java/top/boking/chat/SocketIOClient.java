package top.boking.chat;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import top.boking.chat.domain.dto.MessageDataDTO;
import top.boking.chat.domain.enums.MessageProperties;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketIOClient {
    private static final AtomicInteger printerFlag = new AtomicInteger(0);
    private Socket socket;
    private String roomId;

    public SocketIOClient() {
        // 生成随机房间ID

        Scanner roomsc = new Scanner(System.in);
        System.out.print("请输入房间ID: ");
        String intput = roomsc.nextLine();
        String[] split = intput.split(" ");
        roomId = split[0];
        String user = split[1];

        try {
            // 连接到服务器
            IO.Options options = getOptions();
            socket = IO.socket("http://localhost:30000", options);
            // 设置事件监听器
            setupEventListeners();
            // 连接到服务器
            new Thread(() -> {
                socket.connect();
            }).start();
            Thread.sleep(1000);

            //接收用户输入然后通过socket发送到服务端
            Scanner scanner = new Scanner(System.in);
            System.out.print("我的: ");
            while (scanner.hasNextLine()) {
                MessageDataDTO<String> messageDataDTO = getStringMessageDataDTO(scanner, user);
                socket.emit("vr-message-sent", messageDataDTO/*, (Object... ack) -> {
                }*/);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static IO.Options getOptions() {
        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.reconnection = true;
        options.reconnectionAttempts = 10;
        options.timeout = 10000;
        options.transports = new String[]{"websocket", "polling"};  // 添加这行
        return options;
    }

    public static void main(String[] args) {
        SocketIOClient client = new SocketIOClient();

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(client::disconnect));
    }

    private MessageDataDTO<String> getStringMessageDataDTO(Scanner scanner, String user) {
        String message = scanner.nextLine();
        Map<String, String> propertiesStringMap = Map.of(MessageProperties.SENDER.name(), user);
        MessageDataDTO<String> messageDataDTO = MessageDataDTO.<String>builder().message(message).roomId(roomId).properties(propertiesStringMap).build();
        return messageDataDTO;
    }

    private void setupEventListeners() {
        // 连接成功事件
        socket.on(Socket.EVENT_CONNECT, args -> {
            System.out.println("已连接到服务器！socket.id: " + socket.id());
            // 加入房间
            socket.emit("join-room", new String[]{roomId}, (Object... ack) -> {
                if (ack.length > 0) {
                    System.out.println("成功加入房间: " + roomId);
                    System.out.println("我的ID: " + ack[0]);
                    System.out.println("当前房间状态: " + ack[1]);

                    // 开始定期发送位置更新
                }
            });
        });
        startSendingUpdates();

        // 新用户加入事件
        socket.on("user-joined", args -> {
            String userId = (String) args[0];
            System.out.println("新用户加入: " + userId);
        });

        // 用户离开事件
        socket.on("user-leave", args -> {
            String userId = (String) args[0];
            System.out.println("用户离开: " + userId);
        });

        // 房间状态更新事件
        socket.on("room-state", args -> {
            JSONObject snapshot = (JSONObject) args[0];
            System.out.println("房间状态更新: " + roomId + ": " + snapshot.toString());
        });
        socket.on("vr-message", args -> {
            Object arg = args[0];
            String jsonStr = arg.toString();
            MessageDataDTO<String> messageDataDTO = com.alibaba.fastjson2.JSONObject.parseObject(jsonStr, MessageDataDTO.class);
            String sender = messageDataDTO.getProperties().get(MessageProperties.SENDER.name());
            System.out.println(sender + "：" + messageDataDTO.getMessage());
            System.out.println("\n");
        });

        // 连接错误事件
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            System.out.println("连接错误: " + args[0]);
        });

        // 断开连接事件
        socket.on(Socket.EVENT_DISCONNECT, args -> {
            System.out.println("断开连接: " + args[0]);
        });
    }

    private void startSendingUpdates() {
        // 创建一个线程定期发送位置更新
        new Thread(() -> {
            Random random = new Random();
            while (!Thread.interrupted()) {
                try {
                    JSONObject state = new JSONObject();
                    state.put("x", random.nextDouble() * 100);
                    state.put("y", random.nextDouble() * 100);
                    state.put("z", random.nextDouble() * 100);

                    socket.emit("update-state", state);

                    Thread.sleep(1000); // 每秒更新一次
                } catch (InterruptedException | JSONException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

    }

    private void disconnect() {
        System.out.println("socket = " + socket);
        if (socket != null) {
            socket.disconnect();
        }
    }
}