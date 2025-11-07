package xyz.sadiulhakim.enumeration;

public enum ExecutionType {

    UNKNOWN(0, ""),
    SCHEDULED(1, "Scheduled"),
    MANUAL(2, "Manual");

    private final int id;
    private final String name;


    ExecutionType(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public static ExecutionType getById(int id) {
        for (ExecutionType item : ExecutionType.values()) {
            if (item.getId() == id)
                return item;
        }
        return SCHEDULED;
    }

    public static ExecutionType getByName(String name) {
        for (ExecutionType item : ExecutionType.values()) {
            if (item.getName().equalsIgnoreCase(name))
                return item;
        }
        return SCHEDULED;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
