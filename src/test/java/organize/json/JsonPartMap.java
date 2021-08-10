package organize.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class JsonPartMap extends JsonPart {

    @SerialClass.SerialField
    public JsonElement map;

    @SerialClass.SerialField
    public JsonElement common;

    @Override
    public void inject(JsonElement elem) {
        JsonObject dst = elem.getAsJsonObject();
        map.getAsJsonObject().entrySet().forEach(ent -> {
            dst.add(ent.getKey(), ent.getValue());
            if (common != null) {
                common.getAsJsonObject().entrySet().forEach(e -> {
                    ent.getValue().getAsJsonObject().add(e.getKey(), e.getValue());
                });
            }
        });
    }
}
