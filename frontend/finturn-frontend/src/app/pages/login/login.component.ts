import { Component, OnDestroy, OnInit } from '@angular/core';
import { SubSink } from 'subsink';
import { AuthenticationService } from '../../service/authentication.service';
import { NotificationService } from '../../service/notification.service';
import { Router } from '@angular/router';
import { User } from '../../model/user';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { HeaderType } from '../../enum/header-type.enum';
import { NotificationType } from '../../enum/notification-type.enum';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, OnDestroy {
  private subsription: Subscription[] = [];

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService
  ) {

  }
  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.router.navigateByUrl('/admin-panel');
    } else {
      this.router.navigateByUrl('/login');
    }
  }

  onLogin(user: User) {
    this.subsription.push(
      this.authenticationService.login(user).subscribe(
        (response: HttpResponse<User>) => {
          const token = response.headers.get(HeaderType.JWT_TOKEN);
          if (token !== null) {
            this.authenticationService.saveToken(token);
          }
          const user = response.body;
          if (user !== null) {
            this.authenticationService.addUserToLocalCache(user);
          }
          this.router.navigateByUrl('/home');
        },
        (errorResponse: HttpErrorResponse) => {
          const message = errorResponse.error.message;
          this.sendErrorNotification(NotificationType.ERROR, message);
        }
      )
    )
  }

  ngOnDestroy(): void {
    this.subsription.forEach(sub => sub.unsubscribe()); 
  }

  private sendErrorNotification(notificationType: NotificationType, message: string) {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occured, please try again');
    }
  }

}
