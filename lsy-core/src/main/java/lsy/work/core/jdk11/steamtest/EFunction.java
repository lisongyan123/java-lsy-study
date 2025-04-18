package lsy.work.core.jdk11.steamtest;

import java.util.function.Function;

@FunctionalInterface
public interface EFunction<T, R> extends Function<T, R> {
    default R elseApply(final T t) {
        return this.apply(t);
    }
}