import { Injectable } from '@angular/core';
import {Page} from "../../common/utils/fetchUtils";
import {Template} from "../../common/types/template";
import {environment} from "../../../environments/environment";
import {Client} from "../../common/types/client";
import {AuthenticatedService} from "./authenticated.service";

@Injectable({
  providedIn: 'root'
})
export class ClientService extends AuthenticatedService {

  constructor() {
    super()
  }


  async findAll(): Promise<Page<Client>>{
    return fetch(environment.apiUrl + '/client/', {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async deleteById(identifier: string) {
    return fetch(environment.apiUrl + '/client/' + identifier, {
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
