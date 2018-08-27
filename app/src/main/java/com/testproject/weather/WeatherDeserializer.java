package com.testproject.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.testproject.weather.entity.Weather;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Class for Json deserialization of response.
 */
public class WeatherDeserializer implements JsonDeserializer<Weather> {

    @Override
    public Weather deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject weatherJson = json.getAsJsonObject();

        String cityName = weatherJson.get("name").getAsString();

        long time = new Date().getTime();

        JsonObject mainObject = weatherJson.get("main").getAsJsonObject();
        double temperature = mainObject.get("temp").getAsDouble();
        double pressure = mainObject.get("pressure").getAsDouble();
        double humidity = mainObject.get("humidity").getAsDouble();

        JsonArray weatherArray = weatherJson.get("weather").getAsJsonArray();
        JsonObject weatherObject = weatherArray.get(0).getAsJsonObject();
        String weatherIconId = weatherObject.get("icon").getAsString();

        JsonObject windObject = weatherJson.get("wind").getAsJsonObject();
        double windSpeed = windObject.get("speed").getAsDouble();

        Weather weather = new Weather();
        weather.setCityName(cityName);
        weather.setTime(time);
        weather.setTemperature(temperature);
        weather.setPressure(pressure);
        weather.setHumidity(humidity);
        weather.setWindSpeed(windSpeed);
        weather.setWeatherIconId(weatherIconId);

        return weather;
    }
}
