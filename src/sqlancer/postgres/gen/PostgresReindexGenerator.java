package sqlancer.postgres.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sqlancer.IgnoreMeException;
import sqlancer.Query;
import sqlancer.QueryAdapter;
import sqlancer.Randomly;
import sqlancer.postgres.PostgresGlobalState;
import sqlancer.postgres.PostgresSchema.PostgresIndex;

public final class PostgresReindexGenerator {

    private PostgresReindexGenerator() {
    }

    private enum Scope {
        INDEX, TABLE, DATABASE;
    }

    public static Query create(PostgresGlobalState globalState) {
        List<String> errors = new ArrayList<>();
        errors.add("could not create unique index"); // CONCURRENT INDEX
        StringBuilder sb = new StringBuilder();
        sb.append("REINDEX");
        // if (Randomly.getBoolean()) {
        // sb.append(" VERBOSE");
        // }
        sb.append(" ");
        Scope scope = Randomly.fromOptions(Scope.values());
        switch (scope) {
        case INDEX:
            sb.append("INDEX ");
            if (Randomly.getBoolean()) {
                sb.append("CONCURRENTLY ");
            }
            List<PostgresIndex> indexes = globalState.getSchema().getRandomTable().getIndexes();
            if (indexes.isEmpty()) {
                throw new IgnoreMeException();
            }
            sb.append(indexes.stream().map(i -> i.getIndexName()).collect(Collectors.joining()));
            break;
        case TABLE:
            sb.append("TABLE ");
            if (Randomly.getBoolean()) {
                sb.append("CONCURRENTLY ");
            }
            sb.append(globalState.getSchema().getRandomTable(t -> !t.isView()).getName());
            break;
        case DATABASE:
            sb.append("DATABASE ");
            if (Randomly.getBoolean()) {
                sb.append("CONCURRENTLY ");
            }
            sb.append(globalState.getSchema().getDatabaseName());
            break;
        default:
            throw new AssertionError(scope);
        }
        errors.add("already contains data"); // FIXME bug report
        errors.add("does not exist"); // internal index
        errors.add("REINDEX is not yet implemented for partitioned indexes");
        return new QueryAdapter(sb.toString(), errors);
    }

}
