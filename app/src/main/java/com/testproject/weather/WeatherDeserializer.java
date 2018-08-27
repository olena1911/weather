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

public class WeatherDeserializer implements JsonDeserializer<Weather> {

    @Override
    public Weather deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject weatherJson = json.getAsJsonObject();

        long cityId = weatherJson.get("id").getAsLong();
        String cityName = weatherJson.get("name").getAsString();
        Date time = new Date(weatherJson.get("dt").getAsLong());
        double longitude = weatherJson.getAsJsonObject("coord").get("lon").getAsDouble();
        double latitude = weatherJson.getAsJsonObject("coord").get("lat").getAsDouble();;

        JsonObject mainObject = weatherJson.get("main").getAsJsonObject();
        double temperature = mainObject.get("temp").getAsDouble();
        double pressure = mainObject.get("pressure").getAsDouble();
        double humidity = mainObject.get("humidity").getAsDouble();
        double minTemperature = mainObject.get("temp_min").getAsDouble();
        double maxTemperature = mainObject.get("temp_max").getAsDouble();

        JsonArray weatherArray = weatherJson.get("weather").getAsJsonArray();
        JsonObject weatherObject = weatherArray.get(0).getAsJsonObject();
        String weatherIconId = weatherObject.get("icon").getAsString();

        JsonObject windObject = weatherJson.get("wind").getAsJsonObject();
        double windSpeed = windObject.get("speed").getAsDouble();
        double windDegree = windObject.get("deg").getAsDouble();

        double cloudiness = weatherJson.get("clouds").getAsJsonObject().get("all").getAsDouble();

        Weather weather = new Weather();
        weather.setCityId(cityId);
        weather.setCityName(cityName);
        weather.setTime(time);
        weather.setLongitude(longitude);
        weather.setLatitude(latitude);
        weather.setTemperature(temperature);
        weather.setPressure(pressure);
        weather.setHumidity(humidity);
        weather.setMinTemperature(minTemperature);
        weather.setMaxTemperature(maxTemperature);
        weather.setWindSpeed(windSpeed);
        weather.setWindDegree(windDegree);
        weather.setCloudiness(cloudiness);
        weather.setWeatherIconId(weatherIconId);

        return weather;
    }
}
