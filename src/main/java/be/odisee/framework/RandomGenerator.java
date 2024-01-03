package be.odisee.framework;

import java.util.Random;

public class RandomGenerator {
    Random random;
    public RandomGenerator() {
        random = new Random(23432L);
    }
    public RandomGenerator(long seed) {
        random = new Random(seed);
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
