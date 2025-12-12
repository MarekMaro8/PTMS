package com.MarekMaro8.ptms.dto.session;

public class SessionStartDTO {

    // Notatki wstępne (np. "Boli mnie lekko kolano")
    private String notes;

    // Skala 1-10 (1 = Zombie, 10 = Gaz)
    private Integer energyLevel;

    // Skala 1-10 (1 = Tragiczny, 10 = Jak dziecko)
    private Integer sleepQuality;

    // Skala 1-10 (1 = Spokoj, 10 = Panika)
    private Integer stressLevel;

    // Waga w dniu treningu (opcjonalne)
    private Double bodyWeight;

    public SessionStartDTO() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(Integer energyLevel) {
        this.energyLevel = energyLevel;
    }

    public Integer getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(Integer sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public Integer getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(Integer stressLevel) {
        this.stressLevel = stressLevel;
    }

    public Double getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(Double bodyWeight) {
        this.bodyWeight = bodyWeight;
    }
}
