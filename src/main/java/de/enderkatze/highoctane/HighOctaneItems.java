package de.enderkatze.highoctane;


import com.tterrag.registrate.util.entry.ItemEntry;
import de.enderkatze.highoctane.content.items.TurboItem;
import net.minecraft.world.item.Item;

import static de.enderkatze.highoctane.HighOctane.REGISTRATE;


public class HighOctaneItems {

    public static final ItemEntry<TurboItem> TURBO_ITEM = REGISTRATE.item("turbocharger", TurboItem::new).register();

    public static void register() {
    }
}
