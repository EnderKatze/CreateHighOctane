package de.enderkatze.highoctane.content.items;


import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;


public class TurboItem extends Item {

    public TurboItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // TODO: Put your right-click interaction here.

        return InteractionResult.PASS;
    }
}
