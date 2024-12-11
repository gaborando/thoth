import {Component, OnInit} from '@angular/core';
import {Client} from "../../../common/types/client";
import {ClientService} from "../../../services/api/client.service";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {IonModal, ModalController} from "@ionic/angular";
import {SharingOptionsComponent} from "../../../components/sharing-options/sharing-options.component";
import {GuiUtilsService} from "../../../services/gui-utils.service";

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.page.html',
  styleUrls: ['./client-list.page.scss'],
})
export class ClientListPage extends ListPage<Client> implements OnInit {
  selectedClient: Client | undefined;

  constructor(private clientService: ClientService,
              private screenMessageService: ScreenMessageService,
              private modalController: ModalController,
              public guiUtils: GuiUtilsService) {
    super(clientService)
  }

  ngOnInit() {
  }

  unregister(client: Client) {
    return this.screenMessageService.showDeleteAlert(async () => {
      await this.clientService.deleteById(client.identifier);
      this.elements = this.elements?.filter(c => c.identifier !== client.identifier)
    })
  }

  updateSharingOptions(modal: IonModal) {
    if(!this.selectedClient){
      return ;
    }
    const c = this.selectedClient;
    return this.screenMessageService.loadingWrapper(async () => {
      await this.clientService.update(c);
      await this.screenMessageService.showDone();
      return modal.dismiss();
    })
  }

    protected readonly navigator = navigator;
}
