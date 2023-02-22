import {SecuredResource} from "./secured-resource";

export interface ApiKey extends SecuredResource {
  apiKey: string;
  id: string,
  name: string,
  expiry: number
}
