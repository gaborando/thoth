import {SecuredResource} from "./secured-resource";

export interface Client extends SecuredResource{
  identifier: string,
  name: string,
  printServices: string[]

}
