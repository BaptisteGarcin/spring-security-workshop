export default class LoginCtrl {
    constructor($state, UserService, $stateParams) {
        this.userService = UserService;
    }

    $onInit() {
        this.logged();
    }

    async login() {

    }

    logged() {
        this.userService.getCurrentUser()
            .then((user) => {
                if (user) {
                    document.location.href="/livredor";
                }

            })
    }
}
