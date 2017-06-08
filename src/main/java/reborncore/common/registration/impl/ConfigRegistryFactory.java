package reborncore.common.registration.impl;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryTarget;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IRegistryFactory.RegistryFactory
public class ConfigRegistryFactory implements IRegistryFactory {

	private static File configDir = null;
	private static HashMap<String, Configuration> configMap = new HashMap<>();

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return ConfigRegistry.class;
	}

	@Override
	public void handleField(Field field) {
		try {
			RebornRegistry rebornRegistry = (RebornRegistry) RegistrationManager.getAnnoation(field.getDeclaringClass().getAnnotations(), RebornRegistry.class);
			ConfigRegistry annotation = (ConfigRegistry) RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);

			Configuration configuration = getOrCreateConfig(annotation, rebornRegistry);

			if (!Modifier.isStatic(field.getModifiers())) {
				throw new RuntimeException("Field " + field.getName() + " must be static");
			}

			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}

			String key = annotation.key();
			if (key.isEmpty()) {
				key = field.getName();
			}
			Object defaultValue = field.get(null);
			Property property = get(annotation.category(), key, defaultValue, annotation.comment(), field.getType(), configuration);
			Object value = getObjectFromType(property, field.getType());
			field.set(null, value);

		} catch (IllegalAccessException e) {
			throw new Error("Failed to load config", e);
		}
	}

	private Object getObjectFromType(Property property, Class<?> type) {
		if (type == String.class) {
			return property.getString();
		}
		if (type == boolean.class) {
			return property.getBoolean();
		}
		if (type == int.class) {
			return property.getInt();
		}
		if (type == double.class) {
			return property.getDouble();
		}
		throw new RuntimeException("Type not supported");
	}

	private Property get(String category, String key, Object defaultValue, String comment, Class<?> type, Configuration configuration) {
		if (type == String.class) {
			return configuration.get(category, key, (String) defaultValue, comment);
		}
		if (type == boolean.class) {
			return configuration.get(category, key, (Boolean) defaultValue, comment);
		}
		if (type == int.class) {
			return configuration.get(category, key, (Integer) defaultValue, comment);
		}
		if (type == double.class) {
			return configuration.get(category, key, (Double) defaultValue, comment);
		}
		throw new RuntimeException("Type not supported: " + type);
	}

	private static Configuration getOrCreateConfig(ConfigRegistry annotation, RebornRegistry rebornRegistry) {
		String configIdent = rebornRegistry.modID();
		if (!annotation.config().isEmpty()) {
			configIdent = configIdent + ":" + annotation.config();
		}
		Configuration configuration;
		if (configMap.containsKey(configIdent)) {
			configuration = configMap.get(configIdent);
		} else {
			File modConfigDir = new File(configDir, rebornRegistry.modID());
			String configName = "config.cfg";
			if (!annotation.config().isEmpty()) {
				configName = annotation.config() + ".cfg";
			}
			configuration = new Configuration(new File(modConfigDir, configName));
			configMap.put(configIdent, configuration);
		}
		return configuration;
	}

	public static void saveAll() {
		for (Map.Entry<String, Configuration> configurationEntry : configMap.entrySet()) {
			configurationEntry.getValue().save();
		}
	}

	public static void setConfigDir(File configDir) {
		ConfigRegistryFactory.configDir = configDir;
	}

	@Override
	public List<RegistryTarget> getTargets() {
		return Collections.singletonList(RegistryTarget.FIELD);
	}
}
