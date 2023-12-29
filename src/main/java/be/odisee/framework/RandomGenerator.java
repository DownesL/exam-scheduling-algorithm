package be.odisee.framework;

import java.util.Random;

public class RandomGenerator {
    Random random;
    public RandomGenerator() {
        random = new Random(2344532L);
    }
    public int[] getRandomIndexes(int size) {
        int supplier = random.nextInt(size);
        int receiver = random.nextInt(size);

        while (supplier == receiver) {
            receiver = random.nextInt(size);
        }

        if (supplier > receiver) {
            int temp = receiver;
            receiver = supplier;
            supplier = temp;
        }
        return new int[]{supplier, receiver};
    }

    public int next(int size) {
        return random.nextInt(size);
    }

}
