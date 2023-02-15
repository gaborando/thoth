import { Injectable } from '@angular/core';
import {Template} from "../common/types/template";
import {find} from "rxjs";
import {environment} from "../../environments/environment";
import {Page} from "../common/utils/fetchUtils";

@Injectable({
  providedIn: 'root'
})
export class TemplateService {

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

  private guidGenerator() {
    const S4 = () => {
      return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    };
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
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
      return await r.json()
    });
  }
}
