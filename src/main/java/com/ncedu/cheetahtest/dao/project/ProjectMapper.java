package com.ncedu.cheetahtest.dao.project;

import com.ncedu.cheetahtest.entity.project.Project;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectMapper implements RowMapper<Project> {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String LINK = "link";
    private static final String STATUS = "status";
    private static final String CREATE_DATE = "create_date";
    private static final String OWNER_ID = "owner_id";

    @Override
    public Project mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Project(
                resultSet.getInt(ID),
                resultSet.getString(NAME),
                resultSet.getString(LINK),
                resultSet.getString(STATUS),
                resultSet.getDate(CREATE_DATE),
                resultSet.getInt(OWNER_ID)
        );
    }
}
