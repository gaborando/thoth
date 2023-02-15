import { Component, OnInit } from '@angular/core';
import {Client} from "../../../common/types/client";
import {ClientService} from "../../../services/api/client.service";

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.page.html',
  styleUrls: ['./client-list.page.scss'],
})
export class ClientListPage implements OnInit {

  clients: Client[]  = []
  constructor(private clientService: ClientService) { }

  async ionViewWillEnter(){
    this.clients = (await this.clientService.findAll()).content;
  }
  ngOnInit() {
  }

}
