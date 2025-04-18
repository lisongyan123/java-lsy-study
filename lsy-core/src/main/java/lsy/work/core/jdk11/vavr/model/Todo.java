package lsy.work.core.jdk11.vavr.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Alexandre Grison (a.grison@gmail.com)
 */
@Data
@RequiredArgsConstructor
public class Todo {
	public final String todo;
	public boolean done;
}
