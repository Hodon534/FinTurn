import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { SubSink } from 'subsink';
import { User } from '../../../../../model/user';
import { AuthenticationService } from '../../../../../service/authentication.service';
import { NotificationService } from '../../../../../service/notification.service';
import { UserService } from '../../../../../service/user.service';
import { Router } from '@angular/router';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { NotificationType } from '../../../../../enum/notification-type.enum';
import { Role } from '../../../../../enum/role.enum';
import { CustomHttpResponse } from '../../../../../model/custom-http-response';

@Component({
  selector: 'app-users-table',
  templateUrl: './users-table.component.html',
  styleUrl: './users-table.component.css'
})
export class UsersTableComponent implements OnInit, OnDestroy{
  private subs = new SubSink();
  private titleSubject = new BehaviorSubject<string>('Users');
  public titleAction$ = this.titleSubject.asObservable();
  public users: User[];
  public user: User;
  public refreshing: boolean;
  private subscription: Subscription[] = [];
  public selectedUser: User;
  public fileName: string | null;
  public profileImage: File;
  public editUser: User = new User();
  private currentUsername: string;


  constructor(private router: Router, private authenticationService: AuthenticationService, private userService: UserService, private notificationService: NotificationService) {}


  ngOnInit(): void {
    this.user = this.authenticationService.getUserFromLocalCache();
    this.getUsers(true);  }

  public changeTitle(title: string){
    this.titleSubject.next(title);
  }

  public getUsers(showNotification: boolean) {
    this.refreshing = true;
    this.subs.add(
      this.userService.getUsers().subscribe(
        (response: User[])=> {
          this.userService.addUsersToLocalCache(response)
          this.users = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} user(s) loaded successfully.`)
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;

        }
      )
    );
  }

public onSelectUser(selectedUser: User) {
  this.selectedUser = selectedUser;
  this.clickButton('view-user-modal');
}
//todo !! Image doesn't delete after submitting new user, how to handle that
 public onProfileImageChange(event: Event) {
    if (event) {
      const files = (event.target as  HTMLInputElement).files;
      if (files) {
        this.fileName = files[0].name;
        this.profileImage = files[0];
      }
    }
} 
/**
 * click the button to add new user, hidden button in the form
 */
public saveNewUser() {
  this.clickButton('new-user-save');
}

public onAddNewUser(userForm: NgForm) {
  const formData = this.userService.createUserFormData("", userForm.value, this.profileImage);

  /**
   * since it's a call to the backend, we have to subsribe to it
   */
  this.subs.add(
    this.userService.addUser(formData).subscribe(
      (response: User) => {
        this.clickButton('new-user-close');
        /**
         * if i don't want a notification to pop up
         */
        this.getUsers(false);
        this.fileName = null;
        /**
         * instead of file = null, i had an issue with that
         */
        this.profileImage = new File([],'');
        userForm.reset();
        this.sendNotification(NotificationType.SUCCESS, 
          `${response.firstName} ${response.lastName} added successfully`);
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        this.refreshing = false;
        this.fileName = null;
        this.profileImage = new File([],'');

      }))
}

/**
 * Issue with updating users - profile image is copied from previous user that was edited
 */

public onUpdateUser() {
  const formData = this.userService.createUserFormData(this.currentUsername, this.editUser, this.profileImage);
  this.subs.add(
    this.userService.updateUser(formData).subscribe(
      (response: User) => {
        this.clickButton('closeEditModalButton');
        this.getUsers(false);
        this.fileName = null;
        this.profileImage = new File([],'');
        this.sendNotification(NotificationType.SUCCESS, 
          `${response.firstName} ${response.lastName} updated successfully`);
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        this.refreshing = false;
        this.fileName = null;
        this.profileImage = new File([],'');

      }))
}

  public searchUsers(searchTerm: string) {
    const results: User[] = [];
    for (const user of this.userService.getUsersFromLocalCache()) {
      if (user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.email.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
          user.userId.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) {
            results.push(user);
          }
    }
    this.users = results;
    //if (results.length === 0 || !searchTerm) {
    if (results.length === 0) {

      this.sendNotification(NotificationType.ERROR, 'No users had been found');

      //this.users = this.userService.getUsersFromLocalCache();
    }
  }

  public onUpdateCurrentUser(user: User) {
    this.refreshing = true
    this.currentUsername = this.authenticationService.getUserFromLocalCache().username;
    const formData = this.userService.createUserFormData(this.currentUsername, user, this.profileImage);
    this.subs.add(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.authenticationService.addUserToLocalCache(response);
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = new File([],'');
          this.sendNotification(NotificationType.SUCCESS, 
            `${response.firstName} ${response.lastName} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
          this.fileName = null;
          this.profileImage = new File([],'');
  
        }))
  }

  public onUpdateProfileImage() {
    const formData = new FormData();
    formData.append('username', this.user.username);
    formData.append('profileImage', this.profileImage);
    this.subs.add(
      this.userService.updateProfileImge(formData).subscribe(
        (event: HttpEvent<any>) => {
          this.reportUploadProgress(event);
          
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }

        ));
  }
  private reportUploadProgress(event: HttpEvent<any>) {
    switch (event.type) {
      case HttpEventType.Response:
          if (event.status == 200) {
            this.user.profileImageUrl = `${event.body.profileImageUrl}?time=${new Date().getTime()}`;
            this.sendNotification(NotificationType.SUCCESS, 
              `${event.body.firstName}\'s profile image updated successfully`);
            break;
          } else {
            this.sendNotification(NotificationType.ERROR, 
              `Unable to upload image. Please try again`);
              break;
          } default:
          `Finished all processes`;
    }
  }

  public updateProfileImage(): void {
    this.clickButton('profile-image-input');
  }

  public onLogOut() {
    this.authenticationService.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS, `You've been successfully logged out`);
  }

  /**
   * ngForm instead of string value just to make "emailForm.reset()", otherwise it wouldn't be possible
   */

  //Mapping for incorrect email doesn't work
  public onResetPassword(emailForm: NgForm) {
    this.refreshing = true;
    const emailAddress = emailForm.value['reset-password-email'];
    this.subs.add(
      this.userService.resetPassword(emailAddress).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refreshing = false;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, errorResponse.error.message)
          this.refreshing = false;

        },
        () => emailForm.reset()
      )
    )
  }

  public onDeleteUser(username: string) {
    this.subs.add(
      this.userService.deleteUser(username).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, `${response.message}`)
          this.getUsers(false);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message)
          this.refreshing = false;
        }
      )
    )
  }

  public onEditUser(editUser: User) {
    this.editUser = editUser;
    this.currentUsername = editUser.username;
    this.clickButton('openUserEdit');
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  public get isAdmin(): boolean {
    return this.getUserRole() == Role.ADMIN || this.getUserRole() == Role.SUPER_ADMIN;
  }

  public get isManager(): boolean {
    return this.isAdmin || this.getUserRole() == Role.MANAGER;
  }

  public get isAdminOrManager(): boolean {
    return this.isAdmin || this.isManager;
  }

  private sendNotification(notificationType: NotificationType, message: string) {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occured, please try again')
    }
    }

private clickButton(buttonId: string) {
  document.getElementById(buttonId)?.click();
}

ngOnDestroy(): void {
  this.subs.unsubscribe();
}

}
