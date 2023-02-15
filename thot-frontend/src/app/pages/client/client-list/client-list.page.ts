import { Component, OnInit } from '@angular/core';
import {Client} from "../../../common/types/client";
import {ClientService} from "../../../services/api/client.service";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.page.html',
  styleUrls: ['./client-list.page.scss'],
})
export class ClientListPage extends ListPage<Client> implements OnInit {

  constructor(private clientService: ClientService) {
    super(clientService)
  }

  ionViewWillEnter(){
   return super.loadPageData();
  }
  ngOnInit() {
  }

}
