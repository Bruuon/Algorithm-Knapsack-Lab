import java.io.*;
import java.nio.file.*;

// 在 Pi 生成数据上跑贪心+DP 对比
public class PiExperiment {

    public static void main(String[] args) throws Exception {
        // 先确保数据已生成
        PiDataGenerator.main(args);

        String dir = "data/Pi";
        String[] instances = {
            "UNCORRELATED_S",  "UNCORRELATED_M",  "UNCORRELATED_L",
            "WEAKLY_CORRELATED_S", "WEAKLY_CORRELATED_M", "WEAKLY_CORRELATED_L",
            "STRONGLY_CORRELATED_S", "STRONGLY_CORRELATED_M", "STRONGLY_CORRELATED_L"
        };

        System.out.printf("%-45s %-6s %-12s %-12s %-12s %-10s %-10s\n",
                "Instance", "n", "Capacity", "Greedy", "DP", "DP>Greedy", "Gap(%)");
        System.out.println("-".repeat(110));

        for (String inst : instances) {
            int capacity = Integer.parseInt(
                    readFile(dir + "/" + inst + "_c.txt").get(0).trim());
            int[] wt = readInts(dir + "/" + inst + "_w.txt");
            int[] val = readInts(dir + "/" + inst + "_v.txt");

            // DP（精确解，只求值不提物品，省内存）
            long t1 = System.nanoTime();
            int dpVal = DPKnapsack.solveValueOnly(wt, val, capacity);
            long t2 = System.nanoTime();

            // Greedy（近似解）
            long t3 = System.nanoTime();
            int greedyVal = GreedyKnapsack.solve(wt, val, capacity).totalValue;
            long t4 = System.nanoTime();

            double gap = dpVal > 0 ?
                    (double)(dpVal - greedyVal) / dpVal * 100 : 0;
            System.out.printf("%-45s %-6d %-12d %-12d %-12d %-10d %-9.2f\n",
                    inst, wt.length, capacity,
                    greedyVal, dpVal,
                    dpVal - greedyVal, gap);
            System.out.printf("  Time: DP=%.2fms, Greedy=%.3fms\n",
                    (t2 - t1) / 1e6, (t4 - t3) / 1e6);
        }
    }

    static int[] readInts(String path) throws Exception {
        return Files.lines(Paths.get(path))
                .map(String::trim).filter(s -> !s.isEmpty())
                .mapToInt(Integer::parseInt).toArray();
    }
    static java.util.List<String> readFile(String path) throws Exception {
        return Files.readAllLines(Paths.get(path));
    }
}
