package com.example.springexample.test_classes;

import com.example.springexample.dto.lib.Dto;
import com.example.springexample.dto.lib.DtoIdMixin;
import com.example.springexample.entity.lib.Entity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.NotNull;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TestEntity<N extends Entity, T extends Dto> {
    private static final Logger logger = LoggerFactory.getLogger(TestEntity.class);
    private final File mapperFile;
    private final Map<String, String> mapping = new HashMap<>();
    private final ObjectMapper mapper;
    private final ObjectMapper dtoMapper;
    private final WebApplicationContext webApplicationContext;
    private final Map<String, Object> data = new LinkedHashMap<>();
    private HashMap<Class<?>, Map<String, String>> fkMap = new HashMap<>();

    public TestEntity(String pathToResource, @NotNull Class<T> dtoClass, @NotNull CrudRepository<N, Long> repository, @NotNull WebApplicationContext webApplicationContext) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, URISyntaxException {
        this.webApplicationContext = webApplicationContext;
        this.mapper = new ObjectMapper();
        this.dtoMapper = new ObjectMapper().addMixIn(dtoClass, DtoIdMixin.class);
        String dtoClassName = dtoClass.getSimpleName();
        pathToResource = Optional.ofNullable(pathToResource).orElse("/test_data/data.json");
        File mainDataFile = new File(Paths.get(getClass().getResource(pathToResource).toURI()).toRealPath().toString());
        Path pathToMapper = mainDataFile.toPath().toRealPath().getParent();
        pathToMapper = pathToMapper.resolve(dtoClassName + "_mapper.json");
        this.mapperFile = new File(pathToMapper.toUri());
        //ToDo jsonMapper заменить на mapper
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode rootNode = jsonMapper.readTree(mainDataFile);
        JsonNode targetNode = rootNode.path(dtoClassName);
        if (targetNode.isMissingNode()) {
            throw new IllegalStateException(String.format("Файл %s не содержит данные в узле %s", mainDataFile.getName(), dtoClass));
        }
        ReadJson<T> readJson = new ReadJson<>();
        readJson.iterate(targetNode, data, "id", dtoClass);
        //System.out.println(rows);
    }

    private <K, V> V putAndReturnValue(Map<K, V> map, K key, V value) {
        map.put(key, value);
        return value;
    }

    private Map<String, String> mapingByClass(Class<?> fkClass) throws IOException {
        Path path = mapperFile.toPath().getParent().resolve(fkClass.getSimpleName() + "_mapper.json");
        File file = new File(path.toUri());
        Map<String, String> result = mapper.readValue(file, Map.class);
        return result;
    }

    public void postAll(String url) throws IOException, IllegalAccessException {
        clearMapping();
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        for (var root : data.entrySet()) {
            logger.info(root.getKey().toString()); //POST
            for (var command : castToMap(root.getValue()).entrySet()) {
                for (T dto : (List<T>) command.getValue()) {
                    if (root.getKey().equals("POST")) {
                        logger.info(dto.toString());
                        var oldId = dto.getId();
                        String inContent = makeInContent(dto);
                        ResultActions resultActions = null;
                        try {
                            resultActions = mockMvc.perform(MockMvcRequestBuilders
                                    .post(url)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(inContent));
                            String outContent = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
                            String newId = getNewId(outContent);
                            addMapping(oldId, newId);
                        } catch (Exception ex) {
                        } finally {
                            Assertions.assertEquals(command.getKey(), (resultActions != null) ? String.valueOf(resultActions.andReturn().getResponse().getStatus()) : "000");
                        }
                    }
                }
            }
        }

        writeMapperToFile();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object obj) {
        if (obj instanceof Map<?, ?>) {
            return (Map<String, Object>) obj;
        } else {
            throw new ClassCastException("Object can't be cast to Map<String, Object>");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> castToList(Object obj) {
        return (List<Object>) obj;
    }

    @SuppressWarnings("unchecked")
    private List<T> castToListT(Object obj) {
        return (List<T>) obj;
    }

    public void clearMapping() {
        mapping.clear();
    }

    ;

    public void addMapping(String oldId, String newId) {
        mapping.put(oldId, newId);
    }

    public void writeMapperToFile() throws IOException {
        mapper.writeValue(mapperFile, mapping);
    }

    public String getOldId(T dto) throws JsonProcessingException {
        JsonNode inJson = mapper.readTree(mapper.writeValueAsString(dto));
        return inJson.get("id").asText();
    }

    public String getNewId(String outContent) throws JsonProcessingException {
        return mapper.readTree(outContent).get("id").asText();
    }

    public String makeInContent(T dto) throws IOException, IllegalAccessException {
        //ToDo пройти по полям dto объекта и если значение начинается с fk-, то заменить на соответсвующий ключ из мапинга
        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String value = field.get(dto).toString();
            if (value.startsWith("fk-")) {
                String[] split = value.split("-");
                String fkClassName = "com.example.springexample.dto." + split[1];
                Class<?> fkClass = null;
                try {
                    fkClass = Class.forName(fkClassName);
                } catch (ClassNotFoundException e) {
                    throw new ClassCastException("Не найден класс: " + fkClassName);
                }
                var mapping = Optional.ofNullable(fkMap.get(fkClass)).orElse(putAndReturnValue(fkMap, fkClass, mapingByClass(fkClass)));
                if (mapping == null) {
                    throw new FileNotFoundException("Не найден файл " + fkClass.getSimpleName() + "_mapper.json");
                }
                // запись результата
                field.set(dto, mapping.get(split[2]));
            }
        }
        return dtoMapper.writeValueAsString(dto);
    }

    private Map<String, String> getMapping() {
        return mapping;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
