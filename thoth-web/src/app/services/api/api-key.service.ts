import { Injectable } from '@angular/core';
import {AuthenticatedService} from "./authenticated.service";
import {Page} from "../../common/utils/fetchUtils";
import {Client} from "../../common/types/client";
import {environment} from "../../../environments/environment";
import {ApiKey} from "../../common/types/api-key";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";

@Injectable({
  providedIn: 'root'
})
export class ApiKeyService extends AuthenticatedService implements DataFetcher<ApiKey>{
  constructor() {
    super()
  }

  async findAll(page = 0, filter = '', signal: AbortSignal,  size = 10): Promise<Page<ApiKey>>{
    return fetch((await environment()).apiUrl + '/api-key/?page=' + page + '&size=' + size, {
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
               userSID: string,
               organizationSID: string,
               expiry: number | null): Promise<ApiKey>{
    return fetch((await environment()).apiUrl + '/api-key/', {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify({
        name,
        expiry,
        userSID,
        organizationSID
      })
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async delete(id: string): Promise<any>{
    return fetch((await environment()).apiUrl + '/api-key/' + id, {
      method: 'DELETE',
      headers: this.getHeaders(),
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.text()
    });
  }
}
