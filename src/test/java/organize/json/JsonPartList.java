package organize.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class JsonPartList extends JsonPart {

    @SerialClass.SerialField
    public JsonElement list;

    @Override
    public void inject(JsonElement elem) {
        JsonArray dst = elem.getAsJsonArray();
        list.getAsJsonArray().forEach(dst::add);
    }
}
