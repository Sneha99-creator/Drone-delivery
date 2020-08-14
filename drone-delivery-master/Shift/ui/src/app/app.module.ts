import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {environment} from "../environments/environment";
import {HttpClient, HttpClientModule} from "@angular/common/http";

function fwURL() {
  var url = document.URL;
  var index = url.indexOf('#');
  if (index < 0) index = url.length;
  index = url.lastIndexOf('/', index);
  if (index < 0) console.error("cannot parse document.URL", url);
  url = url.substring(0, index + 1);
  var matchdata = /^[^:]+:\/\/([^:\/]+)(:[^\/]+)?/.exec(url);
  if (matchdata && matchdata[1] === 'localhost' && matchdata[2] === ':4200')
    url = environment.SERVER_URL;
  return url;
}

if (!window._SERVER_ENDPOINT)
  window._SERVER_ENDPOINT = fwURL();
if (window._SERVER_ENDPOINT.length > 0 && window._SERVER_ENDPOINT.substr(-1) !== '/') {
  console.warn("_SERVER_ENDPOINT needs to end with a slash");
  window._SERVER_ENDPOINT += '/';
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    CommonModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
