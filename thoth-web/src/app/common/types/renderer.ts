import {Template} from "./template";
import {Datasource} from "./datasource";
import {SecuredResource} from "./secured-resource";

export interface Renderer extends SecuredResource {
  id: string;
  name: string;

  template: Template

  datasourceProperties: Datasource[]

  associationMap: any
}
