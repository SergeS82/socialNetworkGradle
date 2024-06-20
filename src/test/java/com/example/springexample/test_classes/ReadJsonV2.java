package com.example.springexample.test_classes;

import com.example.springexample.dto.*;
import com.example.springexample.dto.lib.Dto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;

class ReadJsonV2<T extends Dto> {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Logger logger = LoggerFactory.getLogger(ReadJsonV2.class);
        File mainDataFile = new File(Paths.get(ReadJsonV2.class.getResource("/test_data/author_data_new.json").toURI()).toRealPath().toString());
        logger.info(mainDataFile.getAbsolutePath()+" qwerty");
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode rootNode = jsonMapper.readTree(mainDataFile);
        ReadJsonV2<AuthorDto> dto = new ReadJsonV2<>(AuthorDto.class);
        dto.read(rootNode);
    }

    private static final Logger logger = LoggerFactory.getLogger(ReadJsonV2.class);
    private int level = 0;
    private Class<T> clazz;
    Command<T> command = null;
    List<Command<AuthorDto>> commands = new ArrayList<>();

    public ReadJsonV2(Class<T> clazz) {
        this.clazz = clazz;
        command = new Command<T>(clazz);
    }

    public void read(JsonNode node) {
        iterate(node);
    }

    // итерации для сборки списка объектов типа Command<T>
    private void iterate(JsonNode node) {
        int index = commands.size() + 1;
        ++level;
        ObjectMapper mapper = new ObjectMapper();
        if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                iterate(arrayItem);
            }
        } else {
            Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> field = iterator.next();
                String name = field.getKey();
                JsonNode node_ = field.getValue();
                try {
                    if (name.startsWith("$class-")) {

                    } else if (name.startsWith("$cmd-")) {
                        command.setCommand(name.replace("$cmd-", "").toLowerCase(Locale.ROOT));
                    } else if (name.startsWith("$return-")) {
                        command.setResult(name.replace("$return-", "").toLowerCase(Locale.ROOT));
                    } else if (name.startsWith("$path-")) {
                        command.setPath(name.replace("$path-",""));
                    } else if (name.startsWith("$dto-body")) {
                        //command.setBody(node_);
                    }
                } catch (NewCommandException ex) {

                }
            }
        }
    }

    static class Command<T extends Dto> {
        private final Class<T> clazz;
        private String command;
        private String result;
        private String path;
        private T body;
        private String bodyStr;

        public Command(Class<T> clazz) {
            this.clazz = clazz;
        }



        public void setCommand(String command) throws NewCommandException {
            if (this.command.isEmpty()) {
                this.command = command;
            } else {
                throw new NewCommandException();
            }
        }

        public void setResult(String result) throws NewCommandException {
            if (this.result.isEmpty()) {
                this.result = result;
            } else {
                throw new NewCommandException();
            }
        }

        public void setPath(String path) throws NewCommandException {
            if (this.path.isEmpty()) {
                this.path = path;
            } else {
                throw new NewCommandException();
            }

        }

        public void setBody(JsonNode node) throws NewCommandException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            if (this.body == null) {
                Constructor<T> constructor = clazz.getConstructor();
                T dto = constructor.newInstance();
                Map<String, Object> subMap = new LinkedHashMap<>();
                node.fields().forEachRemaining(entry -> subMap.put(entry.getKey(), entry.getValue().asText()));
                dto.fillFromMap(subMap);
                this.body = dto;
            } else {
                throw new NewCommandException();
            }
        }

        public void setBodyStr(String bodyStr) throws NewCommandException {
            if (this.bodyStr.isEmpty()) {
                this.bodyStr = bodyStr;
            } else {
                throw new NewCommandException();
            }
        }
    }

    static class NewCommandException extends Exception {
        public NewCommandException() {
        }

        public NewCommandException(String message) {
            super(message);
        }
    }
}