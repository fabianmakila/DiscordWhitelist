package fi.fabianadrian.discordwhitelist.common.config.section;

public interface TicketsSection {
	default long categoryIdentifier() {
		return 0;
	}
}
