export default function AuthInterceptor($state) {
  return {
    responseError: (rejection) => {
      switch (rejection.status) {
        case 403 :
          $state.go("home.login");
          break;
      }
      return rejection;
    }
  }

}
