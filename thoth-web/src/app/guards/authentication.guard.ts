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
    if(!currentToken){
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
