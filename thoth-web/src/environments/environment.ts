// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
function makeid(length: number) {
  let result = '';
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const charactersLength = characters.length;
  let counter = 0;
  while (counter < length) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
    counter += 1;
  }
  return result;
}

const state = makeid(32);
export const environment = async  () => ({
  production: false,
  apiUrl: 'http://localhost:8080',
  oauth: {
    logoutUrl: 'https://glc-thoth.auth.eu-west-1.amazoncognito.com/logout?' +
      'client_id=3fg6d0vhqbag36ad21vge8vmim&' +
      'logout_uri=' + window.location.origin + '/logout' + '&' +
      'identity_provider=COGNITO&scope=email%20profile%20openid%20aws.cognito.signin.user.admin&state='+state+'&'+
      'response_type=token',
    loginUrl: 'https://glc-thoth.auth.eu-west-1.amazoncognito.com/login?' +
      'client_id=3fg6d0vhqbag36ad21vge8vmim&' +
      'redirect_uri=' + window.location.origin + '/token-redirect&' +
      'identity_provider=COGNITO&scope=email%20profile%20openid%20aws.cognito.signin.user.admin&state='+state+'&'+
      'response_type=token'
  }
});

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
