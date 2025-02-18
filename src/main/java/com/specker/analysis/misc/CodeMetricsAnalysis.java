package com.specker.analysis.misc;

import pascal.taie.World;
import pascal.taie.analysis.ProgramAnalysis;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.proginfo.MethodResolutionFailedException;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.language.classes.JClass;
import pascal.taie.language.classes.JMethod;
import pascal.taie.util.collection.Maps;

import java.util.Map;

public class CodeMetricsAnalysis extends ProgramAnalysis {

    public static final String ID = "code-metrics";

    // 用于存储方法调用频率
    private final Map<JMethod, Integer> methodCallFrequency = Maps.newMap();

    // 用户代码和第三方库代码的方法计数
    private int userMethodCount = 0;
    private int libMethodCount = 0;

    public CodeMetricsAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public Object analyze() {
        // 1. 统计方法数量
        countMethods();

        // 2. 统计方法调用频率
        analyzeMethodCalls();

        // 3. 输出结果
        printResults();

        return null;
    }

    private void countMethods() {
        for (JClass jClass : World.get().getClassHierarchy().allClasses().toList()) {
            boolean isUserCode = isUserCode(jClass);

            for (JMethod method : jClass.getDeclaredMethods()) {
                if (isUserCode) {
                    userMethodCount++;
                } else {
                    libMethodCount++;
                }
            }
        }
    }

    private void analyzeMethodCalls() {
        for (JClass jClass : World.get().getClassHierarchy().allClasses().toList()) {
            if (isUserCode(jClass)) {
                for (JMethod method : jClass.getDeclaredMethods()) {
                    if (!method.isAbstract() && method.getIR() != null) {
                        analyzeMethodBody(method);
                    }
                }
            }
        }
    }

    private void analyzeMethodBody(JMethod method) {
        if (method.getIR() == null)
            return;
        method.getIR().forEach(stmt -> {
            if (stmt instanceof Invoke invoke && !invoke.isDynamic()) {
                try {
                    JMethod callee = invoke.getMethodRef().resolve();
                    if (callee != null && !isUserCode(callee.getDeclaringClass())) {
                        methodCallFrequency.merge(callee, 1, Integer::sum);
                    }
                } catch (MethodResolutionFailedException e) {
                    // ignore
                }

            }
        });
    }

    private boolean isUserCode(JClass jClass) {
        // 可以根据具体项目调整判断条件
        String packageName = jClass.getName();
        return packageName!=null && packageName.startsWith("org.apache.logging.log4j.core");
//        return !packageName.startsWith("java.")
//            && !packageName.startsWith("javax.")
//            && !packageName.startsWith("sun.")
//            && !packageName.startsWith("com.sun.")
//            && !packageName.startsWith("org."); // 可以添加更多第三方库的包名前缀
    }

    private void printResults() {
        int totalMethods = userMethodCount + libMethodCount;

        System.out.println("\n=== Code Metrics Analysis Results ===");
        System.out.println("Total Methods: " + totalMethods);
        System.out.printf("User Code Methods: %d (%.2f%%)\n",
            userMethodCount, (userMethodCount * 100.0 / totalMethods));
        System.out.printf("Library Methods: %d (%.2f%%)\n",
            libMethodCount, (libMethodCount * 100.0 / totalMethods));

        System.out.println("\nTop 10 Most Called Library Methods:");
        methodCallFrequency.entrySet().stream()
            .sorted(Map.Entry.<JMethod, Integer>comparingByValue().reversed())
            .limit(20)
            .forEach(entry -> {
                JMethod method = entry.getKey();
                int frequency = entry.getValue();
                System.out.printf("%s.%s: %d calls\n",
                    method.getDeclaringClass().getName(),
                    method.getName(),
                    frequency);
            });
    }
}
