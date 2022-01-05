package com.xxw.util.base.util.junit;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 核心对象：
 * RunNotifier
 * Result
 * RunListener
 * Runner ParentRunner BlockJUnit4ClassRunner
 * Filter
 * Sorter
 *
 */
public class BlockJUnit4ClassRunnerExecutor {
    public static void main(String[] args) {
        RunNotifier notifier = new RunNotifier();
        Result result = new Result();
        notifier.addFirstListener(result.createListener());
        notifier.addListener(new LogRunListener());

        Runner runner = null;
        try {
            runner = new BlockJUnit4ClassRunner(CoreJUnit4SampleTest.class);
//            runner = new SpringJUnit4ClassRunner(CoreJUnit4SampleTest.class);
            try {
                ((BlockJUnit4ClassRunner) runner).filter(new MethodNameFilter("testFilteredOut"));
            } catch (NoTestsRemainException e) {
                System.out.println("All methods are been filtered out");
                return;
            }
            ((BlockJUnit4ClassRunner) runner).sort(new Sorter(new AlphabetComparator()));
        } catch (Throwable e) {
            runner = new ErrorReportingRunner(CoreJUnit4SampleTest.class, e);
        }
        /**JUnit会在Runner运行之前通过RunNotifier发布testRunStarted事件表示JUnit运行开始*/
        notifier.fireTestRunStarted(runner.getDescription());
        runner.run(notifier);
        /**在Runner运行结束之后通过RunNotifier发布testRunFinished时间，表示JUnit运行结束*/
        notifier.fireTestRunFinished(result);
    }
}