// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  cognito: {
    Auth: {
      // REQUIRED only for Federated Authentication - Amazon Cognito Identity Pool ID
      identityPoolId: 'urn:amazon:cognito:sp:eu-west-1_VfkzwcRqJ',

      // OPTIONAL - Amazon Cognito User Pool ID
      userPoolId: 'eu-west-1_VfkzwcRqJ',

      // REQUIRED - Amazon Cognito Region
      region: 'eu-west-1',


      // OPTIONAL - Amazon Cognito Web Client ID (26-char alphanumeric string)
      userPoolWebClientId: '3fg6d0vhqbag36ad21vge8vmim',

      oauth: {
        domain: 'glc-thoth.auth.eu-west-1.amazoncognito.com',
        scope: ['email', 'profile', 'openid', 'aws.cognito.signin.user.admin'],
        redirectSignIn: window.location.origin + '/token-redirect',
        redirectSignOut: window.location.origin + '/logout',
        responseType: 'token' // or 'token', note that REFRESH token will only be generated when the responseType is code
      }
    }
  }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
