package com.example.springexample.test_classes;

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

class ReadJsonV2 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Logger logger = LoggerFactory.getLogger(ReadJsonV2.class);
        File mainDataFile = new File(Paths.get(ReadJsonV2.class.getResource("/test_data/author_data_new.json").toURI()).toRealPath().toString());
        logger.info(mainDataFile.getAbsolutePath()+" qwerty");
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode rootNode = jsonMapper.readTree(mainDataFile);
        ReadJsonV2 dto = new ReadJsonV2();
        dto.read(rootNode);
    }

    private static final Logger logger = LoggerFactory.getLogger(ReadJsonV2.class);
    private int level = 0;

    Command command = null;
    List<Command> commands = new ArrayList<>();

    public ReadJsonV2() {
        command = new Command();
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
                        String className = "com.example.springexample.dto.".concat(name.replaceFirst("^\\$class-", ""));
                        command.setClazz(className);
                    } else if (name.startsWith("$cmd-")) {
                        command.setCommand(name.replace("$cmd-", "").toLowerCase(Locale.ROOT));
                    } else if (name.startsWith("$return-")) {
                        command.setResult(name.replace("$return-", "").toLowerCase(Locale.ROOT));
                    } else if (name.startsWith("$path-")) {
                        command.setPath(name.replace("$path-",""));
                    } else if (name.startsWith("$dto-body")) { // тело всегда идёт в конце
                        command.setBody(node_);
                        commands.add(command);
                        command = new Command();
                        return;
                    }
                } catch (NewCommandException ex) {
                    throw new IllegalStateException("Нарушена структура файла: ");
                } catch (ClassCreateException ex) {
                    throw new ClassCastException(ex.getMessage());
                }
                iterate(node_);
            }
        }
    }

    static class Command {

        private Class<? extends Dto> clazz = null;
        private String command;
        private String result;
        private String path;
        private Dto body;
        private String bodyStr;

        public void setClazz(String clazz) throws NewCommandException, ClassCreateException {
            if (this.clazz == null ) {
                try {
                    this.clazz = (Class<? extends Dto>) Class.forName(clazz);
                } catch (ClassNotFoundException e) {
                    throw new ClassCreateException("Не найден класс "+clazz);
                }
            } else {
                throw new NewCommandException();
            }
        }

        public void setCommand(String command) throws NewCommandException {
            if (this.command == null) {
                this.command = command;
            } else {
                throw new NewCommandException();
            }
        }

        public void setResult(String result) throws NewCommandException {
            if (this.result == null) {
                this.result = result;
            } else {
                throw new NewCommandException();
            }
        }

        public void setPath(String path) throws NewCommandException {
            if (this.path == null) {
                this.path = path;
            } else {
                throw new NewCommandException();
            }

        }

        public void setBody(JsonNode node) throws NewCommandException, ClassCreateException {
            if (this.body == null) {
                try {
                    Constructor<? extends Dto> constructor = clazz.getConstructor();
                    Dto dto = constructor.newInstance();
                    Map<String, Object> subMap = new LinkedHashMap<>();
                    node.fields().forEachRemaining(entry -> subMap.put(entry.getKey(), entry.getValue().asText()));
                    dto.fillFromMap(subMap);
                    this.body = dto;
                } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    throw new ClassCreateException("Ошибка создания body: ".concat(e.getClass().getSimpleName()).concat(e.getMessage()));
                }
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

    // Нужное поле уже заполнено в объекте command, а следовательно пошло наполрнение нового элемента
    static class NewCommandException extends Exception {
        public NewCommandException() {
        }
        public NewCommandException(String message) {
            super(message);
        }
    }

    // Отсутсвует нужный класс в директории com.example.springexample.dto
    static class ClassCreateException extends Exception {
        public ClassCreateException() {
        }
        public ClassCreateException(String message) {
            super(message);
        }
    }

}