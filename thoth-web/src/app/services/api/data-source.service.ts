import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {Datasource} from "../../common/types/datasource";
import {Page} from "../../common/utils/fetchUtils";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";

@Injectable({
  providedIn: 'root'
})
export class DataSourceService implements DataFetcher<Datasource>{

  constructor() { }

  async checkParameters(type: string, datasourceParameters: any, parameters: any) {
    const request = {... datasourceParameters};
    request.parameters = parameters;
    console.log(request)
    return fetch(environment.apiUrl + '/datasource/check/' + type, {
      method: 'POST',
      headers: {
        'Content-Type':'application/json;charset=utf-8'
      },
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
      headers: {
        'Content-Type':'application/json;charset=utf-8'
      },
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
      method: 'GET'
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async findById(identifier: string | null) {
    return fetch(environment.apiUrl + '/datasource/' + identifier, {
      method: 'GET'
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
      headers: {
        'Content-Type':'application/json;charset=utf-8'
      },
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
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.text()
    });
  }
}