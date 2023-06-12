import {Component, Input, OnInit} from '@angular/core';
import {ModalController} from "@ionic/angular";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";

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

  @Input()
  resourceId: string = ""
  jsonData = "";

  constructor(private modalController: ModalController) { }

  ngOnInit() {
    const last = localStorage.getItem("last_form_" + this.resourceId);
    if(last){
      this.data = JSON.parse(last);
    }else{
      for (let parameter of this.parameters) {
        this.data[parameter] = "";
      }
    }
    this.jsonData = JSON.stringify(this.data, null, 4);
  }

  cancel() {
    return this.modalController.dismiss(null, 'cancel');
  }

  submit() {
    localStorage.setItem("last_form_" + this.resourceId, JSON.stringify(this.data));
    return this.modalController.dismiss(this.data);
  }

  codeLoaded(component: CodemirrorComponent) {
  }

  jsonChanged($event: Event) {
    try {
      const d = JSON.parse(this.jsonData);
      this.data = d;
    }catch (e){
    }
  }
}
