package com.thoth.server.service;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.model.domain.Template;
import com.thoth.server.model.repository.TemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final IAuthenticationFacade authenticationFacade;

    public TemplateService(TemplateRepository templateRepository, IAuthenticationFacade authenticationFacade) {
        this.templateRepository = templateRepository;
        this.authenticationFacade = authenticationFacade;
    }
    public Template update(String identifier, Template properties) {
        return update(getById(identifier).orElseThrow(), properties);
    }
    @PreAuthorize("@authenticationFacade.canWrite(#original)")
    private Template update(Template original, Template update)  {
        update.setId(original.getId());
        update.setSvg(cleanSVG(update.getSvg()));
        if(update.getFolder() == null){
            update.setFolder("/");
        }
        if(!update.getFolder().startsWith("/")){
            update.setFolder("/" + update.getFolder());
        }
        return templateRepository.save(update);
    }

    private String cleanSVG(String svg) {
        try {
            // Create a DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Create a DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Parse the XML file

            // Create an InputSource from the XML string
            InputSource inputSource = new InputSource(new StringReader(svg));

            // Parse the XML string
            Document document = builder.parse(inputSource);

            var queue = new LinkedList<Node>();
            queue.add(document);

            while (!queue.isEmpty()){
                var el = queue.remove();
                for (int i = 0; i <el.getChildNodes().getLength(); i++) {
                    queue.add(el.getChildNodes().item(i));
                }
                if(el.getNodeName().equals("text") && el.getTextContent().endsWith("...")){
                    el.getParentNode().removeChild(el);
                    el.setTextContent("");
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Convert the XML document to a string
            DOMSource source = new DOMSource(document);
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);
            transformer.transform(source, result);

            // Get the XML string from the StringWriter
            return stringWriter.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        return svg;
    }


    @PreAuthorize("@authenticationFacade.canWrite(#template)")
    public void delete(Template template){
        templateRepository.delete(template);
    }

    public Page<Template> search(Specification<Template> specification, Pageable pageable){
        return templateRepository.findAll(authenticationFacade.securedSpecification(specification, Template.class), pageable);
    }

    public Template create(String name) {
        var template = new Template();
        template.setId("tpl_" + UUID.randomUUID());
        template.setName(name);
        template.setCreatedAt(Instant.now());
        template.setFolder("/");
        authenticationFacade.fillSecuredResource(template);
        return templateRepository.save(template);
    }

    @PostAuthorize("@authenticationFacade.canRead(returnObject) || hasRole('ROLE_TMP')")
    public Optional<Template> getById(String identifier) {
        return templateRepository.findById(identifier);
    }

    public List<String> getFolders() {
        return templateRepository.getFolders(authenticationFacade.getUserSID(), authenticationFacade.getOrganizationSID());
    }
}
