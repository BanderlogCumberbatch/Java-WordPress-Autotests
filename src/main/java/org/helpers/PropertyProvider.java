package org.helpers;

import lombok.Getter;
import java.io.InputStream;
import java.util.Properties;


@Getter
public final class PropertyProvider {

    /**
     * Класс для загрузки properties.
     */
    private final Properties properties = new Properties();

    /**
     * Файл properties. по умолчанию
     */
    private static final String propertyFile = "env_local.properties";

    /**
     * Загрузка .properties-файла.
     * @param propertyFile имя .properties-файла в папке resources
     */
    public PropertyProvider(String propertyFile) {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(propertyFile)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    /**
     * Загрузка .properties-файла по умолчанию (env_local.properties).
     */
    public PropertyProvider() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(propertyFile)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    /**
     * Загрузить параметр из .properties-файла.
     * @param key ключ, для нахождения параметра
     * @return параметр, найденный по ключю
     */
    public String getProperty(final String key) {
        return properties.getProperty(key);
    }
}
