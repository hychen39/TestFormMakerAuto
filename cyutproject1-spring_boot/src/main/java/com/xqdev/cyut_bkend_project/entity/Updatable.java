package com.xqdev.cyut_bkend_project.entity;

import java.lang.reflect.Field;

public interface Updatable {
    default <T> T update(T newInstance) throws IllegalAccessException {
        Class currentClass = this.getClass();
        Field[] fields = currentClass.getDeclaredFields();
        for (Field f: fields){
            if ("id".equals(f.getName())) continue;
            // to access the private fields
            // Ref: https://stackoverflow.com/a/735311/7820390
            f.setAccessible(true);
            f.set(this, f.get(newInstance));
        }

        return (T) this;

    }
}
