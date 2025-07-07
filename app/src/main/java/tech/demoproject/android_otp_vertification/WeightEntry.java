package tech.demoproject.android_otp_vertification;

public class WeightEntry {
    private String id;
    private float weight;
    private long timestamp;

    public WeightEntry() {
        // Default constructor required for calls to DataSnapshot.getValue(WeightEntry.class)
    }

    public WeightEntry(String id, float weight) {
        this.id = id;
        this.weight = weight;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public float getWeight() {
        return weight;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
