export class User {
    public userId: string;
    public username: string;
    public email: string;
    public profileImageUrl: string;
    public lastLoginDateDisplay: Date;
    public joinDate: Date;
    public role:string;
    public authorities: [];
    public active: boolean;
    public notLocked: boolean;

    constructor() {
        this.username = '';
        this.email = '';
        this.active = false;
        this.notLocked = false;
        this.role = '';
        this.authorities = [];
    }   
}