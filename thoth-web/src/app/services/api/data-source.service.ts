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

  async checkParameters(type: string, datasourceParameters: any, parameters: any) {
    const request = {... datasourceParameters};
    request.parameters = parameters;
    console.log(request)
    return fetch(environment.apiUrl + '/datasource/check/' + type, {
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

  saveParameters(type: string, jdbcDatasourceParameters: any) {
    return fetch(environment.apiUrl + '/datasource/' + type, {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify(jdbcDatasourceParameters)
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async findAll(): Promise<Page<Datasource>> {
    return fetch(environment.apiUrl + '/datasource/', {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async findById(identifier: string | null) {
    return fetch(environment.apiUrl + '/datasource/' + identifier, {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  updateParameters(type: string, request: any) {
    return fetch(environment.apiUrl + '/datasource/' + type + '/' + request.id, {
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
    return fetch(environment.apiUrl + '/datasource/' + id, {
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
