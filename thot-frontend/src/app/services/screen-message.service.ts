import { Injectable } from '@angular/core';
import {ToastController} from "@ionic/angular";

@Injectable({
  providedIn: 'root'
})
export class ScreenMessageService {

  constructor(private toastController: ToastController) { }

  async showDone() {
    const toast = await this.toastController.create({
      message: 'Done',
      duration: 3000,
      icon: 'checkmark',
      color: 'success'
    });
    return toast.present()
  }
}
