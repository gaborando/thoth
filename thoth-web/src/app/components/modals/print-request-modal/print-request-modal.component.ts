import { Component, OnInit } from '@angular/core';
import {ModalController} from "@ionic/angular";
import * as stream from "stream";
import {ClientService} from "../../../services/api/client.service";
import {Client} from "../../../common/types/client";

@Component({
  selector: 'app-print-request-modal',
  templateUrl: './print-request-modal.component.html',
  styleUrls: ['./print-request-modal.component.scss'],
})
export class PrintRequestModalComponent implements OnInit {

  clients: Client[] = []
  request: {
    client: Client | null,
    printService: string[],
    copies: number
  } = {
    client: null,
    printService: [],
    copies: 1
  }

  constructor(private modalCtrl: ModalController,
              private clientService: ClientService) {}

  cancel() {
    return this.modalCtrl.dismiss(null, 'cancel');
  }

  confirm() {
    return this.modalCtrl.dismiss(this.request, 'confirm');
  }

  async ngOnInit() {
    this.clients = (await this.clientService.findAll()).content;
  }


}
