import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {Datasource} from "../../common/types/datasource";
import {Page} from "../../common/utils/fetchUtils";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";
import {AuthenticatedService} from "./authenticated.service";

@Injectable({
  providedIn: 'root'
})
export class DataSourceService extends AuthenticatedService implements DataFetcher<Datasource>{

  constructor() {
    super()
  }

  async checkParameters(type: string, definition: any, parameters: any) {
    const request = {... definition};
    request.parameters = parameters;
    return fetch((await environment()).apiUrl + '/datasource/check/' + type, {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify(request)
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async saveParameters(type: string, definition: any) {
    return fetch((await environment()).apiUrl + '/datasource/' + type, {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify(definition)
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async findAll(page = 0, filter = '', signal: AbortSignal): Promise<Page<Datasource>> {
    return fetch((await environment()).apiUrl + '/datasource/?page=' +page+'&filter='+filter, {
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

  async findById(identifier: string | null) {
    return fetch((await environment()).apiUrl + '/datasource/' + identifier, {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json();
    });
  }

  async updateParameters(type: string, request: any) {
    return fetch((await environment()).apiUrl + '/datasource/' + type + '/' + request.id, {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify(request)
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async deleteById(id: string) {
    return fetch((await environment()).apiUrl + '/datasource/' + id, {
      method: 'DELETE',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.text()
    });
  }
}
