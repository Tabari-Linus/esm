package lii.employeemanagementsystem.service;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UniqueIdGenerator {
    private static final Set<UUID> usedIds = new HashSet<>();

    public static UUID generateUniqueId() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }

    public static boolean isUsed(UUID id) {
        return usedIds.contains(id);
    }

    public static void reset() {
        usedIds.clear(); // Useful for testing or resetting the system
    }
}
