export interface SecuredResource {
  allowedUserList: {sid: string, permission:string}[];
  allowedOrganizationList: {sid: string, permission:string}[];
}
