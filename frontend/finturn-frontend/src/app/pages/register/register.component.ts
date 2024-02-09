import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthenticationService } from '../../service/authentication.service';
import { NotificationService } from '../../service/notification.service';
import { Router } from '@angular/router';
import { NotificationType } from '../../enum/notification-type.enum';
import { HttpErrorResponse } from '@angular/common/http';
import { User } from '../../model/user';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit, OnDestroy {
  private subscription: Subscription[] = [];

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    // if (this.authenticationService.isLoggedIn()) {
    //   this.router.navigateByUrl('/home');
    // } 
  }

  onRegister(user: User) {
    this.subscription.push(this.authenticationService.register(user).subscribe(
      (response: User) => {
        const message = `New account was created for ${response.username}.`
        this.sendNotification(NotificationType.SUCCESS, message);
      },
      (errorResponse: HttpErrorResponse) => {
        const message = errorResponse.error.message;
        this.sendNotification(NotificationType.ERROR, message);
      }
    ))
  }

  ngOnDestroy(): void {
    this.subscription.forEach(sub => sub.unsubscribe());
  }

  private sendNotification(notificationType: NotificationType, message: string) {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occured, please try again')
    }
    }

    



}
