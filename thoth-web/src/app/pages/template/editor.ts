import {Template} from "../../common/types/template";
import {TemplateService} from "../../services/api/template.service";
import {Router} from "@angular/router";

export const library = [
  {
    "xml": "jVHbbsMgDP0a3ilI6/NItj7taR8woeAFVAMRcZv07+cEtmqTKk0ykn18jm8I3cX1VOzk37IDFPpF6K7kTNWLaweIQsnghO6FUpKfUK8Psoc9KydbINF/BKoKrhYvUBFxNGwf0ZYzFHHsN9s5M92wcQhWrm48RR64P7A7U8ln6DLmwkjKiZnmMyD+gSyGMXE48HxcXpsrFAqDxeeWiMG5rY1ZfCB4n+yw9Vz4QIyVfEkOttElR210LgDrw/V3qO1+ghyByo0pS3DkK+OpXkh6CKNvKt0wO9d4/FHeb8lOO+d3eP+2PffrV78A",
    "w": 60,
    "h": 30,
    "aspect": "fixed",
    "title": "Marker"
  },
  {
    "xml": "jVLbbsIwDP2aPILalNsrlMvLpt0+AJnWtNFS0iUug339nIYWNg1paio755w4dmyRpNVpY6EuH02OWiQrkaTWGApedUpRayEjlYtkKaSM+BdyfYeNWzaqweKB/nNAhgNH0A0GREwXvLYfNuN8xHTpV6txdNYXjSuh9q6qoGC7OKIllYF+gB3qZ+MUKXNgfmeITMUC7YkFZO+FNc0hT402lvkc99Bouokw16rwJ8nUjIKrMfN17NUJOeNFe+G8Q6MOYb8kqp1I5r5UuabSUBkPdjiEWg2LBjRk2rjGohtmnJFcN6S0Y7uDUGiyDmb5Z/0TqHw+nyqnkjWjKOqxElVR0i9wb2wFHnx53aZPy1VPFC725YB22MoO9Ka+/LVywvtLM/gt8HS3oS106eYGTYVkzyw5B3YwHk5mo2QUz2aTaMafxEE8ClG69H2QcZiLqE//FgQXgKIPfx0hdi5T1G2v09pyP4b5Gw==",
    "w": 150,
    "h": 150,
    "aspect": "fixed",
    "title": "QR Code"
  },
  {
    "xml": "jVLbboMwDP2aPK6CsLXPBUpfNu3SD6gMpCRaICwxHd3Xz+HWbVKlSUF2zjkmvrEoqfu9hVY+mVJoFu1YlFhjcPTqPhFaMx6okkUp4zygj/HsBhsObNCCFQ3+J4CPAWfQnRgRtonpHD9sQfnsDyHbpP4MMocXPcmchNa7qoaKbHwWFlUB+hFyoV+MU6hMQ3xuEE1NAu2JGIr3ypquKROjjSW+FCfoNP74w1arykeiaQkF14rCl3JSvaCk4+HB7YwGM0K+RGwdi7a+Wp6hNCjDu1ysoFWrqgMNhTaus8KtCsqIZx0q7cjmMNTKomw06a0WrKH2KX2qEiXJ7oNgwaRQlcQ/4MnYGjz4+nZMntPdQlQu9AVa33NSNXhQX/5hvqb7NBHqhuhvTnWAppHuhakF2gtJ5tS84mGcfLCk9hMENwLVEntdEnKmPZmv130cuF/r+g0=",
    "w": 150,
    "h": 150,
    "aspect": "fixed",
    "title": "QR Code - GS1"
  },
  {
    "xml": "jVLbboMwDP2aPK6C0LV7LVD6smmT9gGVAReiBYIS09F9/RwubSdt0qREds6xk+PYIkqa4WChq19MiVpEexEl1hiavGZIUGshA1WKKBVSBryFzP5gw5ENOrDY0n8S5JRwBt3jhIhtzOtYsJpQPolt6tcY5Oii5yBXQ+dd1UDFNj6jJVWAfoYc9ZtxipRpmc8NkWk4QHsihuKjsqZvy8RoY5kv8QS9prsbdlpVPpNMxyi4DgtfyEkNyJLj8cHdggYLwn5N1DkR7XytMqPaUB0+5LiCTq2qHjQU2rjeolsVrEhmPSnt2OZgfa0iyiaT/v4BG2i8oE9VUs1BoQyCK1ijqmqvZ30HnoxtwIPJa7o/+psWpnKhrwi0wzGupXf15V+WGz7PDeHvwOHPpo7Q3NEDmgbJXjhkUcfso4ymtKs4n7ZdTyC4CaiuubcZYWcek+V4G8eR+zGt3w==",
    "w": 523,
    "h": 174,
    "aspect": "fixed",
    "title": "Code 128"
  },
  {
    "xml": "jVLbboMwDP2aPK6C0LV7LVD6smmT+gGVgZREDQQlpqP7+jnc2j1UmpTIzjl2chybRUndHyy08sOUQrNoz6LEGoOjV/eJ0JrxQJUsShnnAW3GsydsOLBBC1Y0+J8EPiZcQXdiRNg2pnUqSE3I3w7HkG1Tv4Y4hzc9xTkJrXdVDRXZ+CosqgL0O+RCfxmnUJmG+NwgmpoCtCdiKC6VNV1TJkYbS3wpztBpfLhhp1XlM9G0hIJrReFrOatekOp4eHA3o8GMkC8RW8einS+XZygNyvAlFyto1arqQEOhjeuscKuCFPGsQ6Ud2RysL5dF2WjSp3+wgdpr+lYlSooLeRAsoBSqkl7S+gE8G1uDB5PPdH+iyxamcqGv0fp/p7AGj+rHv803dJ66Qh8i+qedHaCprQdhaoH2RiGzOGJfeTSmLdp82nY9guBGoFpy74NCzjQr8/E+kwP3Z2R/AQ==",
    "w": 523,
    "h": 174,
    "aspect": "fixed",
    "title": "Code 128 - GS1"
  }
];

export class Editor {


  private drawIoWindow: Window | null = null;
  private currentTemplate: string | null = null;
  private currentListener: any | null = null;


  constructor(private router: Router) {
    window.addEventListener('beforeunload', ()=> {
      if (this.drawIoWindow != null) {
        this.drawIoWindow?.close();
      }
    });
  }

  openEditor(template: Template,
             templateService: TemplateService) {
    var sub = this.router.events.subscribe(change => {
      if (this.drawIoWindow != null) {
        this.drawIoWindow?.close();
      }
      if(sub) {
        sub.unsubscribe();
      }
    })
    var url = 'https://embed.diagrams.net/?embed=1&ui=atlas&spin=1&modified=unsavedChanges&proto=json&hide-pages=1&configure=1&template=' + template.id;
    if (this.drawIoWindow != null) {
      this.drawIoWindow?.close();
    }
    if (this.currentListener != null) {
      window.removeEventListener('message', this.currentListener)
    }

    // Implements protocol for loading and exporting with embedded XML
    this.currentListener = (evt: any) => {
      if (evt.data.length > 0 && this.currentTemplate == template.id) {
        var msg = JSON.parse(evt.data);
        if (msg.event === 'configure') {
          this.drawIoWindow?.postMessage(JSON.stringify({
            action: 'configure',
            config: {
              defaultLibraries: "general;thoth-elements",
              enableCustomLibraries: true,
              enabledLibraries: ["general", "thoth-elements"],
              libraries: [{
                "title": {
                  "main": "THOTH"
                },
                "entries": [{
                  "id": "thoth-elements",
                  "title": {
                    "main": "Thoth Elements"
                  },
                  "desc": {
                    "main": "Thoth Graphic Elements"
                  },
                  "libs": [{
                    "title": {
                      "main": "Thoth Graphic Elements"
                    },
                    "data": library
                  }]
                }]
              }]
            }
          }), '*');
        }
        // Received if the editor is ready
        else if (msg.event == 'init') {
          // Sends the data URI with embedded XML to editor
          this.drawIoWindow?.postMessage(JSON.stringify(
            {action: 'load', xml: template.xml, autosave: 1}), '*');
        }
        // Received if the user clicks save
        else if (msg.event == 'save' || msg.event == 'autosave') {
          // Sends a request to export the diagram as XML with embedded PNG
          template.xml = msg.xml;
          this.drawIoWindow?.postMessage(JSON.stringify(
            {action: 'export', format: 'svg', spinKey: 'saving', embedImages: false}), '*');
        }
        // Received if the export request was processed
        else if (msg.event == 'export') {
          // Updates the data URI of the image
          template.img = msg.data;
          template.svg = decodeURIComponent(escape(atob(msg.data.replace('data:image/svg+xml;base64,', ''))));
          template.markers = [];

          const regex = /{{ *([a-zA-Z0-9_]+)[^{]*}}/g;

          let m;

          while ((m = regex.exec(template.svg)) !== null) {
            // This is necessary to avoid infinite loops with zero-width matches
            if (m.index === regex.lastIndex) {
              regex.lastIndex++;
            }

            // The result can be accessed through the `m`-variable.
            m.forEach((match, groupIndex) => {
              if (groupIndex === 1) {
                template.markers.push(match);
              }
            });
          }
          template.markers = [...new Set(template.markers)];
          if (!template.markers.includes('block**')) {
            const toFill = template.markers.filter(m => m.startsWith("_"))
            if(toFill.length) {
              for (let string of toFill) {
                this.drawIoWindow?.postMessage(JSON.stringify(
                  {
                    action: 'prompt',
                    title: 'Specify marker for ' + string,
                    ok: 'Insert',
                    defaultValue: '{{' + string.substring(1) + '}}',
                    markerToFill: string
                  }), '*');
              }
            }else {
              this.save(template, templateService);
            }
            /*
            if (template.markers.includes('_barcode')) {
              this.drawIoWindow?.postMessage(JSON.stringify(
                {
                  action: 'prompt',
                  title: 'Specify marker for barcode',
                  ok: 'Insert',
                  defaultValue: '{{barcode}}'
                }), '*');
            } else if (template.markers.includes('_qrcode')) {
              this.drawIoWindow?.postMessage(JSON.stringify(
                {
                  action: 'prompt',
                  title: 'Specify marker for qrcode',
                  ok: 'Insert',
                  defaultValue: '{{qrcode}}'
                }), '*');
            } else if (template.markers.includes('_marker')) {
              this.drawIoWindow?.postMessage(JSON.stringify(
                {
                  action: 'prompt',
                  title: 'Specify the marker',
                  ok: 'Insert',
                  defaultValue: '{{marker}}'
                }), '*');
            } else {
              this.save(template, templateService);
            }*/
          } else {
            this.save(template, templateService);
          }
        } else if (msg.event == 'prompt') {

          var xmlDoc = new DOMParser().parseFromString(template.xml || '', 'application/xml');
          var diagrams = xmlDoc.getElementsByTagName('diagram');
          const node: any = diagrams[0].children.item(0)?.outerHTML;
          console.log(msg);
          // @ts-ignore
          if(msg.message.markerToFill){
            const n = node.replaceAll('{{'+msg.message.markerToFill+'}}', msg.value);
            this.drawIoWindow?.postMessage(JSON.stringify(
              {action: 'merge', xml: n}), '*');
          }
          /*
          if (msg.message.defaultValue === '{{barcode}}') {
            const n = node.replaceAll('{{_barcode}}', msg.value);
            this.drawIoWindow?.postMessage(JSON.stringify(
              {action: 'merge', xml: n}), '*');
          } else if (msg.message.defaultValue === '{{qrcode}}') {
            const n = node.replaceAll('{{_qrcode}}', msg.value);
            this.drawIoWindow?.postMessage(JSON.stringify(
              {action: 'merge', xml: n}), '*');
          } else if (msg.message.defaultValue === '{{marker}}') {
            const n = node.replaceAll('{{_marker}}', msg.value);
            this.drawIoWindow?.postMessage(JSON.stringify(
              {action: 'merge', xml: n}), '*');
          }*/

        }

        // Received if the user clicks exit or after export
        if (msg.event == 'exit') {
          // Closes the editor
          window.removeEventListener('message', this.currentListener);
          this.drawIoWindow?.close();
          this.drawIoWindow = null;
          templateService.update(template).finally();
        }
      }
    };

    // Opens the editor
    window.addEventListener('message', this.currentListener);
    this.currentTemplate = template.id;
    this.drawIoWindow = window.open(url);
  }

  save(template: Template, templateService: TemplateService) {
    templateService.update(template).then(ok => {
      this.drawIoWindow?.postMessage(JSON.stringify({
        action: 'status',
        message: 'Saved',
        modified: false
      }), '*');
    });

  }
}
