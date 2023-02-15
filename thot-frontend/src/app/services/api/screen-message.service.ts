import {Injectable} from '@angular/core';
import {AlertController, ToastController} from "@ionic/angular";

@Injectable({
  providedIn: 'root'
})
export class ScreenMessageService {

  constructor(private toastController: ToastController,
              private alertController: AlertController) {
  }

  async showDone() {
    const toast = await this.toastController.create({
      message: 'Done',
      duration: 3000,
      icon: 'checkmark',
      color: 'success'
    });
    return toast.present()
  }

  async showError(e: any) {
    const toast = await this.alertController.create({
      message: e.message,
      buttons: ['OK'],
      header: 'Error',
      cssClass: 'error-alert'
    });
    return toast.present()
  }

  async showDeleteAlert(deleteFun: () => Promise<any>) {
    const alert = await this.alertController.create({
      header: "Delete item",
      message: "Are you sure?",
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
        },
        {
          text: 'OK',
          role: 'confirm',
          handler: async () => {
            try {
              await deleteFun();
              await this.showDone();
            }catch (e){
              return this.showError(e);
            }
          },
        },
      ],
    })
    return alert.present();
  }
}
