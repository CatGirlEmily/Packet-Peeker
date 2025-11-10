package catgirlemily.peeker;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Packetpeeker implements ModInitializer {
    public static final String MOD_ID = "packetpeeker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PacketCommands.registerCommands();
        LOGGER.info("gratuluje gra sie nie wyjebala");
    }
}
