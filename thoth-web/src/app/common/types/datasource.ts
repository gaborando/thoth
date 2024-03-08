import {SecuredResource} from "./secured-resource";
import {Renderer} from "./renderer";

export interface Datasource extends SecuredResource{
  id: string;
  type: string;
  name: string;

  properties: { name: string, helper: string }[]
  usages: Renderer[]

  parameters: string[]

}
