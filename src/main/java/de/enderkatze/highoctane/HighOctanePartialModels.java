package de.enderkatze.highoctane;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class HighOctanePartialModels {


    public static final PartialModel V6_CYLINDERS_1 = model("block/v6_engine/partial/cylinders/v6_cylinder_1");
    public static final PartialModel V6_CYLINDERS_2 = model("block/v6_engine/partial/cylinders/v6_cylinder_2");
    public static final PartialModel V6_CYLINDERS_3 = model("block/v6_engine/partial/cylinders/v6_cylinder_3");
    public static final PartialModel V6_CYLINDERS_4 = model("block/v6_engine/partial/cylinders/v6_cylinder_4");
    public static final PartialModel V6_CYLINDERS_5 = model("block/v6_engine/partial/cylinders/v6_cylinder_5");
    public static final PartialModel V6_CYLINDERS_6 = model("block/v6_engine/partial/cylinders/v6_cylinder_6");

    public static final PartialModel V6_BAY_1 = model("block/v6_engine/partial/v6_bay_1");
    public static final PartialModel V6_BAY_2 = model("block/v6_engine/partial/v6_bay_2");

    public static PartialModel model(String id){
        return PartialModel.of(HighOctane.rl(id));
    }

    public static void init(){}
}
