package com.ncedu.cheetahtest.dao.testcase;


public class TestCaseConsts {

    private TestCaseConsts() {
    }

    public static final String SELECT_ALL_PARAMS_FROM_TEST_CASE =
            "SELECT id, title, project_id, status, result, repeatable, execution_cron_date " +
                    "FROM test_case ";

    public static final String UPDATE = "UPDATE test_case SET ";

    public static final String UPDATE_TEST_CASE_SQL =
            UPDATE + " title = ?, project_id = ?," +
                    " status = ?::test_case_status, result = ?::test_case_result" +
                    " WHERE id = ?";

    public static final String FIND_TEST_CASE_BY_ID =
            SELECT_ALL_PARAMS_FROM_TEST_CASE +
                    " WHERE id = ? LIMIT 1";

    public static final String FIND_TEST_CASE_BY_TITLE_EXCEPT_CURRENT =
            SELECT_ALL_PARAMS_FROM_TEST_CASE +
                    " WHERE title = ? AND id <> ? LIMIT 1";

    public static final String DEACTIVATE_TEST_CASE_SQL =
            UPDATE + " status = 'INACTIVE'::test_case_status WHERE id = ?";

    public static final String CREATE_TEST_CASE_SQL =
            "INSERT INTO test_case (title, project_id, status, result)" +
                    " VALUES (?,?,?::test_case_status,?::test_case_result)";

    public static final String GET_TEST_CASE_ID_BY_TITLE =
            "SELECT id FROM test_case WHERE title = ?";

    public static final String GET_TEST_CASE_PAGINATED_BY_PROJECT_ID =
            SELECT_ALL_PARAMS_FROM_TEST_CASE +
            "WHERE project_id = ? " +
            "AND status = 'ACTIVE' " +
            "ORDER BY id LIMIT ? OFFSET ?";

    public static final String GET_AMOUNT_OF_ACTIVE_TEST_CASES_BY_PROJECT_ID =
            "SELECT COUNT(id) " +
            "FROM test_case " +
            "WHERE status = 'ACTIVE' " +
            "AND project_id = ?" +
            "LIMIT 1";

    public static final String FIND_BY_TITLE_TEST_CASE_PAGINATED_BY_PROJECT_ID =
            SELECT_ALL_PARAMS_FROM_TEST_CASE +
            "WHERE project_id = ? " +
            "AND status = 'ACTIVE' " +
            "AND title ILIKE ? " +
            "ORDER BY id LIMIT ? OFFSET ?";

    public static final String GET_AMOUNT_OF_ACTIVE_TEST_CASES_BY_PROJECT_ID_AND_ILIKE =
            "SELECT COUNT(id) " +
            "FROM test_case " +
            "WHERE status = 'ACTIVE' " +
            "AND title ILIKE ? " +
            "AND project_id = ?" +
            "LIMIT 1";

    public static final String FIND_TEST_CASE_BY_PROJECT_ID_AND_TEST_CASE_ID =
            SELECT_ALL_PARAMS_FROM_TEST_CASE +
            "WHERE project_id = ? AND id = ?";

    public static final String GET_ACTIVE_TEST_CASES_WITH_EXECUTION_DATE =
            SELECT_ALL_PARAMS_FROM_TEST_CASE +
            "WHERE execution_cron_date IS NOT NULL ";

    public static final String SET_EXECUTION_DATE_TO_NULL =
            UPDATE +
            "execution_cron_date = NULL " +
            "where id = ?";

    public static final String SET_EXECUTION_DATE_AND_REPEATABILITY =
            UPDATE +
            "execution_cron_date = ?, " +
            "repeatable = ?" +
            "WHERE id = ?";

    public static final String DELETE_EXECUTION_DATE_AND_REPEATABILITY =
            UPDATE +
            "execution_cron_date = null " +
            "WHERE id = ?";
}
