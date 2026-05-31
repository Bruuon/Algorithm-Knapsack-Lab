import java.io.*;
import java.nio.file.*;

// 读 FSU 数据，跑贪心+DP，输出对比
public class Experiment {

    public static void main(String[] args) throws Exception {
        String dataDir = "data/FSU";
        String[] problems = {"p01", "p02", "p03", "p04", "p05", "p06", "p07", "p08"};
        String expDir = "experiments";
        Files.createDirectories(Paths.get(expDir));

        System.out.printf("%-6s %-6s %-12s %-12s %-12s %-10s %-10s\n",
                "Problem", "n", "Capacity", "Greedy", "DP", "Optimal", "Gap(%)");
        System.out.println("-".repeat(80));

        for (String p : problems) {
            // 读数据文件
            int capacity = Integer.parseInt(readFile(dataDir + "/" + p + "_c.txt").get(0).trim());
            int[] wt = readInts(dataDir + "/" + p + "_w.txt");
            int[] val = readInts(dataDir + "/" + p + "_p.txt");
            int[] optSel = readInts(dataDir + "/" + p + "_s.txt");

            int optVal = 0;
            for (int i = 0; i < optSel.length; i++) {
                if (optSel[i] == 1) optVal += val[i];
            }

            // DP
            long t1 = System.nanoTime();
            DPKnapsack.Result dpRes = DPKnapsack.solve(wt, val, capacity);
            long t2 = System.nanoTime();

            // Greedy
            long t3 = System.nanoTime();
            GreedyKnapsack.Result greedyRes = GreedyKnapsack.solve(wt, val, capacity);
            long t4 = System.nanoTime();

            double gap = (double)(optVal - greedyRes.totalValue) / optVal * 100;
            System.out.printf("%-6s %-6d %-12d %-12d %-12d %-10d %-9.2f\n",
                    p.toUpperCase(), wt.length, capacity,
                    greedyRes.totalValue, dpRes.totalValue, optVal, gap);

            // 验证 DP 正确性
            if (dpRes.totalValue != optVal) {
                System.out.println("  [WARN] DP result " + dpRes.totalValue + " != optimal " + optVal);
            }

            System.out.printf("  Time: DP=%.3fms, Greedy=%.3fms\n",
                    (t2 - t1) / 1e6, (t4 - t3) / 1e6);
        }
    }

    static int[] readInts(String path) throws Exception {
        return Files.lines(Paths.get(path))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    static java.util.List<String> readFile(String path) throws Exception {
        return Files.readAllLines(Paths.get(path));
    }
}
