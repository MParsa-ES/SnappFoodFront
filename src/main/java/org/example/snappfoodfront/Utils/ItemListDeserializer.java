package org.example.snappfoodfront.Utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.example.snappfoodfront.model.BuyerDto;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.RestaurantDto;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemListDeserializer implements JsonDeserializer<BuyerDto.ItemList> {

    @Override
    public BuyerDto.ItemList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();


        RestaurantDto.Response vendor = context.deserialize(jsonObject.get("vendor"), RestaurantDto.Response.class);
        List<String> menuTitles = context.deserialize(jsonObject.get("menu_titles"), new TypeToken<List<String>>() {}.getType());


        JsonObject menuTitleObject = jsonObject.getAsJsonObject("menu_title");


        Map<String, List<FoodItemDto.Response>> menuTitleMap = new HashMap<>();
        if (menuTitles != null && menuTitleObject != null) {
            for (String title : menuTitles) {

                if (menuTitleObject.has(title)) {
                    List<FoodItemDto.Response> items = context.deserialize(
                            menuTitleObject.get(title),
                            new TypeToken<List<FoodItemDto.Response>>() {}.getType()
                    );
                    menuTitleMap.put(title, items);
                }
            }
        }


        BuyerDto.ItemList itemList = new BuyerDto.ItemList();
        itemList.setVendor(vendor);
        itemList.setMenu_titles(menuTitles);
        itemList.setMenu_title(menuTitleMap);

        return itemList;
    }
}
