import { Injectable } from '@angular/core';
import {ModalController} from "@ionic/angular";
import {PrintRequestModalComponent} from "../components/modals/print-request-modal/print-request-modal.component";

@Injectable({
  providedIn: 'root'
})
export class GuiUtilsService {

  constructor(private modalController: ModalController) { }

  async printRequestModal(){
    const modal = await this.modalController.create({
      component: PrintRequestModalComponent
    });
    await modal.present();
    return modal.onWillDismiss()
  }
}
