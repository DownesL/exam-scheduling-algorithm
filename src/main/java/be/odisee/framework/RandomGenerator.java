package be.odisee.framework;

import java.util.*;

public class RandomGenerator {
    Random random;
    public RandomGenerator() {
        random = new Random(2344532L);
    }
    public int[] getTwoRandomIndexes(int size) {
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

    public int[] getRandomIndexes(int size) {
        List<Integer> arr = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            arr.add(i);
        }
        Collections.shuffle(arr);
        return arr.stream().mapToInt(value -> value).toArray();
    }

    public int next(int size) {
        return random.nextInt(size);
    }

}
