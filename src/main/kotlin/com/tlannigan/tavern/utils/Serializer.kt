package com.tlannigan.tavern.utils

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class Serializer {

    /**
     * Converts a [playerInventory] to an array of Base64 strings.
     * Index 0 is content and 1 is the armor.
     */
    @Throws(IllegalStateException::class)
    fun playerInventoryToBase64(playerInventory: PlayerInventory): Array<String> {
        // Get the main content part, this doesn't return the armor
        val content = toBase64(playerInventory)
        val armor = itemStackArrayToBase64(playerInventory.armorContents)
        return arrayOf(content, armor)
    }

    /**
     * Serializes an [items] array to Base64 String.
     */
    @Throws(IllegalStateException::class)
    fun itemStackArrayToBase64(items: Array<ItemStack?>): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)

            // Write the size of the inventory
            dataOutput.writeInt(items.size)

            // Save every element in the list
            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }

            // Serialize that array
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }

    /**
     * Serialize an [inventory] to Base64 string.
     */
    @Throws(IllegalStateException::class)
    fun toBase64(inventory: Inventory): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)

            // Write the size of the inventory
            dataOutput.writeInt(inventory.size)

            // Save every element in the list
            for (i in 0 until inventory.size) {
                dataOutput.writeObject(inventory.getItem(i))
            }

            // Serialize that array
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }

    /**
     * Returns an inventory from a Base64 [string].
     */
    @Throws(IOException::class)
    fun fromBase64(string: String?): Inventory? {
        return try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(string))
            val dataInput = BukkitObjectInputStream(inputStream)
            val inventory = Bukkit.getServer().createInventory(null, dataInput.readInt())

            // Read the serialized inventory
            for (i in 0 until inventory.size) {
                inventory.setItem(i, dataInput.readObject() as ItemStack)
            }
            dataInput.close()
            inventory
        } catch (e: ClassNotFoundException) {
            throw IOException("Unable to decode class type.", e)
        }
    }

    /**
     * Returns an array of ItemStacks from a Base64 [string].
     */
    @Throws(IOException::class)
    fun itemStackArrayFromBase64(string: String?): Array<ItemStack?>? {
        return try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(string))
            val dataInput = BukkitObjectInputStream(inputStream)
            val items = arrayOfNulls<ItemStack>(dataInput.readInt())

            // Read the serialized inventory
            for (i in items.indices) {
                items[i] = dataInput.readObject() as? ItemStack
            }
            dataInput.close()
            items
        } catch (e: ClassNotFoundException) {
            throw IOException("Unable to decode class type.", e)
        }
    }
}
