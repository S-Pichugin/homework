package benchmarks;

import my.CustomList;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class CustomListBenchmark {

    private static final int ELEMENTS_COUNT = 1_000_000;

    @Benchmark
    public void customListAdd() {
        CustomList<Integer> list = new CustomList<>();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            list.add(i);
        }
    }

    @Benchmark
    public void arrayListAdd() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            list.add(i);
        }
    }

    @Benchmark
    public void linkedListAdd() {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            list.add(i);
        }
    }

    @Benchmark
    public void customListAddRemoveFirst() {
        CustomList<Integer> list = new CustomList<>();
        for (int i = 0; i < 10_000; i++) {
            list.add(i);
        }
        for (int i = 0; i < 10_000; i++) {
            list.remove(0);
        }
    }

    @Benchmark
    public void arrayListAddRemoveFirst() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            list.add(i);
        }
        for (int i = 0; i < 10_000; i++) {
            list.remove(0);
        }
    }

    @Benchmark
    public void linkedListAddRemoveFirst() {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < 10_000; i++) {
            list.add(i);
        }
        for (int i = 0; i < 10_000; i++) {
            list.remove(0);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CustomListBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
