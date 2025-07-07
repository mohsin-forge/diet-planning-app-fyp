package tech.demoproject.android_otp_vertification;

public class UserProfile {
    private String age;
    private String gender;
    private String weight;
    private String height;
    private String activityLevel;
    private String dietaryRestrictions;
    private String goals;
    private boolean keepAgePrivate;
    private boolean keepWeightPrivate;
    private boolean keepHeightPrivate;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }

    public UserProfile(String age, String gender, String weight, String height, String activityLevel,
                       String dietaryRestrictions, String goals, boolean keepAgePrivate,
                       boolean keepWeightPrivate, boolean keepHeightPrivate) {
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.activityLevel = activityLevel;
        this.dietaryRestrictions = dietaryRestrictions;
        this.goals = goals;
        this.keepAgePrivate = keepAgePrivate;
        this.keepWeightPrivate = keepWeightPrivate;
        this.keepHeightPrivate = keepHeightPrivate;
    }

    // Getters and setters
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public boolean isKeepAgePrivate() {
        return keepAgePrivate;
    }

    public void setKeepAgePrivate(boolean keepAgePrivate) {
        this.keepAgePrivate = keepAgePrivate;
    }

    public boolean isKeepWeightPrivate() {
        return keepWeightPrivate;
    }

    public void setKeepWeightPrivate(boolean keepWeightPrivate) {
        this.keepWeightPrivate = keepWeightPrivate;
    }

    public boolean isKeepHeightPrivate() {
        return keepHeightPrivate;
    }

    public void setKeepHeightPrivate(boolean keepHeightPrivate) {
        this.keepHeightPrivate = keepHeightPrivate;
    }
}
