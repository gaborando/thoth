import {SecuredResource} from "./secured-resource";

export interface Secret extends SecuredResource {
  name: string
}
