import { ServerModule } from '@angular/platform-server';

import { AppComponent } from './app.component';
import { NgModule } from '@angular/core';
import { AppModule } from './app.module';

@NgModule({
  imports: [
    AppModule,
    ServerModule,
  ],
  bootstrap: [AppComponent],
})
export class AppServerModule {}
