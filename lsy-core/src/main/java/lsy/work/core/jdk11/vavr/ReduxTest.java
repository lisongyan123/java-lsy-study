package lsy.work.core.jdk11.vavr;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
class Foo {
    String s = "foo";
    int i = 1;
    List<String> l = new ArrayList<>();
}

public class ReduxTest {
    Reducer<String, String> concatBar = (action, state) -> "CONCAT".equals(action) ? state + "bar" : state;

    Reducer<String, Integer> plus2 = (action, state) -> "PLUS".equals(action) ? state + 2 : state;

    Reducer<String, List<String>> addFoo = (action, state) -> {
        if ("ADD".equals(action))
            state.add("foo");
        return state;
    };

    Reducer reducers = Redux.combineReducers(Tuple.of("s", concatBar), Tuple.of("i", plus2), Tuple.of("l", addFoo));

    public void testCombineReducersMap() {
        io.vavr.collection.Map<String, Serializable> initialState = HashMap.of("s", "foo", "i", 1, "l", new ArrayList<String>());
        Store<Map<String, Object>, String> store = Redux.createStore(initialState.toJavaMap(), reducers);
        store.dispatch("CONCAT");
        store.dispatch("PLUS");
        store.dispatch("PLUS");
        store.dispatch("CONCAT");
        store.dispatch("ADD");
        store.dispatch("ADD");

    }

    public void testCombineReducersBean() {
        Store<Foo, String> store = Redux.createStore(new Foo(), reducers);
        store.dispatch("CONCAT");
        store.dispatch("PLUS");
        store.dispatch("PLUS");
        store.dispatch("CONCAT");
        store.dispatch("ADD");
        store.dispatch("ADD");

    }

    // Actions
    static final String INC = "INC";
    static final String DEC = "DEC";

    // this is our reducer which increments if INC, decrement if DEC
    // and does nothing otherwise
    final Reducer<String, Integer> reducer =
            (action, state) -> state + HashMap.of(INC, 1, DEC, -1).getOrElse(action, 0);

    final Middleware<Integer, String> middleware = (store, action, next) -> {
        System.out.println("Before " + store.getState());
        next.accept(store, action, null);
        System.out.println("After " + store.getState());
    };

    public void foo() {
        Store<Integer, String> store = Redux.createStore(0, reducer, middleware);

        store.dispatch(INC);
    }
}
