import java.io.*;
import java.nio.file.*;

// 在 JJ 数据集上跑贪心+DP 对比
public class JJExperiment {

    public static void main(String[] args) throws Exception {
        String base = "data/JJ/problemInstances";
        String optimaPath = "data/JJ/optima.csv";

        // 读最优值表
        var optima = new java.util.HashMap<String, Integer>();
        for (String line : Files.readAllLines(Paths.get(optimaPath))) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                try { optima.put(parts[0].trim(), Integer.parseInt(parts[1].trim())); }
                catch (NumberFormatException e) { }
            }
        }

        // 6 个代表性实例：3种f值 × 2种规模（n=400/1000, c=1M）
        String[] instances = {
            "n_400_c_1000000_g_10_f_0.1_eps_0_s_100",
            "n_400_c_1000000_g_10_f_0.2_eps_0_s_100",
            "n_400_c_1000000_g_10_f_0.3_eps_0_s_100",
            "n_1000_c_1000000_g_10_f_0.1_eps_0_s_100",
            "n_1000_c_1000000_g_10_f_0.2_eps_0_s_100",
            "n_1000_c_1000000_g_10_f_0.3_eps_0_s_100",
        };

        System.out.printf("%-40s %-6s %-10s %-10s %-10s %-7s %-8s\n",
                "Instance", "n", "Capacity", "Greedy", "DP", "Gap(%)", "DP Time");
        System.out.println("-".repeat(95));

        for (String inst : instances) {
            String dir = base + "/" + inst;
            var lines = Files.readAllLines(Paths.get(dir + "/test.in"));

            int n = Integer.parseInt(lines.get(0).trim());
            int[] wt = new int[n];
            int[] val = new int[n];
            for (int i = 0; i < n; i++) {
                String[] p = lines.get(i + 1).trim().split("\\s+");
                val[i] = Integer.parseInt(p[1]);  // p[1]=profit
                wt[i] = Integer.parseInt(p[2]);   // p[2]=weight
            }
            int cap = Integer.parseInt(lines.get(lines.size() - 1).trim());

            String shortName = inst.replaceAll(
                    "n_(\\d+)_c_(\\d+)_g_\\d+_f_([\\d.]+)_eps_0_s_100",
                    "n=$1,c=1M,f=$3");

            // Greedy
            long t1 = System.nanoTime();
            int greedyVal = GreedyKnapsack.solve(wt, val, cap).totalValue;
            long t2 = System.nanoTime();

            // DP
            long t3 = System.nanoTime();
            int dpVal = DPKnapsack.solveValueOnly(wt, val, cap);
            long t4 = System.nanoTime();

            int bestVal = dpVal > 0 ? dpVal : optima.getOrDefault(inst, -1);
            double gap = bestVal > 0 ? (double)(bestVal - greedyVal) / bestVal * 100 : 0;

            System.out.printf("%-40s %-6d %-10d %-10d %-10d %-6.2f %-8s\n",
                    shortName, n, cap, greedyVal, bestVal, gap,
                    String.format("%.0fms", (t4 - t3) / 1e6));
            System.out.printf("  Greedy: %.2fms\n", (t2 - t1) / 1e6);
        }

        System.out.println("\n注：JJ数据集中c=100M和c=10B的实例容量过大，");
        System.out.println("  DP分别需~2min和超出int范围(>21亿)，此处仅选c=1M作为代表。");
    }
}
