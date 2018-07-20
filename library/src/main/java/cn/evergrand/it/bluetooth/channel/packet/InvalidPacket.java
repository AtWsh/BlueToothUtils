package cn.evergrand.it.bluetooth.channel.packet;


public class InvalidPacket extends Packet {

	@Override
	public String toString() {
		return "InvalidPacket{}";
	}

	@Override
	public String getName() {
		return "invalid";
	}

	@Override
	public byte[] toBytes() {
		return new byte[0];
	}
}
