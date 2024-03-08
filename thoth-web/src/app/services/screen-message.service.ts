import {Injectable} from '@angular/core';
import {AlertController, LoadingController, ToastController} from "@ionic/angular";

@Injectable({
  providedIn: 'root'
})
export class ScreenMessageService {

  constructor(private toastController: ToastController,
              private alertController: AlertController,
              private loadingController: LoadingController) {
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

  async loadingWrapper(fun: () => Promise<any>, showError = false) {
    const loading = await this.loadingController.create();
    await loading.present();
    try {
      return await fun();
    } catch (e) {
      if (showError) {
        await loading.dismiss();
        await this.showError(e);
      }
    } finally {
      await loading.dismiss();
    }
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
            await alert.dismiss();
            try {
              await deleteFun();
              await this.showDone();
            } catch (e) {
              return this.showError(e);
            }
          },
        },
      ],
    })
    return alert.present();
  }
}
