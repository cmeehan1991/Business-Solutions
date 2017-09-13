/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.projects.search;

import com.cbmwebdevelopment.connections.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.cbmwebdevelopment.projects.search.ProjectsSearchResultTable.ProjectsTable;

/**
 *
 * @author cmeehan
 */
public class ProjectsSearch {

    public ObservableList<ProjectsTable> projects(String search) throws SQLException {
        ObservableList<ProjectsTable> list = FXCollections.observableArrayList();

        Connection conn = new DBConnection().connect();
        String sql = "SELECT PROJECT.ID, ALL_CUSTOMERS.COMPANY_NAME, PROJECT.TITLE, PROJECT.PROJECT_TYPE, PROJECT.DATE_CREATED, PROJECT.LAST_UPDATED FROM PROJECT JOIN ALL_CUSTOMERS ON PROJECT.CLIENT_ID = ALL_CUSTOMERS.ID WHERE " + search;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            System.out.println(ps);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    list.addAll(new ProjectsTable(rs.getString("ID"), rs.getString("COMPANY_NAME"), rs.getString("TITLE"), rs.getString("PROJECT_TYPE"), rs.getString("DATE_CREATED"), rs.getString("LAST_UPDATED")));
                } while (rs.next());
            }
            ps.closeOnCompletion();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }

        return list;
    }
}
