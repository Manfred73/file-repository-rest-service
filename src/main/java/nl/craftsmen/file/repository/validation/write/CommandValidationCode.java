package nl.craftsmen.file.repository.validation.write;

public enum CommandValidationCode {

	VR102(CommandValidationCodeDescriptions.VR102_DESCRIPTION),
	VR103(CommandValidationCodeDescriptions.VR103_DESCRIPTION),
	VR104(CommandValidationCodeDescriptions.VR104_DESCRIPTION);

	private final String description;

	CommandValidationCode(String description) {
		this.description = description;
	}

	public String getValidationCode() {
		return name();
	}

	public String getValidationCodeWithDescription(CommandValidationCode commandValidationCode) {
		return getValidationCode() + ": " + commandValidationCode.getDescription();
	}

	public String getDescription() {
		return description;
	}
}
