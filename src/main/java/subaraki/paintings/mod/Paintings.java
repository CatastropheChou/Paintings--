package subaraki.paintings.mod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import subaraki.paintings.config.ConfigurationHandler;
import subaraki.paintings.mod.client.RenderPaintingLate;
import subaraki.paintings.mod.server.proxy.CommonProxy;


@Mod(modid = Paintings.MODID, name = Paintings.NAME, version = Paintings.VERSION, dependencies = "after:PaintingSelGui")
public class Paintings {

    public static final String MODID = "morepaintings";
    public static final String VERSION = "1.11.2-3.2.1.1";
    public static final String NAME = "Paintings++";

    @SidedProxy(serverSide = "subaraki.paintings.mod.server.proxy.CommonProxy", clientSide = "subaraki.paintings.mod.client.proxy.ClientProxy")
    public static CommonProxy proxy;
    private static final String CLASS_LOC = "com.mcf.davidee.paintinggui.gui.PaintingButton";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModMetadata modMeta = event.getModMetadata();
        modMeta.authorList = Arrays.asList(new String[]{"Subaraki"});
        modMeta.autogenerated = false;
        modMeta.credits = "Subaraki";
        modMeta.description = "More Paintings! Check the config file for options.";
        modMeta.url = "http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1287285-/";

        ConfigurationHandler.instance.loadConfig(event.getSuggestedConfigurationFile());

        loadPaintingGui();
        proxy.registerRenderInformation();
    }

    public void loadPaintingGui() {
        try {
            ResourceLocation loc = new ResourceLocation("subaraki:patterns/" + ConfigurationHandler.instance.texture + ".json");
            InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            Gson gson = new Gson();
            JsonElement je = gson.fromJson(reader, JsonElement.class);
            JsonObject json = je.getAsJsonObject();
            PaintingsPatternLoader loader = gson.fromJson(json, PaintingsPatternLoader.class);
            loader.loadPatterns();
        } catch (IOException e) {
            FMLLog.log.warn(e.getLocalizedMessage());
        }

        try {
            Class altClass = Class.forName(CLASS_LOC);
            paintingGuiTextureHelper(altClass, "TEXTURE", new ResourceLocation("subaraki:art/" + ConfigurationHandler.instance.texture + ".png"));
            paintingGuiHelper(altClass, "KZ_WIDTH", (int) RenderPaintingLate.getSize());
            paintingGuiHelper(altClass, "KZ_HEIGHT", (int) RenderPaintingLate.getSize());
        } catch (Exception e) {
        }
    }

    private void paintingGuiHelper(Class c, String field, int value)
            throws Exception {
        Field f = c.getField(field);
        f.setAccessible(true);
        f.set(null, Integer.valueOf(value));
    }

    private void paintingGuiTextureHelper(Class c, String field, ResourceLocation loc)
            throws Exception {
        Field f = c.getField(field);
        f.setAccessible(true);
        f.set(null, loc);
    }
}
