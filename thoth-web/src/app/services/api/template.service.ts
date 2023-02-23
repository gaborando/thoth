import { Injectable } from '@angular/core';
import {Template} from "../../common/types/template";
import {find} from "rxjs";
import {environment} from "../../../environments/environment";
import {Page} from "../../common/utils/fetchUtils";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";
import {AuthenticatedService} from "./authenticated.service";

@Injectable({
  providedIn: 'root'
})
export class TemplateService extends AuthenticatedService implements DataFetcher<Template>{

  constructor() {
    super()
  }

  async findAll(page = 0): Promise<Page<Template>>{
    return fetch((await environment()).apiUrl + '/template/?page='+page, {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async create(name: string) {
    return fetch((await environment()).apiUrl + '/template/', {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify({name})
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async update(template: Template) {

    return fetch((await environment()).apiUrl + '/template/' + template.id, {
      method: 'PUT',
      headers: this.postHeaders(),
      body: JSON.stringify(template)
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async delete(id: string) {
    return fetch((await environment()).apiUrl + '/template/' + id, {
      method: 'DELETE',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.text()
    });
  }

  async print(template: string, parameters: any, clientIdentifier: string, printService: any, copies: number) {
    return fetch((await environment()).apiUrl + '/template/' + template + '/print', {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify({
        parameters,
        clientIdentifier,
        printService,
        copies
      })
    }).then(async r => {
      if (!r.ok) {
        throw await r.text()
      }
      return await r.text()
    });
  }

  async findById(identifier: string | null): Promise<Template> {
    return fetch((await environment()).apiUrl + '/template/'+identifier, {
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
