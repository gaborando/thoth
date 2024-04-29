import { Injectable } from '@angular/core';
import {Page} from "../../common/utils/fetchUtils";
import {ApiKey} from "../../common/types/api-key";
import {environment} from "../../../environments/environment";
import {AuthenticatedService} from "./authenticated.service";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";
import {Secret} from "../../common/types/secret";
import {SecuredResource} from "../../common/types/secured-resource";

@Injectable({
  providedIn: 'root'
})
export class SecretService  extends AuthenticatedService implements DataFetcher<Secret>{

  constructor() {
    super()
  }

  async findAll(page = 0, filter = '', signal: AbortSignal): Promise<Page<Secret>>{
    return fetch((await environment()).apiUrl + '/secret/?page=' + page, {
      method: 'GET',
      headers: this.getHeaders(),
      signal
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async create(name: string,
               value: string): Promise<Secret>{
    return fetch((await environment()).apiUrl + '/secret/', {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify({
        name,
        value
      })
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async delete(id: string): Promise<any>{
    return fetch((await environment()).apiUrl + '/secret/' + id, {
      method: 'DELETE',
      headers: this.getHeaders(),
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.text()
    });
  }

  async updateSecrets(secret: Secret) {
    return fetch((await environment()).apiUrl + '/secret/' + secret.name, {
      method: 'PUT',
      headers: this.postHeaders(),
      body: JSON.stringify({
        allowedUserList: secret.allowedUserList,
        allowedOrganizationList: secret.allowedOrganizationList,
      })
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.text()
    });
  }
}
