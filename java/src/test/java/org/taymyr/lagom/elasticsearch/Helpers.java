package org.taymyr.lagom.elasticsearch;

import akka.util.ByteString;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

public class Helpers {

    public static <TypedRequest> String serializeRequest(TypedRequest data, Class<TypedRequest> clazz) {
        return ServiceCall.serializeRequest(data, clazz).utf8String();
    }

    public static <TypedResponse> TypedResponse deserializeResponse(String data, Class<TypedResponse> clazz) {
        return ServiceCall.deserializeResponse(clazz).invoke(ByteString.fromString(data));
    }

    public static String resourceAsString(String resourceName) {
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
}
