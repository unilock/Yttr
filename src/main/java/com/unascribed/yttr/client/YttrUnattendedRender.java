package com.unascribed.yttr.client;

import java.io.File;

import org.lwjgl.glfw.GLFW;

import com.unascribed.rend.fabric.client.render.item.DefaultPngItemStackHandler;
import com.unascribed.rend.fabric.client.render.item.ItemStackRenderer;
import com.unascribed.rend.fabric.client.render.manager.RenderManager;
import com.unascribed.rend.render.item.ItemStackParameters;
import com.unascribed.rend.render.request.RenderingRequest;
import com.unascribed.yttr.content.item.SnareItem;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.init.YItemGroups;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.util.YLog;

import com.google.common.base.MoreObjects;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class YttrUnattendedRender extends IHasAClient {

	private static int totalRenders = 0;
	private static int finishedRenders = 0;
	private static int lastRenderPct = 0;
	
	public static void doBulkRender() {
		YLog.info("Performing unattended autorender... 0%");
		File dir = new File(System.getProperty("yttr.renderOutput", "yttr_autorender"));
		synchronized (YttrClient.class) {
			for (var i : Registry.ITEM) {
				var id = Registry.ITEM.getId(i);
				if (id.getNamespace().equals("yttr") || id.getNamespace().equals("minecraft")) {
					DefaultedList<ItemStack> li = DefaultedList.of();
					i.appendStacks(MoreObjects.firstNonNull(i.getGroup(), ItemGroup.SEARCH), li);
					if (i == YItems.SNARE) {
						i.appendStacks(YItemGroups.SNARE, li);
					}
					for (var is : li) {
						var hnd = new DefaultPngItemStackHandler(dir, 512, true, false, false) {
							@Override
							protected String getFilename(ItemStack value) {
								String fname = super.getFilename(value);
								if (value.getItem() instanceof LampBlockItem) {
									if (LampBlockItem.isInverted(value)) {
										fname += "_inverted";
									}
									fname += "_"+LampBlockItem.getColor(value).asString();
								} else if (value.getItem() instanceof SnareItem si) {
									var ty = si.getEntityType(value);
									if (ty != null) {
										fname += "_"+Registry.ENTITY_TYPE.getId(ty).toUnderscoreSeparatedString();
									}
								}
								return fname;
							}
						};
						RenderManager.push(new RenderingRequest<>(
								new ItemStackRenderer(),
								new ItemStackParameters(512),
								is,
								hnd,
								(t) -> {
									synchronized (YttrClient.class) {
										finishedRenders++;
										int pct = (finishedRenders*100)/totalRenders;
										if (pct != lastRenderPct) {
											YLog.info("Performing unattended autorender... {}%", pct);
											lastRenderPct = pct;
										}
										if (finishedRenders >= totalRenders) {
											GLFW.glfwSetWindowShouldClose(mc.getWindow().getHandle(), true);
										}
									}
								}
							));
						totalRenders++;
					}
				}
			}
			
		}
	}

}
