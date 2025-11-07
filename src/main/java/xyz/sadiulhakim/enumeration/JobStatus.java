package xyz.sadiulhakim.enumeration;

public enum JobStatus {

    UNKNOWN("", 0),
    NEW("New", 1),
    IN_PROGRESS("In Progress", 2),
    FINISHED("Finished", 3),
    KILLED("Killed", 4),
    FAILED("Failed", 5),
    CANCELED("Canceled", 6),
    DOWNLOADING_FILE("Downloading File", 7),
    DOWNLOADING_DONE("Downloading Done", 8),
    PROCESSING_FILE("Processing File", 9),
    PROCESSING_DONE("Processing Done", 10),
    SAVING_DATA("Saving Data", 11),
    SAVING_DONE("Saving Done", 12),
    LOADING_DATA("Loading Data", 13),
    TRANSFERRING_DATA("Transferring Data", 14);


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
