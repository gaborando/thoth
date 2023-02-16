import { Injectable } from '@angular/core';
import {Template} from "../../common/types/template";
import {find} from "rxjs";
import {environment} from "../../../environments/environment";
import {Page} from "../../common/utils/fetchUtils";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";

@Injectable({
  providedIn: 'root'
})
export class TemplateService implements DataFetcher<Template>{

  constructor() {

  }

  async findAll(): Promise<Page<Template>>{
    return fetch(environment.apiUrl + '/template/', {
      method: 'GET'
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async create(name: string) {
    return fetch(environment.apiUrl + '/template/', {
      method: 'POST',
      headers: {
        'Content-Type':'application/json;charset=utf-8'
      },
      body: JSON.stringify({name})
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async update(template: Template) {

    return fetch(environment.apiUrl + '/template/' + template.id, {
      method: 'PUT',
      headers: {
        'Content-Type':'application/json;charset=utf-8'
      },
      body: JSON.stringify(template)
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async delete(id: string) {
    return fetch(environment.apiUrl + '/template/' + id, {
      method: 'DELETE'
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.text()
    });
  }

  async print(template: string, parameters: any, clientIdentifier: string, printService: any) {
    return fetch(environment.apiUrl + '/template/' + template + '/print', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=utf-8'
      },
      body: JSON.stringify({
        parameters,
        clientIdentifier,
        printService
      })
    }).then(async r => {
      if (!r.ok) {
        throw await r.text()
      }
      return await r.text()
    });
  }
}
