package com.unascribed.yttr.mixin.damage_enchant;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.unascribed.yttr.mixinsupport.ClassNodeTransformer;

import net.fabricmc.loader.api.FabricLoader;

public class TransformerEntities implements ClassNodeTransformer {

	@Override
	public void transform(String name, ClassNode node) {
		var EnchantmentHelper = "net/minecraft/class_1890";
			var getAttackDamage = "method_8218";
		
		var Entity = "net/minecraft/class_1297";
			var getGroup = "method_6046";
		
		var DamageSource = "net/minecraft/class_1282";
		
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			EnchantmentHelper = "net/minecraft/enchantment/EnchantmentHelper";
				getAttackDamage = "getAttackDamage";
			
			Entity = "net/minecraft/entity/Entity";
				getGroup = "getGroup";
			
			DamageSource = "net/minecraft/entity/damage/DamageSource";
		}

		var LEntity = "L"+Entity+";";
		var LDamageSource = "L"+DamageSource+";";
		
		
		for (var mn : node.methods) {
			if (mn.desc.contains(LEntity)) {
				int entityLocal = -1;
				int entityLocalCandidate = -1;
				boolean foundAttackDamageCall = false;
				for (var insn : mn.instructions) {
					if (insn instanceof VarInsnNode vin && vin.getOpcode() == ALOAD) {
						entityLocalCandidate = vin.var;
					} else if (insn instanceof MethodInsnNode min) {
						if (min.name.equals(getGroup)) {
							entityLocal = entityLocalCandidate;
						}
						if (min.owner.equals(EnchantmentHelper)
								&& min.name.equals(getAttackDamage)) {
							if (entityLocal != -1) {
								mn.instructions.insert(min, insns(
									ALOAD(entityLocal),
									ALOAD(0),
									INVOKESTATIC("com/unascribed/yttr/Yttr", "getAdditionalAttackDamage", "("+LEntity+LEntity+")F"),
									FADD()
								));
								foundAttackDamageCall = true;
							}
						}
						
						if (foundAttackDamageCall) {
							if (min.desc.equals("("+LDamageSource+"F)Z")) {
								mn.instructions.insertBefore(min, insns(
									// the stack is […, DamageSource, float]
									DUP_X1(), // stack is now […, float, DamageSource, float]
									POP(), // stack is now […, float, DamageSource]
									INVOKESTATIC("com/unascribed/yttr/Yttr", "modifyDamageSource", "("+LDamageSource+")"+LDamageSource),
									// repeat what we did to undo it
									DUP_X1(),
									POP()
								));
							}
						}
					}
				}
			}
		}
	}

	private InsnList insns(AbstractInsnNode... insns) {
		var li = new InsnList();
		for (var i : insns) li.add(i);
		return li;
	}
	
	private static VarInsnNode ALOAD(int var) {
		return new VarInsnNode(ALOAD, var);
	}

	private static InsnNode DUP_X1() {
		return new InsnNode(DUP_X1);
	}
	
	private static InsnNode POP() {
		return new InsnNode(POP);
	}
	
	private static InsnNode FADD() {
		return new InsnNode(FADD);
	}

	private static MethodInsnNode INVOKESTATIC(String owner, String name, String desc) {
		return new MethodInsnNode(INVOKESTATIC, owner, name, desc);
	}

}
