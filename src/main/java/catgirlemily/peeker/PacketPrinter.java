package catgirlemily.peeker;

import net.minecraft.network.packet.Packet;
import net.minecraft.util.Formatting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class PacketPrinter {

    private static String currentPacket = null;
    private static boolean showTypes = false;

    public static String getCurrentPacket() {
        return currentPacket;
    }

    public static void setCurrentPacket(String packetName) {
        currentPacket = packetName;
    }

    public static void toggleShowTypes() {
        showTypes = !showTypes;
    }

    public static boolean isShowTypes() {
        return showTypes;
    }

    public static String formatPacket(Packet<?> packet) {
        if (currentPacket == null || !packet.getClass().getSimpleName().equals(currentPacket)) return "";

        StringBuilder sb = new StringBuilder();
        sb.append(Formatting.YELLOW).append("{").append("\n");

        try {
            Field[] fields = packet.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(packet);

                sb.append(Formatting.AQUA).append(field.getName())
                  .append(Formatting.GRAY).append(": ")
                  .append(formatValue(value))
                  .append(Formatting.GRAY).append(",\n");
            }
            if (fields.length > 0) sb.setLength(sb.length() - 2);
        } catch (Exception e) {
            sb.append(Formatting.RED).append("error reading fields");
        }

        sb.append("\n").append(Formatting.YELLOW).append("}");
        return sb.toString();
    }

    private static String formatValue(Object value) {
        if (value == null) return Formatting.GRAY + "null";

        if (value instanceof Boolean) {
            return Formatting.RED + value.toString();
        } else if (value instanceof Number) {
            return Formatting.GOLD + value.toString();
        } else if (value instanceof String) {
            return Formatting.GREEN + "\"" + value + "\"";
        } else if (value instanceof Collection<?> collection) {
            StringBuilder sb = new StringBuilder(Formatting.GREEN + "[");
            for (Object item : collection) {
                sb.append(formatValue(item)).append(Formatting.GRAY).append(", ");
            }
            if (!collection.isEmpty()) sb.setLength(sb.length() - 2);
            sb.append(Formatting.GREEN).append("]");
            return sb.toString();
        } else if (value.getClass().isArray()) {
            int len = Array.getLength(value);
            StringBuilder sb = new StringBuilder(Formatting.GREEN + "[");
            for (int i = 0; i < len; i++) {
                sb.append(formatValue(Array.get(value, i))).append(Formatting.GRAY).append(", ");
            }
            if (len > 0) sb.setLength(sb.length() - 2);
            sb.append(Formatting.GREEN).append("]");
            return sb.toString();
        } else if (value instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder(Formatting.LIGHT_PURPLE + "{");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                sb.append(formatValue(entry.getKey()))
                  .append(Formatting.GRAY).append(": ")
                  .append(formatValue(entry.getValue()))
                  .append(Formatting.GRAY).append(", ");
            }
            if (!map.isEmpty()) sb.setLength(sb.length() - 2);
            sb.append(Formatting.LIGHT_PURPLE).append("}");
            return sb.toString();
        } else {
            String result = value.toString();
            if (showTypes) result += Formatting.GRAY + " (" + value.getClass().getSimpleName() + ")";
            return Formatting.LIGHT_PURPLE + result;
        }
    }

    public static void printPacket(Packet<?> packet) {
        String formatted = formatPacket(packet);
        if (!formatted.isEmpty()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(formatted), false);
            }
        }
    }
}
