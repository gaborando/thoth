import {Template} from "./template";
import {Datasource} from "./datasource";

export interface Renderer {
  id: string;
  name: string;

  template: Template

  datasourceProperties: Datasource[]

  associationMap: any
}
