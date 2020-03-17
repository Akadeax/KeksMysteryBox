package me.akadeax.mysterybox.util;

import me.akadeax.mysterybox.reward.Reward;

import java.util.List;

public class MathUtil {

    public static Reward getRandomWeighted(List<Reward> rewardsList) {
        Reward[] items = rewardsList.toArray(new Reward[0]);

        // Compute the total weight of all items together
        double totalWeight = 0.0d;
        for (Reward i : items)
        {
            totalWeight += i.weight;
        }
        // Now choose a random item
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < items.length; ++i)
        {
            random -= items[i].weight;
            if (random <= 0.0d)
            {
                randomIndex = i;
                break;
            }
        }

        return items[randomIndex];
    }
}
