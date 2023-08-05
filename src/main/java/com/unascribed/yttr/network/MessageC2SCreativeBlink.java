package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.C2SMessage;
import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.annotation.field.MarshalledAs;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YNetwork;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class MessageC2SCreativeBlink extends C2SMessage {

	@MarshalledAs("f64")
	public double x;
	@MarshalledAs("f64")
	public double y;
	@MarshalledAs("f64")
	public double z;
	
	public MessageC2SCreativeBlink(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SCreativeBlink(double x, double y, double z) {
		super(YNetwork.CONTEXT);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		if (Yttr.isEnlightened(player, false)) {
			ServerWorld world = (ServerWorld) player.getWorld();
			Vec3d vel = player.getVelocity();
			Box box = player.getBoundingBox();
			world.spawnParticles(ParticleTypes.WITCH, box.getCenter().x, box.getCenter().y, box.getCenter().z,
					40, box.getXLength(), box.getYLength(), box.getZLength(), 0);
			world.spawnParticles(ParticleTypes.POOF, box.getCenter().x, box.getCenter().y, box.getCenter().z,
					30, box.getXLength() / 2, box.getYLength() / 2, box.getZLength() / 2, 0);
			player.teleport(world, x, y, z, player.getYaw(), player.getPitch());
			player.setVelocity(vel);
			player.velocityModified = true;
			world.spawnParticles(ParticleTypes.WITCH, x, y + (player.getHeight() / 2), z,
					20, box.getXLength(), box.getYLength(), box.getZLength(), 0);
			world.spawnParticles(ParticleTypes.POOF, x, y + (player.getHeight() / 2), z,
					10, box.getXLength() / 2, box.getYLength() / 2, box.getZLength() / 2, 0);
			player.getWorld().playSound(null, player.getPos().x, player.getPos().y, player.getPos().z, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, player.getSoundCategory(), 1, 1);
		}
	}

}
