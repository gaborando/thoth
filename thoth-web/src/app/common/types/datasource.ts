import {SecuredResource} from "./secured-resource";

export interface Datasource extends SecuredResource{
  id: string;
  type: string;
  name: string;

  properties: string[]

  parameters: string[]

}
