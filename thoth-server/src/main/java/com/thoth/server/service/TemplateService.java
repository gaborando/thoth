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
        template.setSvg("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" height=\"1px\" version=\"1.1\" viewBox=\"-0.5 -0.5 1 1\" width=\"1px\"><defs/><g/></svg>");
        template.setXml("<mxfile host=\"embed.diagrams.net\" modified=\"2023-12-04T10:02:26.125Z\" agent=\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0\" etag=\"ICBrTCvB0ec7XxhN_fFi\" version=\"22.1.5\" type=\"embed\">\n" +
                "  <diagram id=\"FsnQ3hMx6PmaIt3tymRr\" name=\"Page-1\">\n" +
                "    <mxGraphModel dx=\"1426\" dy=\"782\" grid=\"1\" gridSize=\"10\" guides=\"1\" tooltips=\"1\" connect=\"1\" arrows=\"1\" fold=\"1\" page=\"1\" pageScale=\"1\" pageWidth=\"827\" pageHeight=\"1169\" math=\"0\" shadow=\"0\">\n" +
                "      <root>\n" +
                "        <mxCell id=\"0\" />\n" +
                "        <mxCell id=\"1\" parent=\"0\" />\n" +
                "      </root>\n" +
                "    </mxGraphModel>\n" +
                "  </diagram>\n" +
                "</mxfile>\n");
        template.setImg("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2ZXJzaW9uPSIxLjEiIHdpZHRoPSIxcHgiIGhlaWdodD0iMXB4IiB2aWV3Qm94PSItMC41IC0wLjUgMSAxIj48ZGVmcy8+PGcvPjwvc3ZnPg==");
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
