import { Injectable } from '@angular/core';
import {Page} from "../../common/utils/fetchUtils";
import {Template} from "../../common/types/template";
import {environment} from "../../../environments/environment";
import {Client} from "../../common/types/client";

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  constructor() { }


  async findAll(): Promise<Page<Client>>{
    return fetch(environment.apiUrl + '/client/', {
      method: 'GET'
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }
}
