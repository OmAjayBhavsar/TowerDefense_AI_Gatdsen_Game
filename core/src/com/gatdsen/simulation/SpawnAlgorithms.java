package com.gatdsen.simulation;

public class SpawnAlgorithms {
    static int getSpawnLevel(int wave, int id) {
        switch (id) {
            case 12:
                return campaign1_2(wave);
            default:
                return defaultAlgorithm(wave);
        }
    }

    private static int campaign1_2(int wave) {
        int enemyLevel;
        if (wave % 10 == 0) enemyLevel = wave / 10 + 1;
        else enemyLevel = 1 + wave / 20;
        return enemyLevel;
    }

    private static int defaultAlgorithm(int wave) {
        int enemyLevel;
        if (wave % 10 == 0) enemyLevel = wave / 2;
        else if (wave % 5 == 0) enemyLevel = wave / 5 + 1;
        else enemyLevel = 1 + wave / 20;
        return enemyLevel;
    }
}
