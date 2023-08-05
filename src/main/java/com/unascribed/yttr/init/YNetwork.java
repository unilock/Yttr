package com.unascribed.yttr.init;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.network.MessageC2SAttack;
import com.unascribed.yttr.network.MessageC2SCreativeBlink;
import com.unascribed.yttr.network.MessageC2SCreativeNoClip;
import com.unascribed.yttr.network.MessageC2SDivePos;
import com.unascribed.yttr.network.MessageC2SDiveTo;
import com.unascribed.yttr.network.MessageC2SOscillatorShift;
import com.unascribed.yttr.network.MessageC2SRifleMode;
import com.unascribed.yttr.network.MessageC2SShifterMode;
import com.unascribed.yttr.network.MessageC2STrustedRifleFire;
import com.unascribed.yttr.network.MessageS2CAnimateFastDive;
import com.unascribed.yttr.network.MessageS2CBeam;
import com.unascribed.yttr.network.MessageS2CDiscoveredGeyser;
import com.unascribed.yttr.network.MessageS2CDive;
import com.unascribed.yttr.network.MessageS2CDiveEnd;
import com.unascribed.yttr.network.MessageS2CDiveError;
import com.unascribed.yttr.network.MessageS2CDivePos;
import com.unascribed.yttr.network.MessageS2CDivePressure;
import com.unascribed.yttr.network.MessageS2CEffectorHole;
import com.unascribed.yttr.network.MessageS2CScreeperBreak;
import com.unascribed.yttr.network.MessageS2CSoulImpurity;
import com.unascribed.yttr.network.MessageS2CVoidBall;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class YNetwork {

	public static final NetworkContext CONTEXT = NetworkContext.forChannel(Yttr.id("main"));
	
	public static void init() {
		CONTEXT.register(MessageC2SAttack.class);
		CONTEXT.register(MessageC2SDivePos.class);
		CONTEXT.register(MessageC2SDiveTo.class);
		CONTEXT.register(MessageS2CAnimateFastDive.class);
		CONTEXT.register(MessageS2CBeam.class);
		CONTEXT.register(MessageS2CDiscoveredGeyser.class);
		CONTEXT.register(MessageS2CDive.class);
		CONTEXT.register(MessageS2CDiveEnd.class);
		CONTEXT.register(MessageS2CDiveError.class);
		CONTEXT.register(MessageS2CDivePos.class);
		CONTEXT.register(MessageS2CDivePressure.class);
		CONTEXT.register(MessageS2CEffectorHole.class);
		CONTEXT.register(MessageS2CVoidBall.class);
		CONTEXT.register(MessageC2SRifleMode.class);
		CONTEXT.register(MessageC2SShifterMode.class);
		CONTEXT.register(MessageC2SOscillatorShift.class);
		CONTEXT.register(MessageC2STrustedRifleFire.class);
		CONTEXT.register(MessageS2CScreeperBreak.class);
		CONTEXT.register(MessageS2CSoulImpurity.class);
		CONTEXT.register(MessageC2SCreativeBlink.class);
		CONTEXT.register(MessageC2SCreativeNoClip.class);
	}
	
	public static void sendPacketToPlayersWatching(World world, BlockPos pos, Packet<?> packet) {
		if (world instanceof ServerWorld sw) {
			sw.getChunkManager().delegate
				.getPlayersWatchingChunk(new ChunkPos(pos), false)
				.forEach(spe -> spe.networkHandler.sendPacket(packet));
		}
	}
	
}
