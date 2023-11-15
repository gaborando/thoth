# THOTH - Template and Printing Service

Thoth is a software to design and render documents. 
It is based on [Draw.io](https://www.drawio.com/) to design the document and [Jinja - The Pallets Projects.url](https://palletsprojects.com/p/jinja/) to compile dinamically the document.

With Thoth you can:
* Create **Templates** - Design with draw.io an SVG file that can be filled with marker and script block managed by jinja.
* Create **Datasource** - A connection to a data resource like a Database by JDBC or REST API.
* Create **Renderers**  - Automate the document generation associating data from datasource directly to markers in a template.
* Create **Clients** - Servers with *Thoth Client* installed that can handle printing requests from *Thoth Server*

## Examples
You can find theese documents in the [/doc/examples](doc/examples) folder, to use them just import the file in a new Thoth Template from *Thoth Web*
![marker.png](doc%2Fexamples%2Fmarker.png)
![barcodes.png](doc%2Fexamples%2Fbarcodes.png)
![pipes.png](doc%2Fexamples%2Fpipes.png)
![code.png](doc%2Fexamples%2Fcode.png)

