import {Injectable} from '@angular/core';
import {Template} from "../../common/types/template";
import {Datasource} from "../../common/types/datasource";
import {Renderer} from "../../common/types/renderer";
import {environment} from "../../../environments/environment";
import {Page} from "../../common/utils/fetchUtils";
import {DataFetcher} from "../../common/utils/service-patterns/data-fetcher";
import {AuthenticatedService} from "./authenticated.service";

@Injectable({
  providedIn: 'root'
})
export class RendererService extends AuthenticatedService implements DataFetcher<Renderer>{

  constructor() {
    super()
  }

  async create(name: string | undefined, selectedTemplate: Template | undefined, selectedDatasource: Datasource[] | undefined, associationMap: any): Promise<Renderer> {
    return fetch(environment.apiUrl + '/renderer/', {
      method: 'POST',
      headers: this.postHeaders(),
      body: JSON.stringify({
        name: name,
        template: selectedTemplate?.id,
        datasource: selectedDatasource?.map(d => d.id),
        associationMap
      })
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });

  }

  async findAll(page = 0): Promise<Page<Renderer>> {
    return fetch(environment.apiUrl + '/renderer/?page=' + page, {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async findById(identifier: string | null): Promise<Renderer> {
    return fetch(environment.apiUrl + '/renderer/' + identifier, {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async delete(identifier: string) {
    return fetch(environment.apiUrl + '/renderer/' + identifier, {
      method: 'DELETE',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.text()
    });
  }

  async update(renderer: Renderer | null) {
    return fetch(environment.apiUrl + '/renderer/' + renderer?.id, {
      method: 'PUT',
      headers: this.postHeaders(),
      body: JSON.stringify(
        {
          name: renderer?.name,
          associationMap: renderer?.associationMap,
          allowedOrganizationList: renderer?.allowedOrganizationList,
          allowedUserList: renderer?.allowedUserList
        }
      )
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async print(renderer: string, parameters: any, clientIdentifier: string, printService: any, copies: number) {
    return fetch(environment.apiUrl + '/renderer/' + renderer + '/print', {
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
}
