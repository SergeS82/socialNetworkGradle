package com.example.springexample.test_classes;

import com.example.springexample.dto.lib.Dto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

class ReadJson<T extends Dto> {
    private int level = 0;

    public void iterate(JsonNode node, Map<String, Object> outMap, String stopField, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ++level;
        ObjectMapper mapper = new ObjectMapper();
        if (node.isArray() && level == 1) {
            ObjectNode newNode = mapper.createObjectNode();
            newNode.set(clazz.getSimpleName(), node);
            iterate(newNode, outMap, stopField, clazz); // рекурсивный вызов для объектов
        }
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                if (entry.getValue().isObject()) {
                    Map<String, Object> subMap = new LinkedHashMap<>();
                    iterate(entry.getValue(), subMap, stopField, clazz); // рекурсивный вызов для объектов
                    outMap.put(entry.getKey(), subMap);
                }
                if (entry.getValue().isObject() && (clazz != null && stopField != null)) {
                    Map<String, Object> subMap = new LinkedHashMap<>();
                    iterate(entry.getValue(), subMap, stopField, clazz); // рекурсивный вызов для объектов
                    outMap.put(entry.getKey(), subMap);
                } else if (entry.getValue().isArray() && (clazz != null && stopField != null)) {
                    List<Object> list = new ArrayList<>();
                    // Преобразование массива в список
                    for (JsonNode arrayItem : entry.getValue()) {
                        if (arrayItem.isObject()) {
                            if (arrayItem.has(stopField)) { // пункти назначения
                                Constructor<T> constructor = clazz.getConstructor();
                                T dto = constructor.newInstance();
                                Map<String, Object> subMap = new LinkedHashMap<>();
                                iterate(arrayItem, subMap, null, null);
                                dto.fillFromMap(subMap);
                                list.add(dto);
                            } else {
                                Map<String, Object> subMap = new LinkedHashMap<>();
                                iterate(arrayItem, subMap, stopField, clazz);
                                list.add(subMap);
                            }
                        } else {
                            list.add(arrayItem.asText()); // Если элемент массива - примитив
                        }
                    }
                    outMap.put(entry.getKey(), list);
                } else {
                    outMap.put(entry.getKey(), entry.getValue().asText()); // Добавление значения в карту
                }
            }
        }
    }
    static class Command<T> {
        private String command;
        private String result;
        private String path;
        private T body;
        private String bodyStr;
    }
}