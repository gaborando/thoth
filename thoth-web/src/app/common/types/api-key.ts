import {SecuredResource} from "./secured-resource";

export interface ApiKey extends SecuredResource {
  apiKey: string;
  id: string,
  name: string,
  userSID: string;
  organizationSID: string,
  expiry: number
}
