
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgModule } from '@angular/core';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { UsersComponent } from './pages/admin-panel/users/users.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { TabsComponent } from './pages/admin-panel/components/tabs/tabs.component';
import { DashboardComponent } from './pages/admin-panel/dashboard/dashboard.component';
import { SettingsComponent } from './pages/admin-panel/settings/settings.component';
import { TopPanelComponent } from './pages/admin-panel/users/components/top-panel/top-panel.component';
import { UsersTableComponent } from './pages/admin-panel/users/components/users-table/users-table.component';
import { AdduserComponent } from './pages/admin-panel/users/components/modals/adduser/adduser.component';
import { EdituserComponent } from './pages/admin-panel/users/components/modals/edituser/edituser.component';
import { ViewuserComponent } from './pages/admin-panel/users/components/modals/viewuser/viewuser.component';
import { DeleteuserComponent } from './pages/admin-panel/users/components/modals/deleteuser/deleteuser.component';
import { TestsidebarComponent } from './components/testsidebar/testsidebar.component';
import { FormsModule } from '@angular/forms';
import { NotificationService } from './service/notification.service';
import { AuthenticationGuard } from './guard/authentication.guard';
import { AuthenticationService } from './service/authentication.service';
import { UserService } from './service/user.service';
import { AuthInterceptor } from './interceptor/auth.interceptor';
import { NotificationModule } from './notification.module';
import { HomeComponent } from './pages/home/home.component';


@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    LoginComponent,
    RegisterComponent,
    ResetPasswordComponent,
    UsersComponent,
    TabsComponent,
    DashboardComponent,
    SettingsComponent,
    TopPanelComponent,
    UsersTableComponent,
    AdduserComponent,
    EdituserComponent,
    ViewuserComponent,
    DeleteuserComponent,
    TestsidebarComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatIconModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    NotificationModule
  ],
  providers: [
    NotificationService,
    AuthenticationGuard,
    AuthenticationService,
    UserService,
    {
      provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true
    }],
    bootstrap: [AppComponent]
    //provideClientHydration()
})
export class AppModule { }
