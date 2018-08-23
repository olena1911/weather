package com.testproject.weather;

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

        JsonObject windObject = weatherJson.get("wind").getAsJsonObject();
        double windSpeed = windObject.get("speed").getAsDouble();
        double windDegree = windObject.get("deg").getAsDouble();

        double cloudiness = weatherJson.get("clouds").getAsJsonObject().get("all").getAsDouble();

        Weather weatherObject = new Weather();
        weatherObject.setCityId(cityId);
        weatherObject.setCityName(cityName);
        weatherObject.setTime(time);
        weatherObject.setLongitude(longitude);
        weatherObject.setLatitude(latitude);
        weatherObject.setTemperature(temperature);
        weatherObject.setPressure(pressure);
        weatherObject.setHumidity(humidity);
        weatherObject.setMinTemperature(minTemperature);
        weatherObject.setMaxTemperature(maxTemperature);
        weatherObject.setWindSpeed(windSpeed);
        weatherObject.setWindDegree(windDegree);
        weatherObject.setCloudiness(cloudiness);

        return weatherObject;
    }
}
