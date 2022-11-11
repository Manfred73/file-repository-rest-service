package nl.craftsmen.file.repository.model;

import java.util.HashMap;
import java.util.Map;

public enum FileStatus {
	RECEIVED("File has been received and saved"),
	APPROVED("File has been successfully validated"),
	REJECTED("File has not been successfully validated"),
	PROCESSED("File has been successfully processed"),
	INCOMPLETELY_PROCESSED("File has not been successfully processed");

	FileStatus(String value) {
		FileStatus.TypeValueMaps.VALUE_MAP.put(value, this);
		FileStatus.TypeValueMaps.TYPE_MAP.put(this, value);
	}

	public static FileStatus getFileStatusByDescription(String value) {
		return value != null ? TypeValueMaps.VALUE_MAP.get(value) : null;
	}

	public static String getDescriptionByFileStatus(FileStatus type) {
		return TypeValueMaps.TYPE_MAP.get(type);
	}

	public static boolean isValid(String value) {
		return value == null || getFileStatusByDescription(value) != null;
	}

	@SuppressWarnings("java:S1640" /* Maps with keys that are enum values should be replaced with EnumMap */)
	private static class TypeValueMaps {
		private static final Map<String, FileStatus> VALUE_MAP = new HashMap<>();
		private static final Map<FileStatus, String> TYPE_MAP = new HashMap<>();
	}
}
