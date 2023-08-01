package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.S2CMessage;
import com.unascribed.lib39.tunnel.api.annotation.field.MarshalledAs;
import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.init.YNetwork;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class MessageS2CBeam extends S2CMessage {

	@MarshalledAs("i32")
	public int entityId;
	@MarshalledAs("i32")
	public int color;
	@MarshalledAs("f32")
	public float endX;
	@MarshalledAs("f32")
	public float endY;
	@MarshalledAs("f32")
	public float endZ;
	
	public MessageS2CBeam(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CBeam(int entityId, int color, float endX, float endY, float endZ) {
		super(YNetwork.CONTEXT);
		this.entityId = entityId;
		this.color = color;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		float a = ((color>>24)&0xFF)/255f;
		float r = ((color>>16)&0xFF)/255f;
		float g = ((color>> 8)&0xFF)/255f;
		float b = ((color>> 0)&0xFF)/255f;
		Entity ent = player.getWorld().getEntityById(entityId);
		if (ent == null) return;
		boolean fp = ent == player && mc.options.getPerspective() == Perspective.FIRST_PERSON;
		Vec3d start = RifleItem.getMuzzlePos(ent, fp);
		double len = MathHelper.sqrt((float) start.squaredDistanceTo(endX, endY, endZ));
		double diffX = endX-start.x;
		double diffY = endY-start.y;
		double diffZ = endZ-start.z;
		int count = (int)(len*14);
		DustParticleEffect eff = new DustParticleEffect(new Vector3f(r, g, b), 0.2f);
		SpriteProvider sprites = ((ParticleManagerAccessor)mc.particleManager).getSpriteAwareFactories().get(Registries.PARTICLE_TYPE.getKey(ParticleTypes.DUST).get().getValue());
		for (int i = 0; i < count; i++) {
			double t = (i/(double)count);
			double x = start.x+(diffX*t);
			double y = start.y+(diffY*t);
			double z = start.z+(diffZ*t);
			final int fi = i;
			mc.particleManager.addParticle(new RedDustParticle(mc.world, x, y, z, 0, 0, 0, eff, sprites) {
				{
					if (fp && fi < 3) {
						scale /= 2;
					}
					setMaxAge((int)(Math.log10((fi*4)+5))+10);
					setColor(r, g, b);
					setColorAlpha(a);
					velocityX = 0;
					velocityY = 0;
					velocityZ = 0;
				}
				
				@Override
				protected int getBrightness(float tint) {
					return LightmapTextureManager.pack(15, 15);
				}

				@Override
				public ParticleTextureSheet getType() {
					return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
				}
				
			});
		}
	}

}
