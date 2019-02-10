export class UserService {

    constructor($state, $http) {
        this.$state = $state;
        this.$http = $http;
    }

    set user(user) {
        this._user = user;
    }

    get user() {
        return this._user;
    }

    deconnecter() {
        this.$http.post("/api/user/logout").then(() => {
            this.user = undefined;
            document.location.href = "/";
        }).catch(e => {
            alert("Déconnexion impossible");
            console.warn(e);
        });
    }

    getCurrentUser() {
        return this.$http.get("/api/user/current")
            .then(resp => {
                if (resp.status === 200) {
                    this._user = resp.data;
                    return this._user;
                }
            }).catch((e) => {
                alert(e);
            });
    }

}

export default "UserService"
