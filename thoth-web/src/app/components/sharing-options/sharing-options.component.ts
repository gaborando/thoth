import {Component, Input, OnInit} from '@angular/core';
import {SecuredResource} from "../../common/types/secured-resource";
import {IonModal} from "@ionic/angular";
import {UtilsService} from "../../services/api/utils.service";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-sharing-options',
  templateUrl: './sharing-options.component.html',
  styleUrls: ['./sharing-options.component.scss'],
})
export class SharingOptionsComponent implements OnInit {

  @Input()
  securedResource: SecuredResource  | undefined

  autocompleteData: string[] = []
  autocompleteLoading = false;
  hasAuth = false;
  constructor(private utilsService: UtilsService) { }

  ngOnInit() {
    this.autocompleteData = [];
    environment().then((e: any) => {
      this.hasAuth = !!e.oauth;
    })
  }

  removeAllowedUser(sid: string) {
    if(!this.securedResource){
      return;
    }
    this.securedResource.allowedUserList = this.securedResource?.allowedUserList.filter(s => s !== sid);
  }

  removeAllowedOrganization(sid: string) {
    if(!this.securedResource){
      return;
    }
    this.securedResource.allowedOrganizationList = this.securedResource?.allowedOrganizationList.filter(s => s !== sid);
  }

  addPermission(modal: IonModal, type: any, sid: any) {
    if (type === 'user') {
      this.securedResource?.allowedUserList?.push(sid);
    }else if (type === 'organization') {
      this.securedResource?.allowedOrganizationList?.push(sid);
    }
    return modal.dismiss();
  }

  async fetchAutocomplete($event: any, type: any) {
    this.autocompleteLoading = true;
    try {
      if (type === 'user') {
        this.autocompleteData = await this.utilsService.userAutocomplete($event.detail.value)
      }else if (type === 'organization') {
        this.autocompleteData = await this.utilsService.organizationAutocomplete($event.detail.value)
      }
    }finally {
      this.autocompleteLoading = false;
    }
  }
}
