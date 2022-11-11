package nl.craftsmen.file.repository.validation.read;

public enum QueryValidationCode {

	VR002(QueryValidationCodeDescriptions.VR002_DESCRIPTION),
	VR003(QueryValidationCodeDescriptions.VR003_DESCRIPTION);

	private final String description;

	QueryValidationCode(String description) {
		this.description = description;
	}

	public String getValidationCode() {
		return name();
	}

	public String getValidationCodeWithDescription(QueryValidationCode queryValidationCode) {
		return getValidationCode() + ": " + queryValidationCode.getDescription();
	}

	public String getDescription() {
		return description;
	}
}
