package com.unascribed.yttr.content.block.device;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;
import com.unascribed.yttr.util.YRandom;
import com.unascribed.yttr.util.YTickable;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class CentrifugeBlockEntity extends BlockEntity implements SideyInventory, YTickable, Nameable, DelegatingInventory {

	private final SimpleInventory inv = new SimpleInventory(6);
	
	private int fuelTime;
	private int maxFuelTime;
	private int spinTime;
	private int maxSpinTime;
	
	private boolean fuelLocked = false;
	
	private final PropertyDelegate properties = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0: return fuelTime;
				case 1: return maxFuelTime;
				case 2: return spinTime;
				case 3: return maxSpinTime;
				case 4: return fuelLocked ? 1 : 0;
				default: return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch(index) {
				case 0: fuelTime = value; break;
				case 1: maxFuelTime = value; break;
				case 2: spinTime = value; break;
				case 3: maxSpinTime = value; break;
				case 4: fuelLocked = value != 0; break;
			}

		}

		@Override
		public int size() {
			return 4;
		}
	};
	
	public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.CENTRIFUGE, pos, state);
		inv.addListener(i -> markDirty());
	}
	
	public PropertyDelegate getProperties() {
		return properties;
	}
	
	@Override
	public void tick() {
		boolean burning = isBurning();
		boolean needsDirty = false;
		if (fuelTime > 0) fuelTime--;

		if (!world.isClient) {
			ItemStack fuelStack = getStack(5);
			if (!isBurning() && (fuelStack.isEmpty() || getStack(0).isEmpty())) {
				if (!isBurning() && spinTime > 0) {
					spinTime = MathHelper.clamp(spinTime - 2, 0, maxSpinTime);
				}
			} else {
				var input = getStack(0);
				CentrifugingRecipe recipe = world.getRecipeManager().getFirstMatch(YRecipeTypes.CENTRIFUGING, this, world).orElse(null);
				if (!isBurning() && recipe != null && recipe.canFitOutput(this)) {
					fuelTime = FurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuelStack.getItem(), 0);
					maxFuelTime = fuelTime;
					if (isBurning()) {
						needsDirty = true;
						if (!fuelStack.isEmpty()) {
							Item fuelItem = fuelStack.getItem();
							fuelStack.decrement(1);
							if (fuelStack.isEmpty()) {
								Item remainder = fuelItem.getRecipeRemainder();
								setStack(5, remainder == null ? ItemStack.EMPTY : new ItemStack(remainder));
							}
						}
					}
				}

				if (isBurning() && recipe != null && recipe.canFitOutput(this)) {
					boolean eligible = true;
					boolean copyNbt = false;
					if (input.isOf(YItems.HAEMOPAL)) {
						if (input.hasNbt()) {
							var player = world.getPlayerByUuid(input.getNbt().getUuid("Owner"));
							if (player == null || player.isDead()) {
								eligible = false;
							} else {
								copyNbt = true;
								if (YRandom.get().nextInt(16) == 0) {
									player.damage(player.getDamageSources().magic(), 1.5f);
									if (player.getHealth() < 10 && player.getHungerManager().getSaturationLevel() < 1 && player.getHungerManager().getFoodLevel() == 20) {
										player.getHungerManager().setFoodLevel(19);
									}
								}
							}
						} else {
							eligible = false;
						}
					}
					if (eligible) {
						maxSpinTime = recipe.getSpinTime();
						spinTime++;
						if (spinTime >= maxSpinTime) {
							spinTime = 0;
							recipe.craft(this, world.getRegistryManager());
							needsDirty = true;
							if (copyNbt) {
								for (var i = 1; i < 5; i++) {
									var is = getStack(i);
									if (is.isOf(YItems.EMPTY_HAEMOPAL)) {
										is.setNbt(input.getNbt().copy());
									}
								}
							}
						}
					} else {
						spinTime = 0;
					}
				} else {
					spinTime = 0;
				}
			}

			if (burning != isBurning()) {
				needsDirty = true;
				world.setBlockState(pos, getCachedState().with(CentrifugeBlock.LIT, isBurning()), 3);
			}
		}

		if (needsDirty) {
			markDirty();
		}
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putInt("FuelTime", fuelTime);
		tag.putInt("MaxFuelTime", maxFuelTime);
		tag.putInt("SpinTime", spinTime);
		tag.putInt("MaxSpinTime", maxSpinTime);
		if (fuelLocked) tag.putBoolean("FuelLocked", true);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		fuelTime = tag.getInt("FuelTime");
		maxFuelTime = tag.getInt("MaxFuelTime");
		spinTime = tag.getInt("SpinTime");
		maxSpinTime = tag.getInt("MaxSpinTime");
		fuelLocked = tag.getBoolean("FuelLocked");
	}
	
	public boolean isBurning() {
		return fuelTime > 0;
	}
	
	@Override
	public Inventory getDelegateInv() {
		return inv;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return player.squaredDistanceTo(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) < 8*8;
	}
	
	@Override
	public Text getName() {
		return Text.translatable("block.yttr.centrifuge");
	}
	
	@Override
	public boolean canAccess(int slot, Direction side) {
		if (side == Direction.UP) return slot == 0;
		if (side == Direction.DOWN) return slot >= 1 && slot <= 4;
		return slot == 5;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.UP) return slot == 0;
		if (dir == Direction.DOWN) return false;
		return slot == 5 && FurnaceBlockEntity.canUseAsFuel(stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN) return slot != 0 && slot != 5;
		return false;
	}

}
