import { Injectable } from '@angular/core';
import {Page} from "../../common/utils/fetchUtils";
import {Client} from "../../common/types/client";
import {environment} from "../../../environments/environment";
import {AuthenticatedService} from "./authenticated.service";

@Injectable({
  providedIn: 'root'
})
export class UtilsService extends AuthenticatedService{

  constructor() {
    super();
  }

  async userAutocomplete(value: string): Promise<string[]>{
    return fetch((await environment()).apiUrl + '/utils/user-autocomplete?value=' + value, {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async organizationAutocomplete(value: string): Promise<string[]>{
    return fetch((await environment()).apiUrl + '/utils/organization-autocomplete?value=' + value, {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }
}
