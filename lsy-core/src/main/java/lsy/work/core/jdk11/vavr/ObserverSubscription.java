package lsy.work.core.jdk11.vavr;

import java.util.Observer;

/**
 * This represents part of a store, regarding the subscribing/un-subscribing using
 * {@link Observer}.
 *
 * @author Alexandre Grison (a.grison@gmail.com)
 */
public interface ObserverSubscription {
	/**
	 * Subscribe a consumer of the state.
	 *
	 * @param observer the observer.
	 */
	void subscribe(Observer observer);

	/**
	 * Un-subscribe a consumer from this store.
	 *
	 * @param observer the observer to be removed from the list of observers.
	 */
	void unsubscribe(Observer observer);
}
