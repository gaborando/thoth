import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {firstValueFrom, last, lastValueFrom} from "rxjs";
import {environment} from "../environments/environment";

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit{

  public appPages = [
    { title: 'Templates', url: '/template-list', icon: 'create' },
    { title: 'Datasource', url: '/datasource-list', icon: 'server' },
    { title: 'Renderers', url: '/renderer-list', icon: 'document-text' },
    { title: 'Clients', url: '/client-list', icon: 'print' },
  ];
  showMenu = false;
  constructor() {}


  ngOnInit(): void {
    this.showMenu = window.location.pathname !== '/login'
  }

  logout() {
    localStorage.removeItem("access_token")
    window.location.replace('https://' + environment.cognito.Auth.oauth.domain + '/logout?' +
      'client_id=' + environment.cognito.Auth.userPoolWebClientId + '&' +
      'logout_uri=' + environment.cognito.Auth.oauth.redirectSignOut + '&' +
      'redirect_uri=' + environment.cognito.Auth.oauth.redirectSignIn + '&' +
      'identity_provider=COGNITO&scope=email%20profile%20openid%20aws.cognito.signin.user.admin&state=uepdg8YumFOCc0L41pM81oHsjbm0Gm77&'+
      'response_type=token');
  }
}
