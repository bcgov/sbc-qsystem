/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import java.text.ParseException;
import ru.apertum.qsystem.common.exceptions.ServerException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.awt.Color;
import java.lang.reflect.Type;
import java.util.Date;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;

/**
 *
 * @author egorov
 */
public class GsonPool extends SoftReferenceObjectPool {

    private static class ColorSerializer implements JsonDeserializer<Color>, JsonSerializer<Color> {

        @Override
        public JsonElement serialize(Color arg0, Type arg1, JsonSerializationContext arg2) {
            return new JsonPrimitive(arg0.getRGB());
        }

        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Color(json.getAsInt());
        }
    }

    private static class DateSerializer implements JsonDeserializer<Date>, JsonSerializer<Date> {

        @Override
        public JsonElement serialize(Date arg0, Type arg1, JsonSerializationContext arg2) {
            return new JsonPrimitive(Uses.FORMAT_DD_MM_YYYY_TIME.format(arg0));
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return Uses.FORMAT_DD_MM_YYYY_TIME.parse(json.getAsString());
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
                    gsonb.registerTypeHierarchyAdapter(Date.class, ds);
                    gsonb.registerTypeHierarchyAdapter(Color.class, cs);
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
            throw new ServerException("Проблемы с gson pool. ", ex);
        }
    }

    public void returnGson(Gson gson) {
        try {
            instance.returnObject(gson);
        } catch (Exception ex) {
            throw new ServerException("Проблемы с  gson pool. ", ex);
        }
    }
}
