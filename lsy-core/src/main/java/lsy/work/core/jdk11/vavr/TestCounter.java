package lsy.work.core.jdk11.vavr;

import io.vavr.collection.HashMap;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import lsy.work.core.jdk11.vavr.model.TodoAction;
import lsy.work.core.jdk11.vavr.model.Todos;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TestCounter {
    static final String INC = "INCREMENT";
    static final String DEC = "DECREMENT";

    final Reducer<String, Integer> reducer =
            (action, state) -> state + HashMap.of(INC, 1, DEC, -1).getOrElse(action, 0);

    final AtomicInteger consumerCount = new AtomicInteger(0);
    final Consumer<Integer> consumer = (state) -> consumerCount.incrementAndGet();

    final AtomicLong m1Start = new AtomicLong(0);
    final AtomicLong m1End = new AtomicLong(0);
    final Middleware<Integer, String> m1 = (store, action, next) -> {
        m1Start.set(System.currentTimeMillis());
        sleep(50);
        next.accept(store, action, null);
        sleep(50);
        m1End.set(System.currentTimeMillis());
    };

    final AtomicLong m2Start = new AtomicLong(0);
    final AtomicLong m2End = new AtomicLong(0);
    final Middleware<Integer, String> m2 = (store, action, next) -> {
        m2Start.set(System.currentTimeMillis());
        next.accept(store, action, null);
        m2End.set(System.currentTimeMillis());
    };


    public void testCounter() {
        int counter = 0;
        Store<Integer, String> store = Store.create(counter, reducer, m2, m1);

        store.dispatch(INC);

        Stream.of(1, 2, 3).forEach(e -> store.dispatch(INC));

        Stream.of(1, 2, 3, 4, 5).forEach(e -> store.dispatch(DEC));

        store.subscribe(consumer);
        Stream.of(1, 2, 3, 4, 5, 6, 7).forEach(e -> store.dispatch(INC));

        store.unsubscribe(consumer);
        Stream.of(1, 2, 3).forEach(e -> store.dispatch(DEC));

    }

    final Reducer<TodoAction, Todos> reducerMe =
            (action, state) -> HashMap.<String, Supplier<Todos>>of(TodoAction.ADD, () -> state.addTodo((String) action.payload),
                            TodoAction.REMOVE, () -> state.removeTodo((int) action.payload),
                            TodoAction.TOGGLE, () -> state.toggle((int) action.payload),
                            TodoAction.CHANGE_DISPLAY, () -> state.changeDisplay((String) action.payload))
                    .getOrElse(action.type, () -> {
                        throw new IllegalArgumentException("Invalid Action Type: " + action.type);
                    }).get();

    public void testTodo() {
        Store<Todos, TodoAction> store = Store.create(new Todos(), reducerMe);
        store.dispatch(TodoAction.of(TodoAction.ADD, "foo"));
    }

    private void sleep(long m) {
        Try.run(() -> Thread.sleep(m));
    }
}
