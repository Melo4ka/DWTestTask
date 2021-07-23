package ru.meldren.dwtesttask;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import ru.meldren.dwtesttask.utils.PersonalHolo;

import java.util.List;
import java.util.Optional;

/**
 * Created by Meldren on 22/06/2021
 */
public class Listeners {

    public Listeners() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(Manager.getInstance(), ListenerPriority.NORMAL,
                        PacketType.Play.Server.ENTITY_METADATA) {
                    @Override
                    public void onPacketSending(PacketEvent e) {
                        WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(e.getPacket());

                        if (!PersonalHolo.getHolograms().containsKey(wrapper.getEntityID()))
                            return;

                        List<WrappedWatchableObject> metadata = wrapper.getMetadata();

                        WrappedDataWatcher.WrappedDataWatcherObject o =
                                new WrappedDataWatcher.WrappedDataWatcherObject(
                                        2,  WrappedDataWatcher.Registry.getChatComponentSerializer(true));
                        metadata.add(new WrappedWatchableObject(o,
                                Optional.of(WrappedChatComponent.fromText(
                                        PersonalHolo.getHolograms().get(
                                                wrapper.getEntityID()).getText(e.getPlayer())).getHandle())));

                        wrapper.setMetadata(metadata);
                    }
                });
    }

}
