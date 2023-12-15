package model.objects;

import java.util.Random;

public class HealthcareSensor {
    private double temperatureValue;    //body temperature
    private double oxygenValue;
    private double BPMValue;
    private Random rnd;

    public HealthcareSensor(double temperatureValue, double oxygenValue, double BPMValue) {
        this.rnd = new Random();
        this.temperatureValue = temperatureValue;
        this.oxygenValue = oxygenValue;
        this.BPMValue = BPMValue;
    }

    /**TODO: aggiungere dei valori sensati
     * **/
    private double generateTemperatureValue() {
        // Range tipico per la temperatura corporea umana: 36.0°C - 37.5°C
        return 36.0 + rnd.nextDouble() * (37.5 - 36.0);
    }

    private double generateOxygenValue() {
        // Range tipico per il livello di ossigeno nel sangue: 95% - 99%
        return 95 + rnd.nextDouble() * (99 - 95);
    }

    private int generateBPMValue() {
        // Range tipico per il battito cardiaco: 60 BPM - 100 BPM
        return 60 + rnd.nextInt()*(100 - 60); // generazione di valori interi tra 60 e 100 inclusi
    }

    private void generateValues() {
        double temperatureValue = generateTemperatureValue();
        double oxygenValue = generateOxygenValue();
        int BPMValue = generateBPMValue();
    }

    public double getTemperatureValue() {
        generateTemperatureValue();
        return temperatureValue;
    }

    public double getOxygenValuealue() {
        generateOxygenValue();
        return oxygenValue;
    }

    public double getBPMValue() {
        generateBPMValue();
        return BPMValue;
    }

}
