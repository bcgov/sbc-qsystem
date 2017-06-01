/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import java.text.ParseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.awt.Color;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;

/**
 *
 * @author egorov
 */
public class GsonPool extends SoftReferenceObjectPool {

    private static class ColorSerializer implements JsonDeserializer<Object>, JsonSerializer<Object> {

        public JsonElement serialize(Color color, Type typeOfT, JsonSerializationContext context) {
            return new JsonPrimitive(color.getRGB());
        }

        @Override
        public JsonElement serialize(Object arg0, Type arg1, JsonSerializationContext arg2) {
            final Color color = (Color) arg0;
            return new JsonPrimitive(color.getRGB());
        }

        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Color(json.getAsInt());
        }
    }
    
    /**
     * Формат даты
     */
    protected static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    /**
     * Формат даты.
     */
    protected final static DateFormat format_dd_MM_yyyy_time = new SimpleDateFormat(DATE_FORMAT);

    private static class DateSerializer implements JsonDeserializer<Object>, JsonSerializer<Object> {

        public JsonElement serialize(Date date, Type typeOfT, JsonSerializationContext context) {
            return new JsonPrimitive(format_dd_MM_yyyy_time.format(date));
        }

        @Override
        public JsonElement serialize(Object arg0, Type arg1, JsonSerializationContext arg2) {
            final Date date = (Date) arg0;
            return new JsonPrimitive(format_dd_MM_yyyy_time.format(date));
        }

        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return format_dd_MM_yyyy_time.parse(json.getAsString());
            } catch (ParseException ex) {
                throw new RuntimeException("Not pars JSON by proxy!", ex);
            }
        }
    }

    private GsonPool(BasePoolableObjectFactory basePoolableObjectFactory) {
        super(basePoolableObjectFactory);
    }
    private static GsonPool instance = null;

    public static GsonPool getInstance() {
        if (instance == null) {

            instance = new GsonPool(new BasePoolableObjectFactory() {

                @Override
                public Object makeObject() throws Exception {
                    //return new Gson();
                    //return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

                    final GsonBuilder gsonb = new GsonBuilder();
                    final DateSerializer ds = new DateSerializer();
                    final ColorSerializer cs = new ColorSerializer();
                    gsonb.registerTypeAdapter(Date.class, ds);
                    gsonb.registerTypeAdapter(Color.class, cs);
                    return gsonb.excludeFieldsWithoutExposeAnnotation().create();


                    //return new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
                }
            });

        }
        return instance;
    }

    public Gson borrowGson() {
        try {
            return (Gson) instance.borrowObject();
        } catch (Exception ex) {
            throw new RuntimeException("Проблемы с gson pool. ", ex);
        }
    }

    public void returnGson(Gson gson) {
        try {
            instance.returnObject(gson);
        } catch (Exception ex) {
            throw new RuntimeException("Проблемы с  gson pool. ", ex);
        }
    }
}
