package gr8pefish.openglider.common.network;

import gr8pefish.openglider.common.capabilities.OpenGliderCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketServerGliding implements IMessage {

    //the data sent
    private byte isGliding;

    public final static byte IS_GLIDING = 0;
    public final static byte IS_NOT_GLIDING = 1;

    public PacketServerGliding() {} //default constructor is necessary

    public PacketServerGliding(byte gliding) {
        this.isGliding = gliding;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        isGliding = (byte) ByteBufUtils.readVarShort(buf);
    }

    @Override
    public void toBytes(ByteBuf buf){
        ByteBufUtils.writeVarShort(buf, isGliding);
    }

    public static class Handler implements IMessageHandler<PacketServerGliding, IMessage> {

        @Override
        public IMessage onMessage(PacketServerGliding message, MessageContext ctx) {

            OpenGliderCapabilities.setIsGliding(ctx.getServerHandler().playerEntity, message.isGliding == IS_GLIDING);

            return null; //no return message
        }
    }
}

