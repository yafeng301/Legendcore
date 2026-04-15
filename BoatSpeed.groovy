package groovy

import cn.mcdcs.legendcore.api.dragoncore.PacketProcessor
import org.bukkit.entity.Boat

class BoatSpeed extends PacketProcessor {

    static def processor = new BoatSpeed()

    static void onGroovyRegister() {
        processor.register()
    }

    static void onGroovyUnregister() {
        processor.unregister()
    }

    @Override
    void execute(String[] args, boolean isALL) {
        def vehicle = player.vehicle
        if (vehicle instanceof Boat) {
            def velocity = vehicle.velocity
            sendPacket("speed", Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y) * 20 + "")
            return
        }
        sendPacket("speed", "-1")
    }

    @Override
    String getCustomIdentifier() {
        return "BoatSpeed"
    }
}
