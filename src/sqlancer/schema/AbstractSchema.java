package sqlancer.schema;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import sqlancer.Randomly;

public class AbstractSchema<A extends AbstractTable<?, ?>> {

	private final List<A> databaseTables;
	
	public AbstractSchema(List<A> databaseTables) {
		this.databaseTables = Collections.unmodifiableList(databaseTables);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (A t : getDatabaseTables()) {
			sb.append(t + "\n");
		}
		return sb.toString();
	}

	public A getRandomTable() {
		return Randomly.fromList(getDatabaseTables());
	}

	public A getRandomTable(Predicate<A> predicate) {
		return Randomly.fromList(getDatabaseTables().stream().filter(predicate).collect(Collectors.toList()));
	}

	public List<A> getDatabaseTables() {
		return databaseTables;
	}

	public List<A> getDatabaseTablesRandomSubsetNotEmpty() {
		return Randomly.nonEmptySubset(databaseTables);
	}

}