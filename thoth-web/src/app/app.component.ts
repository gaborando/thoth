import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {firstValueFrom, last, lastValueFrom} from "rxjs";
import {environment} from "../environments/environment";

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {

  public appPages = [
    {title: 'Templates', url: '/template-list', icon: 'create'},
    {title: 'Datasource', url: '/datasource-list', icon: 'server'},
    {title: 'Renderers', url: '/renderer-list', icon: 'document-text'},
    {title: 'Clients', url: '/client-list', icon: 'print'},
  ];
  showMenu = false;

  openAccess = true;

  constructor() {
  }


  async ngOnInit() {
    this.showMenu = window.location.pathname !== '/login';
    this.openAccess = !(await environment()).oauth
  }

  async logout() {
    localStorage.removeItem("access_token")
    window.location.replace((await environment()).oauth?.logoutUrl);
  }
}
