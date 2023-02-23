import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";

@Component({
  selector: 'app-parameters-form',
  templateUrl: './parameters-form.component.html',
  styleUrls: ['./parameters-form.component.scss'],
})
export class ParametersFormComponent implements OnInit {

  @Input()
  parameters: string[] = []

  data: any = {}

  @Input()
  title: string = ""

  constructor(private modalController: ModalController) { }

  ngOnInit() {}

  cancel() {
    return this.modalController.dismiss(null, 'cancel');
  }

  submit() {
    return this.modalController.dismiss(this.data);
  }
}
