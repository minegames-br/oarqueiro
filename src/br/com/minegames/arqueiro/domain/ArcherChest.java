package br.com.minegames.arqueiro.domain;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class ArcherChest {

	private Chest chest;
	private ItemStack[] items;

	public ArcherChest(Chest chest) {
		this.setChest(chest);
	}

	public void refillChest() {

		items = new ItemStack[10];

		items[0] = new ItemStack(Material.IRON_SWORD);
		items[1] = new ItemStack(Material.GOLD_SWORD);
		items[2] = new ItemStack(Material.GOLDEN_APPLE);
		items[3] = new ItemStack(Material.IRON_HELMET);
		items[4] = new ItemStack(Material.GOLD_HELMET);
		items[5] = new ItemStack(Material.IRON_BOOTS);
		items[6] = new ItemStack(Material.GOLD_BOOTS);
		items[7] = new ItemStack(Material.GOLD_LEGGINGS);
		items[8] = new ItemStack(Material.IRON_CHESTPLATE);
		items[9] = new ItemStack(Material.GOLD_CHESTPLATE);
		
		Random r = new Random();
		int rItem = r.nextInt(items.length);
		this.getChest().getInventory().addItem(items[rItem]);
	}

	public void clearChest() {
		this.getChest().getInventory().clear();
		;
	}

	public Chest getChest() {
		return chest;
	}

	public void setChest(Chest chest) {
		this.chest = chest;
	}

	public ItemStack[] getItems() {
		return items;
	}
}
