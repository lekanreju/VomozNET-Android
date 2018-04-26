package com.vomozsystems.apps.android.vomoznet.entity;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by leksrej on 2/25/18.
 */

public class Engine {
    private int image;
    private String name;
    private String title;
    private String fragmentKey;
    private Map<String, String> parameters;
    private Class clazz;
    private Method method;

    public Engine() {

    }

    public Engine(String name, int image, Class clazz, Map<String, String> parameters) {
        this.name = name;
        this.image = image;
        this.parameters = parameters;
        this.clazz = clazz;
    }

    public Engine(String name) {
        this.name = name;
    }

    public Engine(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public Engine(String name, int image, Class clazz) {
        this.name = name;
        this.image = image;
        this.clazz = clazz;
    }

    public Engine(String name, int image, Method method) {
        this.name = name;
        this.image = image;
        this.method = method;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFragmentKey() {
        return fragmentKey;
    }

    public void setFragmentKey(String fragmentKey) {
        this.fragmentKey = fragmentKey;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
