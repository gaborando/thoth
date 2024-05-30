import {Component, OnInit} from '@angular/core';
import {environment} from "../environments/environment";
import {version} from "../environments/version";
import {NavigationEnd, NavigationStart, Router} from "@angular/router";

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
  showMenu = true;

  openAccess = true;

  environment: any = {}
  v = version;

  constructor(private router: Router) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.showMenu = !event.url.startsWith('/form') && event.url !== '/login';
        console.log(this.showMenu)
      }
    });
  }


  async ngOnInit() {
    this.environment = await environment();
    this.openAccess = !(this.environment.oauth)
  }

  async logout() {
    localStorage.removeItem("access_token")
    window.location.replace((await environment()).oauth?.logoutUrl);
  }
}
