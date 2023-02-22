import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import {environment} from "../../environments/environment";
@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate {


  constructor() {
  }

  parseJwt (token: string) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {


    const params = new URL(window.location.toString().replace("#","?"));
    if(params.searchParams.get("access_token")){
      localStorage.setItem("access_token", params.searchParams.get("access_token") || "")
      window.history.pushState(null,
        "", window.location.pathname);
    }

    const currentToken = localStorage.getItem("access_token");
    if(!currentToken || ((this.parseJwt(currentToken || "").exp * 1000) < new Date().getTime())){
      window.location.replace('https://' + environment.cognito.Auth.oauth.domain + '/login?' +
        'client_id=' + environment.cognito.Auth.userPoolWebClientId + '&' +
        // 'logout_uri=' + environment.cognito.Auth.oauth.redirectSignOut + '&' +
        'redirect_uri=' + environment.cognito.Auth.oauth.redirectSignIn + '&' +
        'identity_provider=COGNITO&scope=email%20profile%20openid%20aws.cognito.signin.user.admin&state=uepdg8YumFOCc0L41pM81oHsjbm0Gm77&'+
        'response_type=token');
      return false;
    }
    return true;
  }

}
