package com.plank.meds_and_herbs.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.plank.meds_and_herbs.MedsAndHerbs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nonnull;
import java.util.*;

public class MedicineTypeLoader extends SimpleJsonResourceReloadListener {
    public static final MedicineTypeLoader INSTANCE = new MedicineTypeLoader();
    private static final Map<ResourceLocation, MedicineDefinition> definitions = new HashMap<>();

    private MedicineTypeLoader() {
        super(new GsonBuilder().setPrettyPrinting().create(), "medicine_types");
    }

    @Override
    protected void apply(@Nonnull Map<ResourceLocation, JsonElement> entries, @Nonnull ResourceManager manager, @Nonnull ProfilerFiller profiler) {
        definitions.clear();
        parseEntries(entries);
    }

    private static void parseEntries(Map<ResourceLocation, JsonElement> entries) {
        for (var entry : entries.entrySet()) {
            try {
                var json = entry.getValue().getAsJsonObject();
                var id = entry.getKey();
                var nameKey = json.get("name").getAsString();
                MedicineDefinition.Type type = MedicineDefinition.Type.valueOf(json.get("type").getAsString().toUpperCase(Locale.ROOT));
                var color = json.has("color") ? parseColor(json.get("color").getAsString()) : 0xFFFFFFFF;
                var functionPath = ResourceLocation.parse(json.get("function").getAsString());
                List<ResourceLocation> cures = parseCures(json.getAsJsonArray("cures"));
                var def = new MedicineDefinition(id, nameKey, type, color, functionPath, cures);
                definitions.put(id, def);
            } catch (Exception e) {
                MedsAndHerbs.LOGGER.error("Failed to load medicine type {}", entry.getKey(), e);
            }
        }
    }

    private static int parseColor(String colorStr) {
        if (colorStr.startsWith("#")) {
            colorStr = colorStr.substring(1);
        }
        int rgb = Integer.parseInt(colorStr, 16);
        return 0xFF000000 | rgb;
    }

    private static List<ResourceLocation> parseCures(JsonArray array) {
        List<ResourceLocation> list = new ArrayList<>(array.size());
        for (var elem : array) {
            list.add(ResourceLocation.parse(elem.getAsString()));
        }
        return list;
    }

    public static MedicineDefinition get(ResourceLocation id) {
        return definitions.get(id);
    }

    public static Collection<MedicineDefinition> getAllDefinitions() {
        return definitions.values();
    }
}