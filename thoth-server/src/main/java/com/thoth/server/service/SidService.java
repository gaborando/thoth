package com.thoth.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

@Service
public class SidService {
    private final JdbcTemplate template;

    public SidService(JdbcTemplate template) {
        this.template = template;
    }

    private String likeWrap(String value) {
        return '%' + value + '%';
    }

    public List<String> findAllUsers(String value, PageRequest of) {
        var params = new Object[]{likeWrap(value), likeWrap(value), likeWrap(value), likeWrap(value), likeWrap(value),
                likeWrap(value), likeWrap(value), likeWrap(value), likeWrap(value), of.getPageSize(), of.getOffset()};
        var types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER};
        return template.queryForList(
                "select sid from (select distinct sid from client_allowed_user_list where sid like ? " +
                        "union distinct " +
                        "select distinct sid from datasource_properties_allowed_user_list  where sid like ? " +
                        "union distinct " +
                        "select distinct sid from renderer_allowed_user_list  where sid like ? " +
                        "union distinct " +
                        "select distinct usersid from api_key where usersid like ? " +
                        "union distinct " +
                        "select distinct created_by from client where created_by like ? " +
                        "union distinct " +
                        "select distinct created_by from datasource_properties where created_by like ? " +
                        "union distinct " +
                        "select distinct created_by from renderer where created_by like ? " +
                        "union distinct " +
                        "select distinct created_by from template where created_by like ? " +
                        "union distinct " +
                        "select distinct sid from template_allowed_user_list  where sid like ? ) as a " +
                        "order by sid " +
                        "limit ? offset ?",
                params,
                types, String.class);
    }

    public List<String> findAllOrganizations(String value, PageRequest of) {
        var params = new Object[]{likeWrap(value), likeWrap(value), likeWrap(value), likeWrap(value), likeWrap(value), of.getPageSize(), of.getOffset()};
        var types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER};
        return template.queryForList(
                "select sid from (select distinct sid from client_allowed_organization_list where sid like ? " +
                        "union distinct " +
                        "select distinct sid from datasource_properties_allowed_organization_list  where sid like ? " +
                        "union distinct " +
                        "select distinct sid from renderer_allowed_organization_list  where sid like ? " +
                        "union distinct " +
                        "select distinct organizationsid from api_key  where organizationsid like ? " +
                        "union distinct " +
                        "select distinct sid from template_allowed_organization_list  where sid like ? ) as a " +
                        "order by sid " +
                        "limit ? offset ?",
                params,
                types, String.class);
    }
}
