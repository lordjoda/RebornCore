/*
 * Copyright (c) 2017 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.LootManager;
import reborncore.common.util.RebornCraftingHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Gigabit101 on 16/08/2016.
 */
public class RebornRegistry {
	public static LootManager.InnerPool lp = new LootManager.InnerPool();

	public static void registerBlock(Block block, String name) {
		block.setRegistryName(name);
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block), block.getRegistryName());
	}

	public static void registerBlock(Block block, ResourceLocation name) {
		block.setRegistryName(name);
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block), block.getRegistryName());
	}

	public static void registerBlock(Block block, Class<? extends ItemBlock> itemclass, String name) {
		block.setRegistryName(name);
		GameRegistry.register(block);
		try {
			ItemBlock itemBlock = itemclass.getConstructor(Block.class).newInstance(block);
			itemBlock.setRegistryName(name);
			GameRegistry.register(itemBlock);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void registerBlock(Block block) {
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block), block.getRegistryName());
	}

	public static void registerItem(Item item) {
		GameRegistry.register(item);
	}

	public static void registerItem(Item item, ResourceLocation name) {
		GameRegistry.register(item, name);
	}

	public static void addShapedOreRecipe(ItemStack output, Object parm) {
		RebornCraftingHelper.addShapedOreRecipe(output, parm);
	}

	public static void addShapelessOreRecipe(ItemStack output, Object parm) {
		RebornCraftingHelper.addShapelessOreRecipe(output, parm);
	}

	//eg: RebornRegistry.addLoot(Items.NETHER_STAR, 0.95, LootTableList.CHESTS_VILLAGE_BLACKSMITH);
	//eg: RebornRegistry.addLoot(Items.DIAMOND, 1.95, LootTableList.ENTITIES_COW);

	public static void addLoot(Item item, double chance, ResourceLocation list) {
		lp.addItem(LootManager.createLootEntry(item, chance, list));
	}

	public static void addLoot(Item item, int minSize, int maxSize, double chance, ResourceLocation list) {
		lp.addItem(LootManager.createLootEntry(item, minSize, maxSize, chance, list));
	}

	public static void addLoot(Item item, int ordinal, int minSize, int maxSize, double chance, ResourceLocation list) {
		lp.addItem(LootManager.createLootEntry(item, ordinal, minSize, maxSize, chance, list));
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModel(Item i, int meta) {
		ResourceLocation loc = i.getRegistryName();
		ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModel(Block b, int meta) {
		registerItemModel(Item.getItemFromBlock(b), meta);
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModel(Item i, int meta, String variant) {
		ResourceLocation loc = i.getRegistryName();
		ModelLoader.setCustomModelResourceLocation(i, meta, new ModelResourceLocation(loc, "type=" + variant));
	}
}
