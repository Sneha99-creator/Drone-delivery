import { Component } from '@angular/core';
import {AppService} from "./app.service";
import {Subject} from "rxjs";
import {debounceTime, distinctUntilChanged} from "rxjs/operators";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'ui';
  inputString: string;
  stringLength: number;
  error: any;
  paths: any;
  txtQueryChanged: Subject<string> = new Subject<string>();

  constructor(private appService: AppService) {
    // don't issue too many queries, wait for input to cease
    this.txtQueryChanged
      .pipe(debounceTime(250), distinctUntilChanged())
      .subscribe(query => {
        this.getServerPaths(query);
      });
  }

  getStringLength(str) {
    this.appService.getStringLength(str)
      .subscribe((response) => {
        this.stringLength = response.length;
      }, (err) => {
        this.error = err;
    })
  }

  getServerPaths(str) {
    this.appService.getServerPaths(str)
      .subscribe((response) => {
        this.paths = response.paths;
      }, (err) => {
        this.error = err;
      })
  }

  inputChange($event) {
    this.txtQueryChanged.next($event);
  };


}
