import {Injectable} from '@angular/core';
import {Template} from "../common/types/template";
import {Datasource} from "../common/types/datasource";
import {Renderer} from "../common/types/renderer";
import {environment} from "../../environments/environment";
import {Page} from "../common/utils/fetchUtils";

@Injectable({
  providedIn: 'root'
})
export class RendererService {

  constructor() {
  }

  async create(name: string | undefined, selectedTemplate: Template | undefined, selectedDatasource: Datasource[] | undefined, associationMap: any): Promise<Renderer> {
    return fetch(environment.apiUrl + '/renderer/', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=utf-8'
      },
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

  async findAll(): Promise<Page<Renderer>> {
    return fetch(environment.apiUrl + '/renderer/', {
      method: 'GET'
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async findById(identifier: string | null): Promise<Renderer> {
    return fetch(environment.apiUrl + '/renderer/' + identifier, {
      method: 'GET'
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async delete(identifier: string) {
    return fetch(environment.apiUrl + '/renderer/' + identifier, {
      method: 'DELETE'
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async update(renderer: Renderer | null) {
    return fetch(environment.apiUrl + '/renderer/' + renderer?.id, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json;charset=utf-8'
      },
      body: JSON.stringify(
        {
          name: renderer?.name,
          associationMap: renderer?.associationMap
        }
      )
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async print(renderer: string, parameters: any, clientIdentifier: string, printService: any) {
    return fetch(environment.apiUrl + '/renderer/' + renderer + '/print', {
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
