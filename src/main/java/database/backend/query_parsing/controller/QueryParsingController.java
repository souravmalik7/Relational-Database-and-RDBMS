package database.backend.query_parsing.controller;

import database.backend.query_parsing.exception.QueryParsingException;
import java.util.regex.Pattern;
import static database.backend.query_parsing.constant.QueryParsingConstant.*;

public class QueryParsingController {

    public QueryParsingController() {
    }

    // parse query before execution
    public Boolean parse(final String query) throws  QueryParsingException {
        Boolean matchFlag;

        // Checking if query is not null
        if (query.isEmpty()) {
            throw new QueryParsingException("Empty Query Syntax");
        }

        final String queryLowerCase = query.trim().toLowerCase();

        if (queryLowerCase.contains(CREATE_DATABASE)) {
            matchFlag = Pattern.matches(CREATE_DATABASE_REGEX, query);
        } else if (queryLowerCase.contains(USE_DATABASE)) {
            matchFlag = Pattern.matches(USE_DATABASE_REGEX, query);
        } else if (queryLowerCase.contains(CREATE_TABLE)) {
            matchFlag = Pattern.matches(CREATE_TABLE_REGEX, query);

        } else if (queryLowerCase.contains(DROP_TABLE)) {
            matchFlag = Pattern.matches(DROP_TABLE_REGEX, query);

        }  else if (queryLowerCase.contains(SELECT)) {
            if (queryLowerCase.contains(SELECT_SPECIFIC)) {
                matchFlag = Pattern.matches(SELECT_SPECIFIC_QUERY_REGEX, query);
            }else {
                throw new QueryParsingException("Invalid SELECT query syntax!");
            }
        } else if (queryLowerCase.contains(INSERT)) {
            matchFlag= Pattern.matches(INSERT_QUERY_REGEX, query);

        } else if (queryLowerCase.contains(DELETE_DATA)) {
            matchFlag = Pattern.matches(DELETE_QUERY_REGEX, query);
        } else if (queryLowerCase.contains(UPDATE_DATA)) {
//            matchFlag = Pattern.matches(UPDATE_QUERY_REGEX, query);
            matchFlag = Boolean.TRUE;
        } else if (queryLowerCase.contains(START_TRANSACTION)) {
            matchFlag = Pattern.matches(START_TRANSACTION_QUERY, query);

        } else if (queryLowerCase.contains(COMMIT)) {
            matchFlag = Pattern.matches(COMMIT_QUERY, query);

        } else if (queryLowerCase.contains(ROLLBACK)) {
            matchFlag = Pattern.matches(ROLLBACK_QUERY, query);

        } else {
            throw new QueryParsingException("Invalid query syntax!");
        }

        if(matchFlag.equals(Boolean.FALSE)){
            throw new QueryParsingException("Invalid Query Syntax!");
        }
        return true;
    }
}
