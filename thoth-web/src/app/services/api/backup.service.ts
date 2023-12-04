import {Injectable} from '@angular/core';
import {Page} from "../../common/utils/fetchUtils";
import {Client} from "../../common/types/client";
import {environment} from "../../../environments/environment";
import {Backup} from "../../common/types/backup";
import {AuthenticatedService} from "./authenticated.service";

@Injectable({
  providedIn: 'root'
})
export class BackupService extends AuthenticatedService {

  constructor() {
    super()
  }

  async getBackups(resourceType: 'TEMPLATE' | 'DATASOURCE' | 'RENDERER',
                   resourceId: string): Promise<Backup[]> {
    return fetch((await environment()).apiUrl + '/backup/' + resourceType + '/' + resourceId, {
      method: 'GET',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async doBackup(resourceType: 'TEMPLATE' | 'DATASOURCE' | 'RENDERER',
                 resourceId: string): Promise<Backup> {
    return fetch((await environment()).apiUrl + '/backup/' + resourceType + '/' + resourceId, {
      method: 'POST',
      headers: this.getHeaders()
    }).then(async r => {
      if (!r.ok) {
        throw await r.json()
      }
      return await r.json()
    });
  }

  async delete(identifier: string) {
    return fetch((await environment()).apiUrl + '/backup/' + identifier, {
      method: 'DELETE',
      headers: this.getHeaders()
    })
  }

  async findById(backupId: string): Promise<Backup> {
    return fetch((await environment()).apiUrl + '/backup/' + backupId, {
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
