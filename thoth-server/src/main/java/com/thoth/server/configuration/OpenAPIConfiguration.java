package com.thoth.server.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Thoth - Template and Printing Service",
                version = "0.0.1",
                contact = @Contact(
                        name = "Gabor Galazzo", email = "gabor.galazzo@gmail.com", url = "https://www.gaborgalazzo.com"
                ),
                description = "Design, Render and Print Document using Draw.io and Jinja",
                summary = "# THOTH - Template and Printing Service\n" +
                        "\n" +
                        "Thoth is a software to design and render documents. \n" +
                        "It is based on [Draw.io](https://www.drawio.com/) to design the document and [Jinja - The Pallets Projects.url](https://palletsprojects.com/p/jinja/) to compile dinamically the document.\n" +
                        "\n" +
                        "With Thoth you can:\n" +
                        "* Create **Templates** - Design with draw.io an SVG file that can be filled with marker and script block managed by jinja.\n" +
                        "* Create **Datasource** - A connection to a data resource like a Database by JDBC or REST API.\n" +
                        "* Create **Renderers**  - Automate the document generation associating data from datasource directly to markers in a template.\n" +
                        "* Create **Clients** - Servers with *Thoth Client* installed that can handle printing requests from *Thoth Server*",
                license = @License(
                        name = "MIT", url = "https://github.com/gaborando/thoth/blob/master/LICENSE.md"
                )
        ),
        servers = @Server(
                url = "http://localhost:8080",
                description = "Production"
        )
)
@SecurityScheme(
        name = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenAPIConfiguration {


}