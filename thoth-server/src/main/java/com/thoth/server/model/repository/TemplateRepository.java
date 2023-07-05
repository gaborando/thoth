package com.thoth.server.model.repository;

import com.thoth.server.model.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, String>, PagingAndSortingRepository<Template, String>, JpaSpecificationExecutor<Template> {
    @Query(value = "select distinct t.folder " +
            "from template t " +
            "left join template_allowed_organization_list taol on t.id = taol.template_id " +
            "left join template_allowed_user_list taul on t.id = taul.template_id " +
            "where t.created_by = ?1 or taol.sid = ?2 or taul.sid = ?1 " +
            "order by t.folder", nativeQuery = true)
    List<String> getFolders(String userSid, String orgSid);
}