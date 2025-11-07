package xyz.sadiulhakim.enumeration;

public enum JobStatus {

    UNDEFINED("", 0),
    NEW("New", 1),
    IN_PROGRESS("In Progress", 2),
    FINISHED("Finished", 3),
    KILLED("Killed", 4),
    FAILED("Failed", 5),
    CANCELED("Canceled", 6);

    private final int id;
    private final String name;

    JobStatus(String name, int id) {
        this.id = id;
        this.name = name;
    }


    public static JobStatus getById(int id) {
        for (JobStatus item : JobStatus.values()) {
            if (item.getId() == id)
                return item;
        }
        return FINISHED;
    }

    public static JobStatus getByName(String name) {
        for (JobStatus item : JobStatus.values()) {
            if (item.getName().equalsIgnoreCase(name))
                return item;
        }
        return FINISHED;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
