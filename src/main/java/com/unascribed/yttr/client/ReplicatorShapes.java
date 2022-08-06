package com.unascribed.yttr.client;

import static com.unascribed.yttr.client.RenderBridge.*;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class ReplicatorShapes {

	public static final int OCTAHEDRON = build(ReplicatorShapes::octahedron);
	public static final int DODECAHEDRON = build(ReplicatorShapes::dodecahedron);
	public static final int ICOSAHEDRON = build(ReplicatorShapes::icosahedron);
	public static final ImmutableList<Integer> ALL = ImmutableList.of(OCTAHEDRON, DODECAHEDRON, ICOSAHEDRON);

	public static int build(Runnable r) {
		if (!canUseCompatFunctions()) return 0;
		int list = glGenLists(1);
		glNewList(list, GL_COMPILE);
		glBegin(GL_TRIANGLES);
		r.run();
		glEnd();
		glEndList();
		return list;
	}
	
	public static void octahedron() {
		glNormal3f(0.577350f, 0.577350f, 0.577350f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(0.577350f, 0.577350f, 0.577350f); glVertex3f(1.000000f, 0.000000f, 0.000000f);
		glNormal3f(0.577350f, 0.577350f, 0.577350f); glVertex3f(0.000000f, 1.000000f, 0.000000f);
		glNormal3f(-0.577350f, 0.577350f, 0.577350f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(-0.577350f, 0.577350f, 0.577350f); glVertex3f(0.000000f, 1.000000f, 0.000000f);
		glNormal3f(-0.577350f, 0.577350f, 0.577350f); glVertex3f(-1.000000f, 0.000000f, 0.000000f);
		glNormal3f(-0.577350f, -0.577350f, 0.577350f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(-0.577350f, -0.577350f, 0.577350f); glVertex3f(-1.000000f, 0.000000f, 0.000000f);
		glNormal3f(-0.577350f, -0.577350f, 0.577350f); glVertex3f(0.000000f, -1.000000f, 0.000000f);
		glNormal3f(0.577350f, -0.577350f, 0.577350f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(0.577350f, -0.577350f, 0.577350f); glVertex3f(0.000000f, -1.000000f, 0.000000f);
		glNormal3f(0.577350f, -0.577350f, 0.577350f); glVertex3f(1.000000f, 0.000000f, 0.000000f);
		glNormal3f(0.577350f, 0.577350f, -0.577350f); glVertex3f(0.000000f, 1.000000f, 0.000000f);
		glNormal3f(0.577350f, 0.577350f, -0.577350f); glVertex3f(1.000000f, 0.000000f, 0.000000f);
		glNormal3f(0.577350f, 0.577350f, -0.577350f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
		glNormal3f(-0.577350f, 0.577350f, -0.577350f); glVertex3f(-1.000000f, 0.000000f, 0.000000f);
		glNormal3f(-0.577350f, 0.577350f, -0.577350f); glVertex3f(0.000000f, 1.000000f, 0.000000f);
		glNormal3f(-0.577350f, 0.577350f, -0.577350f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
		glNormal3f(-0.577350f, -0.577350f, -0.577350f); glVertex3f(0.000000f, -1.000000f, 0.000000f);
		glNormal3f(-0.577350f, -0.577350f, -0.577350f); glVertex3f(-1.000000f, 0.000000f, 0.000000f);
		glNormal3f(-0.577350f, -0.577350f, -0.577350f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
		glNormal3f(0.577350f, -0.577350f, -0.577350f); glVertex3f(1.000000f, 0.000000f, 0.000000f);
		glNormal3f(0.577350f, -0.577350f, -0.577350f); glVertex3f(0.000000f, -1.000000f, 0.000000f);
		glNormal3f(0.577350f, -0.577350f, -0.577350f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
	}
	
	public static void dodecahedron() {
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(0.607000f, 0.000000f, 0.795000f);
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(0.188000f, 0.577000f, 0.795000f);
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(-0.491000f, 0.357000f, 0.795000f);
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(0.607000f, 0.000000f, 0.795000f);
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(-0.491000f, 0.357000f, 0.795000f);
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(-0.491000f, -0.357000f, 0.795000f);
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(0.607000f, 0.000000f, 0.795000f);
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(-0.491000f, -0.357000f, 0.795000f);
		glNormal3f(0.000000f, 0.000000f, 1.000000f); glVertex3f(0.188000f, -0.577000f, 0.795000f);
		glNormal3f(0.724111f, 0.525640f, 0.446504f); glVertex3f(0.982000f, 0.000000f, 0.188000f);
		glNormal3f(0.724111f, 0.525640f, 0.446504f); glVertex3f(0.795000f, 0.577000f, -0.188000f);
		glNormal3f(0.724111f, 0.525640f, 0.446504f); glVertex3f(0.304000f, 0.934000f, 0.188000f);
		glNormal3f(0.723780f, 0.525399f, 0.447324f); glVertex3f(0.982000f, 0.000000f, 0.188000f);
		glNormal3f(0.723780f, 0.525399f, 0.447324f); glVertex3f(0.304000f, 0.934000f, 0.188000f);
		glNormal3f(0.723780f, 0.525399f, 0.447324f); glVertex3f(0.188000f, 0.577000f, 0.795000f);
		glNormal3f(0.723766f, 0.525577f, 0.447137f); glVertex3f(0.982000f, 0.000000f, 0.188000f);
		glNormal3f(0.723766f, 0.525577f, 0.447137f); glVertex3f(0.188000f, 0.577000f, 0.795000f);
		glNormal3f(0.723766f, 0.525577f, 0.447137f); glVertex3f(0.607000f, 0.000000f, 0.795000f);
		glNormal3f(-0.276379f, 0.850814f, 0.446911f); glVertex3f(0.304000f, 0.934000f, 0.188000f);
		glNormal3f(-0.276379f, 0.850814f, 0.446911f); glVertex3f(-0.304000f, 0.934000f, -0.188000f);
		glNormal3f(-0.276379f, 0.850814f, 0.446911f); glVertex3f(-0.795000f, 0.577000f, 0.188000f);
		glNormal3f(-0.276395f, 0.850862f, 0.446810f); glVertex3f(0.304000f, 0.934000f, 0.188000f);
		glNormal3f(-0.276395f, 0.850862f, 0.446810f); glVertex3f(-0.795000f, 0.577000f, 0.188000f);
		glNormal3f(-0.276395f, 0.850862f, 0.446810f); glVertex3f(-0.491000f, 0.357000f, 0.795000f);
		glNormal3f(-0.275624f, 0.850675f, 0.447642f); glVertex3f(0.304000f, 0.934000f, 0.188000f);
		glNormal3f(-0.275624f, 0.850675f, 0.447642f); glVertex3f(-0.491000f, 0.357000f, 0.795000f);
		glNormal3f(-0.275624f, 0.850675f, 0.447642f); glVertex3f(0.188000f, 0.577000f, 0.795000f);
		glNormal3f(-0.895378f, 0.000000f, 0.445307f); glVertex3f(-0.795000f, 0.577000f, 0.188000f);
		glNormal3f(-0.895378f, 0.000000f, 0.445307f); glVertex3f(-0.982000f, 0.000000f, -0.188000f);
		glNormal3f(-0.895378f, 0.000000f, 0.445307f); glVertex3f(-0.795000f, -0.577000f, 0.188000f);
		glNormal3f(-0.894132f, 0.000000f, 0.447803f); glVertex3f(-0.795000f, 0.577000f, 0.188000f);
		glNormal3f(-0.894132f, 0.000000f, 0.447803f); glVertex3f(-0.795000f, -0.577000f, 0.188000f);
		glNormal3f(-0.894132f, 0.000000f, 0.447803f); glVertex3f(-0.491000f, -0.357000f, 0.795000f);
		glNormal3f(-0.894132f, 0.000000f, 0.447803f); glVertex3f(-0.795000f, 0.577000f, 0.188000f);
		glNormal3f(-0.894132f, 0.000000f, 0.447803f); glVertex3f(-0.491000f, -0.357000f, 0.795000f);
		glNormal3f(-0.894132f, 0.000000f, 0.447803f); glVertex3f(-0.491000f, 0.357000f, 0.795000f);
		glNormal3f(-0.276379f, -0.850814f, 0.446911f); glVertex3f(-0.795000f, -0.577000f, 0.188000f);
		glNormal3f(-0.276379f, -0.850814f, 0.446911f); glVertex3f(-0.304000f, -0.934000f, -0.188000f);
		glNormal3f(-0.276379f, -0.850814f, 0.446911f); glVertex3f(0.304000f, -0.934000f, 0.188000f);
		glNormal3f(-0.276297f, -0.850560f, 0.447446f); glVertex3f(-0.795000f, -0.577000f, 0.188000f);
		glNormal3f(-0.276297f, -0.850560f, 0.447446f); glVertex3f(0.304000f, -0.934000f, 0.188000f);
		glNormal3f(-0.276297f, -0.850560f, 0.447446f); glVertex3f(0.188000f, -0.577000f, 0.795000f);
		glNormal3f(-0.275782f, -0.851164f, 0.446613f); glVertex3f(-0.795000f, -0.577000f, 0.188000f);
		glNormal3f(-0.275782f, -0.851164f, 0.446613f); glVertex3f(0.188000f, -0.577000f, 0.795000f);
		glNormal3f(-0.275782f, -0.851164f, 0.446613f); glVertex3f(-0.491000f, -0.357000f, 0.795000f);
		glNormal3f(0.724111f, -0.525640f, 0.446504f); glVertex3f(0.304000f, -0.934000f, 0.188000f);
		glNormal3f(0.724111f, -0.525640f, 0.446504f); glVertex3f(0.795000f, -0.577000f, -0.188000f);
		glNormal3f(0.724111f, -0.525640f, 0.446504f); glVertex3f(0.982000f, 0.000000f, 0.188000f);
		glNormal3f(0.723838f, -0.525441f, 0.447181f); glVertex3f(0.304000f, -0.934000f, 0.188000f);
		glNormal3f(0.723837f, -0.525441f, 0.447181f); glVertex3f(0.982000f, 0.000000f, 0.188000f);
		glNormal3f(0.723837f, -0.525441f, 0.447181f); glVertex3f(0.607000f, 0.000000f, 0.795000f);
		glNormal3f(0.723672f, -0.525509f, 0.447369f); glVertex3f(0.304000f, -0.934000f, 0.188000f);
		glNormal3f(0.723672f, -0.525509f, 0.447369f); glVertex3f(0.607000f, 0.000000f, 0.795000f);
		glNormal3f(0.723672f, -0.525509f, 0.447369f); glVertex3f(0.188000f, -0.577000f, 0.795000f);
		glNormal3f(0.275624f, 0.850675f, -0.447642f); glVertex3f(0.491000f, 0.357000f, -0.795000f);
		glNormal3f(0.275624f, 0.850675f, -0.447642f); glVertex3f(-0.188000f, 0.577000f, -0.795000f);
		glNormal3f(0.275624f, 0.850675f, -0.447642f); glVertex3f(-0.304000f, 0.934000f, -0.188000f);
		glNormal3f(0.276348f, 0.850851f, -0.446861f); glVertex3f(0.491000f, 0.357000f, -0.795000f);
		glNormal3f(0.276348f, 0.850851f, -0.446861f); glVertex3f(-0.304000f, 0.934000f, -0.188000f);
		glNormal3f(0.276348f, 0.850851f, -0.446861f); glVertex3f(0.304000f, 0.934000f, 0.188000f);
		glNormal3f(0.276455f, 0.850833f, -0.446829f); glVertex3f(0.491000f, 0.357000f, -0.795000f);
		glNormal3f(0.276455f, 0.850833f, -0.446829f); glVertex3f(0.304000f, 0.934000f, 0.188000f);
		glNormal3f(0.276455f, 0.850833f, -0.446829f); glVertex3f(0.795000f, 0.577000f, -0.188000f);
		glNormal3f(-0.723766f, 0.525577f, -0.447137f); glVertex3f(-0.188000f, 0.577000f, -0.795000f);
		glNormal3f(-0.723766f, 0.525577f, -0.447137f); glVertex3f(-0.607000f, 0.000000f, -0.795000f);
		glNormal3f(-0.723766f, 0.525577f, -0.447137f); glVertex3f(-0.982000f, 0.000000f, -0.188000f);
		glNormal3f(-0.723750f, 0.525790f, -0.446914f); glVertex3f(-0.188000f, 0.577000f, -0.795000f);
		glNormal3f(-0.723750f, 0.525790f, -0.446914f); glVertex3f(-0.982000f, 0.000000f, -0.188000f);
		glNormal3f(-0.723750f, 0.525790f, -0.446914f); glVertex3f(-0.795000f, 0.577000f, 0.188000f);
		glNormal3f(-0.724160f, 0.525008f, -0.447167f); glVertex3f(-0.188000f, 0.577000f, -0.795000f);
		glNormal3f(-0.724160f, 0.525008f, -0.447167f); glVertex3f(-0.795000f, 0.577000f, 0.188000f);
		glNormal3f(-0.724160f, 0.525008f, -0.447167f); glVertex3f(-0.304000f, 0.934000f, -0.188000f);
		glNormal3f(-0.723672f, -0.525509f, -0.447369f); glVertex3f(-0.607000f, 0.000000f, -0.795000f);
		glNormal3f(-0.723672f, -0.525509f, -0.447369f); glVertex3f(-0.188000f, -0.577000f, -0.795000f);
		glNormal3f(-0.723672f, -0.525509f, -0.447369f); glVertex3f(-0.304000f, -0.934000f, -0.188000f);
		glNormal3f(-0.724136f, -0.525317f, -0.446842f); glVertex3f(-0.607000f, 0.000000f, -0.795000f);
		glNormal3f(-0.724136f, -0.525318f, -0.446842f); glVertex3f(-0.304000f, -0.934000f, -0.188000f);
		glNormal3f(-0.724136f, -0.525317f, -0.446842f); glVertex3f(-0.795000f, -0.577000f, 0.188000f);
		glNormal3f(-0.723628f, -0.525840f, -0.447052f); glVertex3f(-0.607000f, 0.000000f, -0.795000f);
		glNormal3f(-0.723628f, -0.525840f, -0.447052f); glVertex3f(-0.795000f, -0.577000f, 0.188000f);
		glNormal3f(-0.723628f, -0.525840f, -0.447052f); glVertex3f(-0.982000f, 0.000000f, -0.188000f);
		glNormal3f(0.275782f, -0.851164f, -0.446613f); glVertex3f(-0.188000f, -0.577000f, -0.795000f);
		glNormal3f(0.275782f, -0.851164f, -0.446613f); glVertex3f(0.491000f, -0.357000f, -0.795000f);
		glNormal3f(0.275782f, -0.851164f, -0.446613f); glVertex3f(0.795000f, -0.577000f, -0.188000f);
		glNormal3f(0.276131f, -0.850755f, -0.447178f); glVertex3f(-0.188000f, -0.577000f, -0.795000f);
		glNormal3f(0.276131f, -0.850755f, -0.447178f); glVertex3f(0.795000f, -0.577000f, -0.188000f);
		glNormal3f(0.276131f, -0.850755f, -0.447178f); glVertex3f(0.304000f, -0.934000f, 0.188000f);
		glNormal3f(0.276647f, -0.850500f, -0.447344f); glVertex3f(-0.188000f, -0.577000f, -0.795000f);
		glNormal3f(0.276647f, -0.850500f, -0.447344f); glVertex3f(0.304000f, -0.934000f, 0.188000f);
		glNormal3f(0.276647f, -0.850500f, -0.447344f); glVertex3f(-0.304000f, -0.934000f, -0.188000f);
		glNormal3f(0.894132f, 0.000000f, -0.447803f); glVertex3f(0.491000f, -0.357000f, -0.795000f);
		glNormal3f(0.894132f, 0.000000f, -0.447803f); glVertex3f(0.491000f, 0.357000f, -0.795000f);
		glNormal3f(0.894132f, 0.000000f, -0.447803f); glVertex3f(0.795000f, 0.577000f, -0.188000f);
		glNormal3f(0.894756f, -0.001014f, -0.446554f); glVertex3f(0.491000f, -0.357000f, -0.795000f);
		glNormal3f(0.894756f, -0.001014f, -0.446555f); glVertex3f(0.795000f, 0.577000f, -0.188000f);
		glNormal3f(0.894756f, -0.001014f, -0.446554f); glVertex3f(0.982000f, 0.000000f, 0.188000f);
		glNormal3f(0.894369f, 0.001642f, -0.447326f); glVertex3f(0.491000f, -0.357000f, -0.795000f);
		glNormal3f(0.894369f, 0.001642f, -0.447326f); glVertex3f(0.982000f, 0.000000f, 0.188000f);
		glNormal3f(0.894369f, 0.001642f, -0.447326f); glVertex3f(0.795000f, -0.577000f, -0.188000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(0.491000f, -0.357000f, -0.795000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(-0.188000f, -0.577000f, -0.795000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(-0.607000f, 0.000000f, -0.795000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(0.491000f, -0.357000f, -0.795000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(-0.607000f, 0.000000f, -0.795000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(-0.188000f, 0.577000f, -0.795000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(0.491000f, -0.357000f, -0.795000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(-0.188000f, 0.577000f, -0.795000f);
		glNormal3f(0.000000f, 0.000000f, -1.000000f); glVertex3f(0.491000f, 0.357000f, -0.795000f);
	}
	
	public static void icosahedron() {
		glNormal3f(0.491421f, 0.356872f, 0.794448f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(0.491421f, 0.356872f, 0.794448f); glVertex3f(0.894000f, 0.000000f, 0.447000f);
		glNormal3f(0.491421f, 0.356872f, 0.794448f); glVertex3f(0.276000f, 0.851000f, 0.447000f);
		glNormal3f(-0.187612f, 0.577268f, 0.794709f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(-0.187612f, 0.577268f, 0.794710f); glVertex3f(0.276000f, 0.851000f, 0.447000f);
		glNormal3f(-0.187612f, 0.577268f, 0.794710f); glVertex3f(-0.724000f, 0.526000f, 0.447000f);
		glNormal3f(-0.607002f, 0.000000f, 0.794700f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(-0.607002f, 0.000000f, 0.794700f); glVertex3f(-0.724000f, 0.526000f, 0.447000f);
		glNormal3f(-0.607002f, 0.000000f, 0.794700f); glVertex3f(-0.724000f, -0.526000f, 0.447000f);
		glNormal3f(-0.187612f, -0.577268f, 0.794709f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(-0.187612f, -0.577268f, 0.794710f); glVertex3f(-0.724000f, -0.526000f, 0.447000f);
		glNormal3f(-0.187612f, -0.577268f, 0.794710f); glVertex3f(0.276000f, -0.851000f, 0.447000f);
		glNormal3f(0.491421f, -0.356872f, 0.794448f); glVertex3f(0.000000f, 0.000000f, 1.000000f);
		glNormal3f(0.491421f, -0.356872f, 0.794448f); glVertex3f(0.276000f, -0.851000f, 0.447000f);
		glNormal3f(0.491421f, -0.356872f, 0.794448f); glVertex3f(0.894000f, 0.000000f, 0.447000f);
		glNormal3f(0.187612f, 0.577268f, -0.794710f); glVertex3f(-0.276000f, 0.851000f, -0.447000f);
		glNormal3f(0.187612f, 0.577268f, -0.794710f); glVertex3f(0.724000f, 0.526000f, -0.447000f);
		glNormal3f(0.187612f, 0.577268f, -0.794709f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
		glNormal3f(-0.491421f, 0.356872f, -0.794448f); glVertex3f(-0.894000f, 0.000000f, -0.447000f);
		glNormal3f(-0.491421f, 0.356872f, -0.794448f); glVertex3f(-0.276000f, 0.851000f, -0.447000f);
		glNormal3f(-0.491421f, 0.356872f, -0.794448f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
		glNormal3f(-0.491421f, -0.356872f, -0.794448f); glVertex3f(-0.276000f, -0.851000f, -0.447000f);
		glNormal3f(-0.491421f, -0.356872f, -0.794448f); glVertex3f(-0.894000f, 0.000000f, -0.447000f);
		glNormal3f(-0.491421f, -0.356872f, -0.794448f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
		glNormal3f(0.187612f, -0.577268f, -0.794710f); glVertex3f(0.724000f, -0.526000f, -0.447000f);
		glNormal3f(0.187612f, -0.577268f, -0.794710f); glVertex3f(-0.276000f, -0.851000f, -0.447000f);
		glNormal3f(0.187612f, -0.577268f, -0.794709f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
		glNormal3f(0.607002f, 0.000000f, -0.794700f); glVertex3f(0.724000f, 0.526000f, -0.447000f);
		glNormal3f(0.607002f, 0.000000f, -0.794700f); glVertex3f(0.724000f, -0.526000f, -0.447000f);
		glNormal3f(0.607002f, 0.000000f, -0.794700f); glVertex3f(0.000000f, 0.000000f, -1.000000f);
		glNormal3f(0.794653f, 0.577081f, 0.188427f); glVertex3f(0.724000f, 0.526000f, -0.447000f);
		glNormal3f(0.794653f, 0.577081f, 0.188427f); glVertex3f(0.276000f, 0.851000f, 0.447000f);
		glNormal3f(0.794653f, 0.577081f, 0.188427f); glVertex3f(0.894000f, 0.000000f, 0.447000f);
		glNormal3f(-0.303607f, 0.934174f, 0.187462f); glVertex3f(-0.276000f, 0.851000f, -0.447000f);
		glNormal3f(-0.303607f, 0.934174f, 0.187462f); glVertex3f(-0.724000f, 0.526000f, 0.447000f);
		glNormal3f(-0.303607f, 0.934174f, 0.187462f); glVertex3f(0.276000f, 0.851000f, 0.447000f);
		glNormal3f(-0.982396f, 0.000000f, 0.186809f); glVertex3f(-0.894000f, 0.000000f, -0.447000f);
		glNormal3f(-0.982396f, 0.000000f, 0.186809f); glVertex3f(-0.724000f, -0.526000f, 0.447000f);
		glNormal3f(-0.982396f, 0.000000f, 0.186809f); glVertex3f(-0.724000f, 0.526000f, 0.447000f);
		glNormal3f(-0.303607f, -0.934174f, 0.187462f); glVertex3f(-0.276000f, -0.851000f, -0.447000f);
		glNormal3f(-0.303607f, -0.934174f, 0.187462f); glVertex3f(0.276000f, -0.851000f, 0.447000f);
		glNormal3f(-0.303607f, -0.934174f, 0.187462f); glVertex3f(-0.724000f, -0.526000f, 0.447000f);
		glNormal3f(0.794653f, -0.577081f, 0.188427f); glVertex3f(0.724000f, -0.526000f, -0.447000f);
		glNormal3f(0.794653f, -0.577081f, 0.188427f); glVertex3f(0.894000f, 0.000000f, 0.447000f);
		glNormal3f(0.794653f, -0.577081f, 0.188427f); glVertex3f(0.276000f, -0.851000f, 0.447000f);
		glNormal3f(0.303607f, 0.934174f, -0.187462f); glVertex3f(0.724000f, 0.526000f, -0.447000f);
		glNormal3f(0.303607f, 0.934174f, -0.187462f); glVertex3f(-0.276000f, 0.851000f, -0.447000f);
		glNormal3f(0.303607f, 0.934174f, -0.187462f); glVertex3f(0.276000f, 0.851000f, 0.447000f);
		glNormal3f(-0.794653f, 0.577081f, -0.188427f); glVertex3f(-0.276000f, 0.851000f, -0.447000f);
		glNormal3f(-0.794653f, 0.577081f, -0.188427f); glVertex3f(-0.894000f, 0.000000f, -0.447000f);
		glNormal3f(-0.794653f, 0.577081f, -0.188427f); glVertex3f(-0.724000f, 0.526000f, 0.447000f);
		glNormal3f(-0.794653f, -0.577081f, -0.188427f); glVertex3f(-0.894000f, 0.000000f, -0.447000f);
		glNormal3f(-0.794653f, -0.577081f, -0.188427f); glVertex3f(-0.276000f, -0.851000f, -0.447000f);
		glNormal3f(-0.794653f, -0.577081f, -0.188427f); glVertex3f(-0.724000f, -0.526000f, 0.447000f);
		glNormal3f(0.303607f, -0.934174f, -0.187462f); glVertex3f(-0.276000f, -0.851000f, -0.447000f);
		glNormal3f(0.303607f, -0.934174f, -0.187462f); glVertex3f(0.724000f, -0.526000f, -0.447000f);
		glNormal3f(0.303607f, -0.934174f, -0.187462f); glVertex3f(0.276000f, -0.851000f, 0.447000f);
		glNormal3f(0.982396f, 0.000000f, -0.186809f); glVertex3f(0.724000f, -0.526000f, -0.447000f);
		glNormal3f(0.982396f, 0.000000f, -0.186809f); glVertex3f(0.724000f, 0.526000f, -0.447000f);
		glNormal3f(0.982396f, 0.000000f, -0.186809f); glVertex3f(0.894000f, 0.000000f, 0.447000f);
	}
	
	public static void octahedronOutline(MatrixStack m, VertexConsumer vc, float r, float g, float b, float a) {
		YttrClient.addLine(m, vc,
			0.000000f, 0.000000f, 1.000000f,
			1.000000f, 0.000000f, 0.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, 1.000000f, 0.000000f,
			0.000000f, 0.000000f, 1.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, 1.000000f, 0.000000f,
			-1.000000f, 0.000000f, 0.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, 0.000000f, 1.000000f,
			-1.000000f, 0.000000f, 0.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, -1.000000f, 0.000000f,
			0.000000f, 0.000000f, 1.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, -1.000000f, 0.000000f,
			1.000000f, 0.000000f, 0.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, 1.000000f, 0.000000f,
			1.000000f, 0.000000f, 0.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, 0.000000f, -1.000000f,
			-1.000000f, 0.000000f, 0.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, 1.000000f, 0.000000f,
			0.000000f, 0.000000f, -1.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, -1.000000f, 0.000000f,
			-1.000000f, 0.000000f, 0.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, 0.000000f, -1.000000f,
			1.000000f, 0.000000f, 0.000000f,
			r, g, b, a,
			r, g, b, a);
		YttrClient.addLine(m, vc,
			0.000000f, -1.000000f, 0.000000f,
			0.000000f, 0.000000f, -1.000000f,
			r, g, b, a,
			r, g, b, a);
	}
	
	public static void dodecahedronOutline(MatrixStack m, VertexConsumer vc, float r, float g, float b, float a) {
		YttrClient.addLine(m, vc,
			0.607000f, 0.000000f, 0.795000f,
			0.188000f, 0.577000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.188000f, 0.577000f, 0.795000f,
			-0.491000f, 0.357000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.491000f, 0.357000f, 0.795000f,
			-0.491000f, -0.357000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.491000f, -0.357000f, 0.795000f,
			0.188000f, -0.577000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.188000f, -0.577000f, 0.795000f,
			0.607000f, 0.000000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		
		
		YttrClient.addLine(m, vc,
			0.982000f, 0.000000f, 0.188000f,
			0.795000f, 0.577000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.795000f, 0.577000f, -0.188000f,
			0.304000f, 0.934000f, 0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.304000f, 0.934000f, 0.188000f,
			0.188000f, 0.577000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.982000f, 0.000000f, 0.188000f,
			0.607000f, 0.000000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		
		
		YttrClient.addLine(m, vc,
			0.304000f, 0.934000f, 0.188000f,
			-0.304000f, 0.934000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.304000f, 0.934000f, -0.188000f,
			-0.795000f, 0.577000f, 0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.795000f, 0.577000f, 0.188000f,
			-0.491000f, 0.357000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		
		YttrClient.addLine(m, vc,
			-0.795000f, 0.577000f, 0.188000f,
			-0.982000f, 0.000000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.982000f, 0.000000f, -0.188000f,
			-0.795000f, -0.577000f, 0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.795000f, -0.577000f, 0.188000f,
			-0.491000f, -0.357000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.795000f, -0.577000f, 0.188000f,
			-0.304000f, -0.934000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.304000f, -0.934000f, -0.188000f,
			0.304000f, -0.934000f, 0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.304000f, -0.934000f, 0.188000f,
			0.188000f, -0.577000f, 0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		
		YttrClient.addLine(m, vc,
			0.304000f, -0.934000f, 0.188000f,
			0.795000f, -0.577000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.795000f, -0.577000f, -0.188000f,
			0.982000f, 0.000000f, 0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		
		
		YttrClient.addLine(m, vc,
			0.491000f, 0.357000f, -0.795000f,
			-0.188000f, 0.577000f, -0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.188000f, 0.577000f, -0.795000f,
			-0.304000f, 0.934000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.491000f, 0.357000f, -0.795000f,
			0.795000f, 0.577000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.188000f, 0.577000f, -0.795000f,
			-0.607000f, 0.000000f, -0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.607000f, 0.000000f, -0.795000f,
			-0.982000f, 0.000000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		
		
		YttrClient.addLine(m, vc,
			-0.607000f, 0.000000f, -0.795000f,
			-0.188000f, -0.577000f, -0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.188000f, -0.577000f, -0.795000f,
			-0.304000f, -0.934000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		
		
		YttrClient.addLine(m, vc,
			-0.188000f, -0.577000f, -0.795000f,
			0.491000f, -0.357000f, -0.795000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.491000f, -0.357000f, -0.795000f,
			0.795000f, -0.577000f, -0.188000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.491000f, -0.357000f, -0.795000f,
			0.491000f, 0.357000f, -0.795000f,
			r, g, b, a,
			r, g, b, a);
	}
	
	public static void icosahedronOutline(MatrixStack m, VertexConsumer vc, float r, float g, float b, float a) {
		YttrClient.addLine(m, vc,
			0.000000f, 0.000000f, 1.000000f,
			0.894000f, 0.000000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.894000f, 0.000000f, 0.447000f,
			0.276000f, 0.851000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.276000f, 0.851000f, 0.447000f,
			0.000000f, 0.000000f, 1.000000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.276000f, 0.851000f, 0.447000f,
			-0.724000f, 0.526000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.724000f, 0.526000f, 0.447000f,
			0.000000f, 0.000000f, 1.000000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.724000f, 0.526000f, 0.447000f,
			-0.724000f, -0.526000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.724000f, -0.526000f, 0.447000f,
			0.000000f, 0.000000f, 1.000000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.724000f, -0.526000f, 0.447000f,
			0.276000f, -0.851000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.276000f, -0.851000f, 0.447000f,
			0.000000f, 0.000000f, 1.000000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.276000f, -0.851000f, 0.447000f,
			0.894000f, 0.000000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.276000f, 0.851000f, -0.447000f,
			0.724000f, 0.526000f, -0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.724000f, 0.526000f, -0.447000f,
			0.000000f, 0.000000f, -1.000000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.000000f, 0.000000f, -1.000000f,
			-0.894000f, 0.000000f, -0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.894000f, 0.000000f, -0.447000f,
			-0.276000f, 0.851000f, -0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.276000f, 0.851000f, -0.447000f,
			0.000000f, 0.000000f, -1.000000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.000000f, 0.000000f, -1.000000f,
			-0.276000f, -0.851000f, -0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.276000f, -0.851000f, -0.447000f,
			-0.894000f, 0.000000f, -0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.000000f, 0.000000f, -1.000000f,
			0.724000f, -0.526000f, -0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.724000f, -0.526000f, -0.447000f,
			-0.276000f, -0.851000f, -0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.724000f, 0.526000f, -0.447000f,
			0.724000f, -0.526000f, -0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.724000f, 0.526000f, -0.447000f,
			0.276000f, 0.851000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.276000f, 0.851000f, -0.447000f,
			-0.724000f, 0.526000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.894000f, 0.000000f, -0.447000f,
			-0.724000f, -0.526000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.276000f, -0.851000f, -0.447000f,
			0.276000f, -0.851000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.724000f, -0.526000f, -0.447000f,
			0.894000f, 0.000000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.276000f, 0.851000f, -0.447000f,
			0.276000f, 0.851000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.894000f, 0.000000f, -0.447000f,
			-0.724000f, 0.526000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			-0.276000f, -0.851000f, -0.447000f,
			-0.724000f, -0.526000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.724000f, -0.526000f, -0.447000f,
			0.276000f, -0.851000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
		
		YttrClient.addLine(m, vc,
			0.724000f, 0.526000f, -0.447000f,
			0.894000f, 0.000000f, 0.447000f,
			r, g, b, a,
			r, g, b, a);
	}
	

}
