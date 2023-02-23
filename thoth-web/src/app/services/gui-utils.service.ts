import { Injectable } from '@angular/core';
import {ModalController} from "@ionic/angular";
import {PrintRequestModalComponent} from "../components/modals/print-request-modal/print-request-modal.component";
import {ParametersFormComponent} from "../components/modals/parameters-form/parameters-form.component";

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

  async parametersFormModal(title: string, parameters: string[]){
    const m = await this.modalController.create({
      component: ParametersFormComponent,
      componentProps: {
        parameters,
        title
      }
    });
    await m.present();
    return await m.onDidDismiss();
  }
}
