package nl.craftsmen.file.repository.validation.shared;

import nl.craftsmen.file.repository.validation.read.QueryValidationCodeDescriptions;

public enum SharedValidationCode {

	VR001(QueryValidationCodeDescriptions.VR001_DESCRIPTION);

	private final String description;

	SharedValidationCode(String description) {
		this.description = description;
	}

	public String getValidationCode() {
		return name();
	}

	public String getValidationCodeWithDescription(SharedValidationCode queryValidationCode) {
		return getValidationCode() + ": " + queryValidationCode.getDescription();
	}

	public String getDescription() {
		return description;
	}
}
