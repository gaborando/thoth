import {Template} from "../../common/types/template";
import {TemplateService} from "../../services/api/template.service";

export const library = [
  {
    "xml": "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",
    "w": 151,
    "h": 159.77999999999997,
    "aspect": "fixed",
    "title": "QR Code"
  },
  {
    "xml": "7V1rc6LK0/80vjwpREV4OYiXSKKo8ZK8eQoV8Y5BFJNP/3T3DIoKbpLNOVX/Krd2YwTm1tdfd8+wmVxpdaj69mb67I2dZSZXzuRKvucF/LfVoeQslxlZmo0zOSMjyxL8y8iVlLtZuittbN9ZB19pIPMGe3u5c/gVfmEbfCzFBdf3dhvxmOMHzuFsqJG3XjujwB5Gj0vXU6BHxfhVx1s5gf8Bj4iO/sk+ZHOKqqi5YlHOKllFyjn/ZHO8m3A2Dqb8uXxBfpAL0vGPmMDUmbnTaKBs8SEfe0Qq8GfsrVjKcfATSeAXQZVkCuX+TKHt1N7gr7OV7cKnjmSajezlkz10lpa3nQUzbw33h14QeCt4YIk3dHu0QNquxyVv6flwf+xM7N0yiPXAljMXWwbeBq7a2w2QGr5OZgcHpqfTgCy6KkVXsCs7sDM5xr/KlfnGcTMyrlWbw488Y63O4q3edpnOXAbf4JPBUipyqBuMuVXdHVX1llvTw1G91HJNQ281DLZ9MvRFpxyq3Vrbm9Smkg0PuPVSfv1Yetw+HsKRaYw2TwY7NIyWVPsYFawX98Mq5T8bxlPUe2sBPYbQk9sw2u7HYzucPs8fP/7uH/YeMtYusVKfLV2DdR4Zm5Z1dijrcEttsVqI6+zqrFXGf+z0p1xiYasCK34stbwn/nTQhhtu2WDhY5mF3Qo0gQsVjbUegT6ttt5+nD53y9VytjLVP+qHimHqC7v8+CiZh7Dd60gTtniW6x+uuzAr09Frte0t67ORZ3Y8qfHymmsaC6UltSvtxfix09289Cq9fi87feuvGou3/tvari7fR7l2drwe553BuzF97Fen+Vl9tjFflvV+f1mYva3ezfnG7K82ymzz7pufwVNfDoqzwg7YdHgeVA/qvP4RPL1kG4N+Vpu/ybvaLJ+R9ZdlYTfxFMc/qHtL0iZDEHHdWisRnYA2YTmiE1/4DTqVkU4leMzQ2YjTqVXP0dOHclnvlA/6tK53W+PpqP08e/bKj5Wy2faGNb3Uef7s7lnvdf3ULk1fF41meam2iVYroNzGnHflxqdbaC4a5Xb3rdYpL81utt3rLcev/d5m/lbtrd7k6cZeN6Tx4C3n1JaK6zXL01e7NntcmYtNs7t8swer+sr23puLjW2v383V+9ZvSsHQzu2eVkq4tcqH0bD28bw2pcDqZsfDgdxY2/mdtSg4w7XSXL9fUUnzmMGeQWMe27BGZoUG3pvNZi7z4A8zwexUDjkSyYWqdrCJ+44tQ6P90pMqI9TBIn4vdRynPoHWYW+MH7OVx+Dj0YQfa3iA6WCLKh+2Npxorv8yUZlkVver0n5Dvb9N1NW+jyO9mhJ+sEZp6jFLHsGTi/XKm2jT/gtcl3qVNnQpjbG3l0+8hIP6NWzTnG1sbAJXOo49RUWtvFl4pxxrhmuRqF2FPZW28HTFe++7RsH1aWTrbUkresOpFcfY3s46rEfrNLYfKDDsGXvQdri0wHYNycijJVYXUzT4lYBoZnRmzR12/3aAH4V64R1n+dpbUA/tbm+sq0OabEvBzmU+wqZdG+Cg2zz1qGbRnm3ND6CE927SPEnEmUsk7dRae+gjNyeqmXWkeAt/7FWWr09gShLKeIC9aXCbWUSRxsZHj9Gjtnu64mXz+mBPa0enVPk0cRnDHL9iw/As4mZjjUshxqiMr0irtLuToAYDEnVDo8hpy0xz6UVEQDmJMwaIUCu2cYS61cAL2EhlCjRhioHTZ/XHcYla0golb+FLpb2VvDaNT/HV0nEZdanxSUONSXL1FU7DbiIvzBrJnSGXPCEJ5eFTO9/Ae83pjgTZfx+gaDxnoxm2NKJcM+zh4pXGkgsoMnCq4KJC3X0nIR1WBkiDwSfYbP2TOPO8NDgjKx1sMX99R1oqzFoTL20bh14OcTVFfqUhZA7l9oIdxbYfllxDIfpWLwUedKtXGWwrTroU7V9BDlbGgSSdrVsySX61Mm7Q2K1Xm7iuXwpJqHtZ13nG7y/ecEhdKkOiihtmQeKKL0g0ZpzmypqSzZrIlhLeqg8CZGfR4Xq5XvieJc+h5cJz1sSTVxMJplU/sp80uzKO1tnbyCZZhflvrPqgRaK2NCSab1HvkhrLfZcd9c2skb6xamlDorbtTV60yXOt2AQKfXStrkXmqk68nPnKBAzeDeGBB/yCq1ZxwHUrZ5EqZ1H8pKpNw8ZFiyzZnNbT3gZiYBIjVupaLzTrLdokZO/8zRbCIFjyMTbATk4HL7gqwQx5hXR463NdwCnK876e/6Dvghlgz3Yr6tt7bgxTNZlZoJQmSADOVPNQyFi9XthQS5g4n3+7G0yeK5MXkrdIRVV2NLdhCcwtEgw6mpBsrJDBKxvFMfdCV/xeQScHAWTJL0m/gyy1nfd9/EWjNqy+IktKRvXYhjU7INI6dys+uZV532MFtG1Ii71bok80iSp4ECHGrz3yIFK1GmB3kQirjBRL4wIM8qZUZZpJ6G/I/pmG0ScKBI2J+oFtZY1/r7qGa7lcV5YOmRz2RALccTp7H6eyIGMlbpI9Ull0E+f3N1IJFyap2ihMNte7Z3+i7fHZoLpnJuPelTMYbDVZbuu1N89w/3NkL8hEqj9lRoiCBJ4bnT347i4O19q8E+eFP+Aa45PCyqWANKa0AnWhyZPUr0mALGNAmhGQ5Sp3g7GOvk7XLTJhTnVPq7e5DG9sBaxcnoGJ4O1b8IP30KMe2u/ZT22s58tCwmwuYT4RD1x6NpNmW8dccwoNJ+BDkgx43cYok2ZxPas64MsM+DIbtMwpdzlIChwWPlj0OyvFsBQqXCkJWwHZDwT7dY6ttEpj0OohKed7Ytx6RjrypPdwUooJeCMPGGCiXqGmGKY6nOMt5JkHzNROjlsgKtY4d9xgTY+gaK59089zUmjcgLDqG+psDEexajBwKw6/zZHUJ5/DFZJCvz/mtg57l7y5fwN59KTaIaCnBZaCHvkohKZgTU0av/Ty2YVv6AcRpHrvYHJLqKSVFKxRYAS2jt6LPxB5L2/hMZWPExMysA3iWuTCmXW0KaEeU1BugK9YEuoRjZmARWilCHTEsNQ5oAQ/c4mlAKkcGwk0FQdMiE87+5ap3lofgC0yr1XOE+4UQ306IDPAylV7LuwLd9/AE3J7zAK3V3NxNmBoC6SXuw0S3f3sxnw3ygG5wdBI9N6vpkDLMTtJlkBDWwB+Hwf1ZBNlHg0t9QSuUmWRnVSZdrSSgEXI+7CTeQ+NK1agL4xRFsWrqA9oVU0CcxhBEJ7C+IEmcI6oUtz3xZwScZTCnVCiBfJY/srLaPWCqRhnLIAQjSP8CEF1iJ6aR1akqiITFupyrB/jKA5IyAxyuw+A5GgqPAfkkuPaUK8eepqVVcMJh5UpmLa0X3GvwZ4RKSGmIBQVGq03/ojAWeDxxsaI7ggsxRoA01kqIuSDn8QHrA+HfsxsLCNn+SoMtdQgLnNQC2NzRiRHPl9nBqtq4to5jgKv0uRaGeG/nsB/J0cL8pcegYLldo0bEWjLaVJ/C29CDmFr5ihAyb5rPA5IsraLGnci522AWmBqiQ2ZS591LcQQT3AsFRqXrhSuNF5rlLUSeKrIEShqIwQE+NyV5TuiqY9eMR67gigjlMLptBIekCBWQFsseHwWeckcTUEUdxV5pWMq0EISBVY/LYhZR6MNlgGmzjimAg9CMRdDv8vk+Z6HXByXTfsg41qlA7LZoxgx2Zu6jMxoaAikxBqIpjATYp8yITeecI0ijJ2EpNqdLE/GhKUEJIWebul7IKduaeeqJVQetYHOhuOjGygKcHrPYyRfxHqQSdSIokOzlOeDFsfSWUVg6Z7A0gDTrLRoUIniTgFeAAsHIu4EiIIO+Qzdp+An+r6bqGNqCQTBOBEIskN6dbMCWDbpboShtpUBqcwG/P7sZu7JLxDCYgmQDyzJCUld+4o4OCKNo7yHcYGmvuTpT2gKaA3TZLHYSo6QFFD5mWhuUPsh6T+ESRq2bhAw50iqyCkVIam0PBmE1lVy24smQihtR62UbpskUrhfq0Hul3s/4MXsBIJpLo+JWGPBoRZrXmMNuO0an6k2RqhkUmx2dN6hcQaLWDUpxXQZv/4gw8iabgSXWAmzbzCLUwLK/0xfIYAtNK88oOKOTxNIChyjJazKFOVOOHHwuB+SY0y4a1qTPCmoPIrROM2S++/QiCcUzvw3ojISz3P/DXbmBo4iGp6SDkUnujY/YfhzdpBTjKUJQz0x2ojQVJos+XIpCumZcOKIOF94zMhdUXJUl+iKQPd4MiQF/SW7o1CPIStW51kdsPxRwhDg7Yiu6DSiL0/BTZCMmC8fEI78mznDI766FqI4usokpUBZTMzIpnncKgyfYsMLlHKE6BB7Rgm2SED+9YxLSMae5zgJWVHumGSgAQ6XNPUJpjuQhOTFFJZZ3OxirvOEq1RGuKpwyjWBzES4ikgSzzZBW4GsvonGWHPi47r9PKPPdEyq6VP/6FqvYkchzKzenAZ7jFkVntETuIo9nad82NMxQGAiyRrFsQI4QTCbnoVNQ1b/cU4b8VXEcAGlwfdFKUnwT0eWg5Sk+FmBs8CrUx3l1eRZ2hjKupHRglAdn6hzYQqzpCP+gChReRKJGRcpAqBi6tPq+1yazf6OJ7wYGA7eQ9fiPVSG2INkk80A0MRRGsobx2m+wGlHtJVmeVsiThn4FKdUbIpTEG1lEq2xwFpXiaAfoSxRA0RvssMln9cAgV15rikpNcDk9JlAWkm5rFg64Du5jstGX0MCRJL0PHaUZyWs9cxjzwSshYhgABpIFUDNfFR4RiJtPlQBBMvIsRb0iH1EWOtGtuFYBUxFIpmoCojielUoDPVjJTCpriAcPETXkY1R2VXhKa2qeaxzcFuMluvAZ5pYnqVq1SXiAiwTaxRDXCdIhUiW1wLTV3heD2Ql7i4xb5jlGdfrmiDxh5xiaEhfrQliEgIw8rV3/8/i0hhLEmqsTtjjeI+nbZjAXOiJafAzzHXDqV/O6Kuh/8kepVUCO05n516zIlZDI87Fa4GhTnjrshqY+dWMIotqfoCVOZYC9DGgmLxq0yO3cl3sJn6MVwVRkLhF6nGLdKMm+Pv5mDjWAi8TS25w14s6enS8IH/J8SrVe1KjVcxEqeyiDqg07D33rd+rHX65CsgaiLGA7hfuFHM4onpaQHxFys1rQDxYuLZ8SelXiGvTK4BalNBCHv48m2rxul1y/uWYA0jMtGAsdtyB04jXAdEGijx9Upk3c1E94LlajpgwHytytVTjozzJjfunGiBLQFUSrwGqLAlT/bkCyMq3MRWieHDt2OI6Ay9FCBurgFiPtznCnraE/02MF3ciMuVABvCxLyJTACzols+xfzKiou+UhqlR25KoAQJRyM42zmuAmGXiKnBVA0zNUGVi9dnE3Vcn0/iNXTmZH/p9jq6A6kn1/mN15pNvf6T2MlojHcIpq2XNCbAjskLm8RhQYKvknFpUDyzUDR+7HvL+l+WxqO9yF4zVQBKVOlEhXg8MS4l4Q9QDQ+MacUhRNTDZ2pySJgmRm3Dh8FwcHmFmOSUldRbhzr+fi3QNL9rRIPJ00Y6GY00wZY1nFcGrjTDn1UBmcQeO3vYr1cDf28mT+emeCVHLSYs6BJK6IUGaGw/4EXMOqVIt8R1VIuRPie8SXBLoHU+TXOG+JIdEu05OSAo0m+d5WIMnFDHPQ9adxauCQ24FkqqCv5hVZOn7p86Q1HVWi6VXOE8VQSbAOMafItl2syL4+9kXlZt4YXR5DZO7W6xvUg0zsUh0tofiiKIy36/pEZb6Bvb6eg0w86/sQzsFA6yEydcofv1DJZBys0nI6r/Oc5snVgvIDB6OJyfBC51YDdKRvMfVSt05hb49NYtFlcASig/W8dx3UceLqoC8kgeQgSp5eo/7sxs1QK3qDyyAQxyDeROOwUyZMFiEn5JtqcBQogJY1Psi2hAY6srGcvR0ner5IXKK6n7gGwg5nVf+iCkOZkQTKn+JibE4akrb7vWjHIb1I89+3JWekrs+ZlMJOX1iVusKN6GHp13p7GnMpZn2padjjagCCHgC0BM8J8YQ6OlGFuG8CpiKLxanLVfxIiH4rqgKmFBDQKdNEXLyXoxY2emHJwASyrO39sYdEdT1DnxRBUxcX7wGiDuvc9yq86SBys7rgBmef8fdBV+vA2Z+bR/PX0aeJ3ZcVVhpd3r6rrxzDHXDbV/O6mshvXZzI4oU7UnPJFbJcManmp/KCDsp1zW/38sJRrvSk3dLxfalJ+WoTjvTE7HgRc1PiBDYIAJ9wMWbFb9fza6coyfwLbF0hXCqoI/CqSL+TY82M2JXekq8eWt/uc996bcrhV+v+v0rO9GS0qrA/z9U/m7tUP/Pdl6KmpxxsX0ObOIrXU8v815UCaI8LCImzLQGIg97vledsZtPifofu0BX2rH2xx4T0JXyx9of2Nx0jAXYXexWT86zj4/4mtf92BPHWSB5fMf6dU3Ai+JPBDiIi0X8+ff71e9nAe9nAe9nAe9nAe9nAe9nAe9nATPy/Szg/Szg/SzgtZ+/nwXMyPezgLTi+1nA+1nA+1nA+1nA+1nA+1nA+1nA+1nA+1nA+1lA+X4W8H4W8H4W8H4WMDWnfT8L+D91FrDb7bJJmQc3afvqJXimloobbml3u93SAVJz2Ymli2T0kIGN5iGp8MasqPCWUhsR/RKhdT7runHaFKt0O7guZ5KsZT5apr85DwckTTrclokwgvnj3UInSmOVgTJd56qJXfOYOhllET8p45/um3Hx3z41VqmwGpt8bw+N9ydfCXxkPTVMw0dwN6VKRE7sx+twKxX84PmHG+eivB/v6cWpHaNz84+JpcxlXQgFPKxR4gf06lg644mfyUDD6f/ZjkfCkr4bDPr5s/9FYxLlD3lYi+dnRP6QjDIuN7a5Fe3LKazFuYb6bZvp/TBGjQwBzQkX00ah6aIWcB4j3Vr4onT8q6e+SD7+wnj55gvjleKDlNVOf1TewYdo+6DFXgAvadmrF8nncuqDfP3yeDX/UPiF98XnE94Xr9BL3UEJFDcgGvALEw8WHH+TvPK+86Ib/2xnn3AVfKGULW4OvJm4H3VU7WSjvmBavLvzIdAen65dvLeeXj6PL5KnV8eH01ngdDb2CO+CZOAb56fBCtZpZH+HcVlVecjKsTf4y+eckx6yaox1Sv6KdYhULvlWkP6eaYX/yZf8T4NgsyURoThy6gXT7D9D58HezB7cnb20R0tvu/Od7cPIwyhuF8yWW5QI2x95Y+ihwj+MTFGHv/8XXS8a+FdW7BVOKCK/IknHa0f6K6dr7jaLi/CRgvoIZNGerR1fTPlvpOfq/4C4lgFNesj/gu4q12KQSJkL0YA1BefKsg18b+FELF97a6TJZLZcXlyyBetHQAOk1ZVMrGbjMQ6TqJ3n+ovq3yGbYch/S/IzhT1TUjX5f9iI8SgrJShp7vtKCl9P/w8K3Tv7b1L+Hw==",
    "w": 452.2500000000001,
    "h": 117.45000000000005,
    "aspect": "fixed",
    "title": "GS1 Barcode 128"
  },
  {
    "xml": "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",
    "w": 452.2500000000001,
    "h": 117.45000000000005,
    "aspect": "fixed",
    "title": "Barcode 128"
  },
  {
    "xml": "jVHbbsMgDP0a3ilI6/NItj7taR8woeAFVAMRcZv07+cEtmqTKk0ykn18jm8I3cX1VOzk37IDFPpF6K7kTNWLaweIQsnghO6FUpKfUK8Psoc9KydbINF/BKoKrhYvUBFxNGwf0ZYzFHHsN9s5M92wcQhWrm48RR64P7A7U8ln6DLmwkjKiZnmMyD+gSyGMXE48HxcXpsrFAqDxeeWiMG5rY1ZfCB4n+yw9Vz4QIyVfEkOttElR210LgDrw/V3qO1+ghyByo0pS3DkK+OpXkh6CKNvKt0wO9d4/FHeb8lOO+d3eP+2PffrV78A",
    "w": 60,
    "h": 30,
    "aspect": "fixed",
    "title": "Marker"
  }
];

export class Editor {


  private drawIoWindow: Window | null = null;
  private currentTemplate: string | null = null;
  private currentListener: any | null = null;

  openEditor(template: Template,
             templateService: TemplateService) {
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
          template.svg = atob(msg.data.replace('data:image/svg+xml;base64,', ''));
          template.markers = [];

          const regex = /{{ *([a-zA-Z0-9\._]+)[^{]*}}/g;

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
            }
          } else {
            this.save(template, templateService);
          }
        } else if (msg.event == 'prompt') {

          var xmlDoc = new DOMParser().parseFromString(template.xml || '', 'application/xml');
          var diagrams = xmlDoc.getElementsByTagName('diagram');
          const node: any = diagrams[0].children.item(0)?.outerHTML;
          // @ts-ignore
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
          }

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
    templateService.update(template).finally();
    this.drawIoWindow?.postMessage(JSON.stringify({
      action: 'status',
      message: 'Saved',
      modified: false
    }), '*');
  }
}
